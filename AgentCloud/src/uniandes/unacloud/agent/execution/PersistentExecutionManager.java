package uniandes.unacloud.agent.execution;

import static uniandes.unacloud.common.utils.UnaCloudConstants.ERROR_MESSAGE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;

import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.communication.upload.UploadImageTask;
import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.execution.entities.Execution;
import uniandes.unacloud.agent.execution.entities.ImageStatus;
import uniandes.unacloud.agent.execution.task.ExecutorService;
import uniandes.unacloud.agent.platform.PlatformFactory;
import uniandes.unacloud.common.com.UnaCloudAbstractResponse;
import uniandes.unacloud.common.com.messages.InvalidOperationResponse;
import uniandes.unacloud.common.com.messages.exeo.ExecutionAddTimeMessage;
import uniandes.unacloud.common.com.messages.exeo.ExecutionRestartMessage;
import uniandes.unacloud.common.com.messages.exeo.ExecutionSaveImageMessage;
import uniandes.unacloud.common.com.messages.exeo.ExecutionSaveImageResponse;
import uniandes.unacloud.common.com.messages.exeo.ExecutionStartResponse.ExecutionState;
import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Responsible for managing executions. This class is responsible to schedule execution startups and
 * stops. The process is: given a execution and a time t, this class ensures that this execution is going to be turn on for a time t
 * The execution only is stopped when the time t is burnt-out or when the user sends a request to stop it. If this physical machine is turned off,
 * then the next time the physical machine starts the execution will be powered on.<br/>
 * To schedule the execution turn off we used a Timer that manage a collection of TimerTask objects, each timer task is responsible for
 * stopping one execution
 * @author Clouder
 */
public class PersistentExecutionManager {

    /**
     * The file that contains the powered executions and its execution times
     */
    private static final String executionsFile = "executions.txt";
    
    /**
     * Execution hash map, contains list of execution
     */
    private static final Map<Long,Execution> executionList = new TreeMap<>();
        
    /**
     * Timer used to schedule shutdown events
     */
    private static Timer timer = new Timer();
   
    /**
     * Stops an execution and removes it representing execution object
     * @param executionId
     * @param checkTime 
     */
    public static void removeExecution(long executionId, boolean checkTime) {
    	Execution execution = executionList.remove(executionId);
		if (execution != null && (!checkTime || System.currentTimeMillis() > execution.getShutdownTime())) {
			execution.getImage().stopAndUnregister();
		}
		saveData();
    }
    
    /**
     * Stops execution
     * @param executionId
     */
    public static void stopExecution(long executionId) {
    	Execution execution = executionList.get(executionId);
		if (execution != null) {
			execution.getImage().stop();
		}
    }
    
    /**
     * Unregister execution from platforms
     * @param executionId
     */
    public static void unregisterExecution(long executionId) {
    	Execution execution = executionList.get(executionId);
		if (execution != null) {
			execution.getImage().unregister();
		}
    }

    /**
     * Delete directory sent by params
     * @param f directory or file
     */
	public static void cleanDir(File f) {
		if (f.isDirectory()) 
			for (File r : f.listFiles()) 
				cleanDir(r);
		f.delete();
	}
	
    /**
     * Restarts the given execution
     * @return response to server
     */
    public static UnaCloudAbstractResponse restartMachine(ExecutionRestartMessage restartMessage) {
    	Execution execution = executionList.get(restartMessage.getExecutionId());
        try {
        	execution.getImage().restartExecution();
        } catch (PlatformOperationException ex) {
            try {
				ServerMessageSender.reportExecutionState(restartMessage.getExecutionId(), ExecutionStateEnum.FAILED, ex.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        saveData();
        return null;
    }

    /**
     * Starts and configures an execution. this method must be used by other methods to configure, start and schedule an execution
     * @param execution to be configured
     * @param started if execution should be started
     * @return result message
     */
    public static String startUpMachine(Execution execution,boolean started){
    	execution.setShutdownTime(System.currentTimeMillis() + execution.getExecutionTime().toMillis());
    	try {
	        try {
	            if (!started) execution.getImage().startExecution();
	            executionList.put(execution.getId(),execution);
	            timer.schedule(new Scheduler(execution.getId()), new Date(execution.getShutdownTime() + 100l));
	            ServerMessageSender.reportExecutionState(execution.getId(), ExecutionStateEnum.DEPLOYING, "Starting execution");
	            if (new ExecutionStateViewer(execution.getId(), execution.getMainInterface().getIp()).check())
	            	execution.getImage().setStatus(ImageStatus.LOCK);
	        } catch (PlatformOperationException e) {
	        	e.printStackTrace();
	        	execution.getImage().stopAndUnregister();
	        	ServerMessageSender.reportExecutionState(execution.getId(), ExecutionStateEnum.FAILED, e.getMessage());
	            return ERROR_MESSAGE + e.getMessage();
	        }
        } catch (Exception e) {
			e.printStackTrace();
			execution.getImage().setStatus(ImageStatus.FREE);
		}
        saveData();
        return "";
    }
   

    /**
     * Extends the time that the execution must be up
     * @param timeMessage message with execution id and time to be modified
     * @return unacloud response
     */
    public static UnaCloudAbstractResponse extendsVMTime(ExecutionAddTimeMessage timeMessage) {
    	Execution execution = executionList.get(timeMessage.getExecutionId());
    	execution.setExecutionTime(timeMessage.getExecutionTime());
    	execution.setShutdownTime(System.currentTimeMillis() + timeMessage.getExecutionTime().toMillis());
    	timer.schedule(new Scheduler(execution.getId()), new Date(execution.getShutdownTime() + 100l));
    	saveData();
        return null;
    }
    
    /**
     * Saves the current state of executions and images on this node.  
     */
    private static void saveData() {
    	try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(executionsFile));) {
        	oos.writeObject(executionList);
        } catch(Exception e){
        	e.printStackTrace();
        }
    }
    /**
     * Loads and validates status of all executions saved in file
     *
     */
    public static void refreshData() {
    	try {
			List<Long> ids = ImageCacheManager.getCurrentImages();
			System.out.println("There are images " + ids.size());
			loadData();
			List<Execution> removeExecutions = PlatformFactory.validateExecutions(executionList.values());
			for (Execution execution: removeExecutions)	
				if (execution.getImage().getStatus() != ImageStatus.STARTING)
					removeExecution(execution.getId(), false);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Returns a list of id executions that currently are running,
     * not return images in state STARTING (testing running)
     * @return list of execution ids
     */
    public static List<Long> returnIdsExecutions() {
    	if (executionList.values().size() == 0) 
    		return new ArrayList<Long>();
    	try {
    		refreshData();
        	List<Long> ids = new ArrayList<Long>();
        	for (Execution execution: executionList.values())
        		if (execution.getImage().getStatus() != ImageStatus.STARTING)
        			ids.add(execution.getId());
        	return ids;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Long>();
		}    	
    }
    
    /**
     * Loads data from file to map
     */
    @SuppressWarnings("unchecked")
	private static void loadData() {
    	Map<Long,Execution> executions = null;
    	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(executionsFile))) {
        	executions = (Map<Long,Execution>) ois.readObject();
        	if (executions != null)
        		for (Execution execution:executions.values()) 
        			if (execution != null)
        				//execution.getImage().stopAndUnregister();
        				executionList.put(execution.getId(), execution);
            else saveData();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    /**
     * Sends an image copied to server
     * @param message
     * @return unacloud response
     */
    public static UnaCloudAbstractResponse sendImageCopy(ExecutionSaveImageMessage message) {
    	try {
    		Execution execution = executionList.get(message.getExecutionId());
    		ExecutionSaveImageResponse response = new ExecutionSaveImageResponse();
    		if (execution != null && execution.getImageId() == message.getImageId()) {
    			System.out.println("Start copy service with token " + message.getTokenCom());
				response.setMessage("Copying image");
				response.setState(ExecutionState.COPYNG);
				ExecutorService.executeBackgroundTask(new UploadImageTask(execution,message.getTokenCom()));
            } else {
				response.setMessage(UnaCloudConstants.ERROR_MESSAGE + " Execution doesn't exist");
				response.setState(ExecutionState.FAILED);
			}
    		return response;
		} catch (Exception e) {			
			return new InvalidOperationResponse(UnaCloudConstants.ERROR_MESSAGE + e);
		}       
    }
}
