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
import uniandes.unacloud.agent.communication.send.VirtualMachineStateViewer;
import uniandes.unacloud.agent.communication.upload.UploadImageVirtualMachineTask;
import uniandes.unacloud.agent.execution.entities.VirtualMachineExecution;
import uniandes.unacloud.agent.execution.entities.VirtualMachineImageStatus;
import uniandes.unacloud.agent.execution.task.ExecutorService;
import uniandes.unacloud.agent.hypervisor.HypervisorFactory;
import uniandes.unacloud.agent.hypervisor.HypervisorOperationException;
import uniandes.unacloud.common.com.UnaCloudAbstractResponse;
import uniandes.unacloud.common.com.messages.InvalidOperationResponse;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineAddTimeMessage;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineRestartMessage;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineSaveImageMessage;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineSaveImageResponse;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineStartResponse.VirtualMachineState;
import uniandes.unacloud.common.enums.VirtualMachineExecutionStateEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Responsible for managing virtual machine executions. This class is responsible to schedule virtual machine startups and
 * stops. The process is: given a virtual machine and a  time t, this class ensures that this virtual machine is going to be turn on for a time t
 * The virtual machine only is stopped when the time t is burnt-out or when the user sends a request to stop it. If this physical machine is turned off,
 * then the next time the physical machine starts the virtual machine will be powered on.<br/>
 * To schedule the virtual machine turn off we used a Timer that manage a collection of TimerTask objects, each timer task is responsible for
 * stopping one virtual machine
 * @author Clouder
 */
public class PersistentExecutionManager {

    /**
     * The file that contains the powered virtual machines and its execution times
     */
    private static final String executionsFile = "executions.txt";
    
    /**
     * Execution hash map, contains list of execution
     */
    private static final Map<Long,VirtualMachineExecution> executionList=new TreeMap<>();
        
    /**
     * Timer used to schedule shutdown events
     */
    private static Timer timer = new Timer();
   
    /**
     * Stops a virtual machine and removes it representing execution object
     * @param virtualMachineExecutionId
     * @param checkTime 
     */
    public static void removeExecution(long virtualMachineExecutionId,boolean checkTime) {
    	VirtualMachineExecution execution=executionList.remove(virtualMachineExecutionId);
		if(execution!=null&&(!checkTime||System.currentTimeMillis()>execution.getShutdownTime())){
			execution.getImage().stopAndUnregister();
		}
		saveData();
    }
    
    /**
     * Stops execution
     * @param virtualMachineExecutionId
     */
    public static void stopExecution(long virtualMachineExecutionId) {
    	VirtualMachineExecution execution=executionList.get(virtualMachineExecutionId);
		if(execution!=null){
			execution.getImage().stop();
		}
    }
    
    /**
     * Unregister execution from hypervisors
     * @param virtualMachineExecutionId
     */
    public static void unregisterExecution(long virtualMachineExecutionId) {
    	VirtualMachineExecution execution=executionList.get(virtualMachineExecutionId);
		if(execution!=null){
			execution.getImage().unregister();
		}
    }

    /**
     * Delete directory sent by params
     * @param f directory or file
     */
	public static void cleanDir(File f){
		if(f.isDirectory())for(File r:f.listFiles())cleanDir(r);
		f.delete();
	}
    /**
     * Restarts the given virtual machine
     * @return response to server
     */
    public static UnaCloudAbstractResponse restartMachine(VirtualMachineRestartMessage restartMessage) {
    	VirtualMachineExecution execution=executionList.get(restartMessage.getVirtualMachineExecutionId());
        try {
        	execution.getImage().restartVirtualMachine();
        } catch (HypervisorOperationException ex) {
            try {
				ServerMessageSender.reportVirtualMachineState(restartMessage.getVirtualMachineExecutionId(), VirtualMachineExecutionStateEnum.FAILED, ex.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        saveData();
        return null;
    }

    /**
     * Starts and configures a virtual machine. this method must be used by other methods to configure, start and schedule a virtual machine execution
     * @param execution to be configured
     * @param started if execution should be started
     * @return result message
     */
    public static String startUpMachine(VirtualMachineExecution execution,boolean started){
    	execution.setShutdownTime(System.currentTimeMillis()+execution.getExecutionTime().toMillis());
    	try {
	        try {
	            if(!started)execution.getImage().startVirtualMachine();
	            executionList.put(execution.getId(),execution);
	            timer.schedule(new Scheduler(execution.getId()),new Date(execution.getShutdownTime()+100l));
	            ServerMessageSender.reportVirtualMachineState(execution.getId(),VirtualMachineExecutionStateEnum.DEPLOYING,"Starting virtual machine");
	            if(new VirtualMachineStateViewer(execution.getId(),execution.getMainInterface().getIp()).check())
	            	execution.getImage().setStatus(VirtualMachineImageStatus.LOCK);
	        } catch (HypervisorOperationException e) {
	        	e.printStackTrace();
	        	execution.getImage().stopAndUnregister();
	        	ServerMessageSender.reportVirtualMachineState(execution.getId(), VirtualMachineExecutionStateEnum.FAILED, e.getMessage());
	            return ERROR_MESSAGE + e.getMessage();
	        }
        } catch (Exception e) {
			e.printStackTrace();
			execution.getImage().setStatus(VirtualMachineImageStatus.FREE);
		}
        saveData();
        return "";
    }
   

    /**
     * Extends the time that the virtual machine must be up
     * @param timeMessage message with execution id and time to be modified
     * @return unacloud response
     */
    public static UnaCloudAbstractResponse extendsVMTime(VirtualMachineAddTimeMessage timeMessage) {
    	VirtualMachineExecution execution=executionList.get(timeMessage.getVirtualMachineExecutionId());
    	execution.setExecutionTime(timeMessage.getExecutionTime());
    	execution.setShutdownTime(System.currentTimeMillis()+timeMessage.getExecutionTime().toMillis());
    	timer.schedule(new Scheduler(execution.getId()),new Date(execution.getShutdownTime()+100l));
    	saveData();
        return null;
    }
    
    /**
     * Saves the current state of virtual machine executions and virtual machine images on this node.  
     */
    private static void saveData(){
    	try(ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(executionsFile));){
        	oos.writeObject(executionList);
        }catch(Exception e){}
    }
    /**
     * Loads and validates status of all executions saved in file
     *
     */
    public static void refreshData(){
    	try {
			List<Long>ids = ImageCacheManager.getCurrentImages();
			System.out.println("There are images "+ids.size());
			loadData();
			List<VirtualMachineExecution> removeExecutions = HypervisorFactory.validateExecutions(executionList.values());
			for(VirtualMachineExecution execution: removeExecutions){		
				if(execution.getImage().getStatus()!=VirtualMachineImageStatus.STARTING){
					removeExecution(execution.getId(),false);
				}								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Returns a list of id executions that currently are running,
     * not return images in state STARTING (testing running)
     * @return list of execution ids
     */
    public static List<Long> returnIdsExecutions(){
    	if(executionList.values().size()==0)return new ArrayList<Long>();
    	try {
    		refreshData();
        	List<Long> ids = new ArrayList<Long>();
        	for(VirtualMachineExecution execution: executionList.values())if(execution.getImage().getStatus()!=VirtualMachineImageStatus.STARTING)ids.add(execution.getId());
        	return ids;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Long>();
		}    	
    }
    
    /**
     * Load data from file to map
     */
    @SuppressWarnings("unchecked")
	private static void loadData(){
    	Map<Long,VirtualMachineExecution> executions=null;
    	try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(executionsFile))){
        	executions=(Map<Long,VirtualMachineExecution>)ois.readObject();
        	if(executions!=null){
        		for(VirtualMachineExecution execution:executions.values())if(execution!=null){
    				//execution.getImage().stopAndUnregister();
        			executionList.put(execution.getId(), execution);
        		}
            }else saveData();
        } catch (Exception ex){}
    }
    /**
     * Sends an image copied to server
     * @param message
     * @return unacloud response
     */
    public static UnaCloudAbstractResponse sendImageCopy(VirtualMachineSaveImageMessage message){
    	try {
    		VirtualMachineExecution execution=executionList.get(message.getVirtualMachineExecutionId());
    		VirtualMachineSaveImageResponse response = new VirtualMachineSaveImageResponse();
    		if(execution!=null&&execution.getImageId()==message.getImageId()){
    			System.out.println("Start copy service with token "+message.getTokenCom());
				response.setMessage("Copying image");
				response.setState(VirtualMachineState.COPYNG);
				ExecutorService.executeBackgroundTask(new UploadImageVirtualMachineTask(execution,message.getTokenCom()));
            }else{
				response.setMessage(UnaCloudConstants.ERROR_MESSAGE+" Execution doesn't exist");
				response.setState(VirtualMachineState.FAILED);
			}
    		return response;
		} catch (Exception e) {			
			return new InvalidOperationResponse(UnaCloudConstants.ERROR_MESSAGE+e);
		}       
    }
}