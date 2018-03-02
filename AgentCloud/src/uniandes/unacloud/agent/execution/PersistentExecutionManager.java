package uniandes.unacloud.agent.execution;

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

import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.domain.ImageStatus;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.net.upload.UploadImageTask;
import uniandes.unacloud.agent.platform.PlatformFactory;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionAddTimeMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionSaveImageMessage;
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
    private static final Map<Long, Execution> executionList = new TreeMap<>();
        
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
		if (execution != null)
			execution.getImage().unregister();
    }

	
    /**
     * Restarts the given execution
     * @return response to server
     */
    public static UnaCloudResponse restartMachine(long executionId) {
    	UnaCloudResponse response = new UnaCloudResponse();
    	Execution execution = executionList.get(executionId);
        try {
        	execution.getImage().restartExecution();
        	response.setMessage(UnaCloudConstants.SUCCESSFUL_OPERATION);
        	response.setState(ExecutionProcessEnum.SUCCESS);
        } 
        catch (PlatformOperationException ex) {
            try {
				ServerMessageSender.reportExecutionState(executionId, ExecutionProcessEnum.FAIL, ex.getMessage());
			} 
            catch (Exception e) {
				e.printStackTrace();
			}
            response.setMessage(ex.getMessage());
        	response.setState(ExecutionProcessEnum.FAIL);
        }
        saveData();
        return response;
    }

    /**
     * Starts and configures an execution. this method must be used by other methods to configure, start and schedule an execution
     * @param execution to be configured
     * @param started if execution should be started
     * @return result message
     */
    public static void startUpMachine(Execution execution, boolean started) {
    	execution.setShutdownTime(System.currentTimeMillis() + execution.getExecutionTime().toMillis());
    	try {
	        try {
	        	ServerMessageSender.reportExecutionState(execution.getId(), ExecutionProcessEnum.SUCCESS, "Starting execution");
	            if (!started) 
	            	execution.getImage().startExecution();
	            executionList.put(execution.getId(), execution);
	            timer.schedule(new Scheduler(execution.getId()), new Date(execution.getShutdownTime() + 100l));
	            
	            if (new ExecutionStateViewer(execution.getId(), execution.getMainInterface().getIp()).check())
	            	execution.getImage().setStatus(ImageStatus.LOCK);
	        } 
	        catch (PlatformOperationException e) {
	        	e.printStackTrace();
	        	execution.getImage().stopAndUnregister();
	        	ServerMessageSender.reportExecutionState(execution.getId(), ExecutionProcessEnum.FAIL, e.getMessage());
	        }
        } 
    	catch (Exception e) {
			e.printStackTrace();
			execution.getImage().setStatus(ImageStatus.FREE);
		}
        saveData();
    }
   

    /**
     * Extends the time that the execution must be up
     * @param timeMessage message with execution id and time to be modified
     * @return unacloud response
     */
    public static UnaCloudResponse extendsVMTime(ExecutionAddTimeMessage timeMessage) {
    	Execution execution = executionList.get(timeMessage.getExecutionId());
    	execution.setExecutionTime(timeMessage.getExecutionTime());
    	execution.setShutdownTime(System.currentTimeMillis() + timeMessage.getExecutionTime().toMillis());
    	timer.schedule(new Scheduler(execution.getId()), new Date(execution.getShutdownTime() + 100l));
    	saveData();
        return new UnaCloudResponse (UnaCloudConstants.SUCCESSFUL_OPERATION, ExecutionProcessEnum.SUCCESS);
    }
    
    /**
     * Saves the current state of executions and images on this node.  
     */
    private static void saveData() {
    	try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(executionsFile));) {
        	oos.writeObject(executionList);
        } 
    	catch(Exception e){
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
		} 
    	catch (Exception e) {
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
		} 
    	catch (Exception e) {
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
        } 
    	catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    /**
     * Sends an image copied to server
     * @param message
     * @return unacloud response
     */
    public static UnaCloudResponse sendImageCopy(ExecutionSaveImageMessage message) {
    	try {
    		Execution execution = executionList.get(message.getExecutionId());
    		UnaCloudResponse response = new UnaCloudResponse();
    		if (execution != null && execution.getImageId() == message.getImageId()) {
    			System.out.println("Start copy service with token " + message.getTokenCom());
				response.setMessage("Copying image");
				response.setState(ExecutionProcessEnum.SUCCESS);
				ExecutorService.executeBackgroundTask(new UploadImageTask(message.getTokenCom(), execution));
            } 
    		else {
				response.setMessage(UnaCloudConstants.ERROR_MESSAGE + " Execution doesn't exist");
				response.setState(ExecutionProcessEnum.FAIL);
			}
    		return response;
		} catch (Exception e) {			
			return new UnaCloudResponse(UnaCloudConstants.ERROR_MESSAGE + e, ExecutionProcessEnum.FAIL);
		}       
    }
}
