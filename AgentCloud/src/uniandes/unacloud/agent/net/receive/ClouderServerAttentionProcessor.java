package uniandes.unacloud.agent.net.receive;

import static uniandes.unacloud.common.utils.UnaCloudConstants.ERROR_MESSAGE;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import uniandes.unacloud.agent.execution.AgentManager;
import uniandes.unacloud.agent.execution.ExecutorService;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.task.StartExecutionTask;
import uniandes.unacloud.agent.execution.task.StopExecutionTask;
import uniandes.unacloud.agent.host.resources.FileManager;
import uniandes.unacloud.agent.host.system.OSFactory;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.net.tcp.AbstractTCPSocketProcessor;
import uniandes.unacloud.common.net.tcp.message.AgentMessage;
import uniandes.unacloud.common.net.tcp.message.ClientMessage;
import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;
import uniandes.unacloud.common.net.tcp.message.PhysicalMachineOperationMessage;
import uniandes.unacloud.common.net.tcp.message.TCPMessageEnum;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.common.net.tcp.message.agent.ClearImageFromCacheMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionAddTimeMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionSaveImageMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionStartMessage;
import uniandes.unacloud.common.net.tcp.message.pmo.PhysicalMachineTurnOnMessage;


/**
 * Responsible for attending or discarding a
 * Server operation request in a thread
 */
public class ClouderServerAttentionProcessor extends AbstractTCPSocketProcessor {
	
	/**
	 * Creates a new server attention thread
	 * @param socket
	 */
	public ClouderServerAttentionProcessor(Socket socket) {
		super(socket);
	}

	@Override
	public void processMessage(Socket socket) throws Exception {
		try (Socket s = socket; ObjectInputStream ois = new ObjectInputStream(s.getInputStream()); ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream())) {
			ClientMessage clouderServerRequest = (ClientMessage) ois.readObject();
            System.out.println("I received message: " + clouderServerRequest);
            if (clouderServerRequest.getType().equals(TCPMessageEnum.EXECUTION_OPERATION.name()))
		        oos.writeObject(attendExecutionOperation((ImageOperationMessage) clouderServerRequest));
            else if (clouderServerRequest.getType().equals(TCPMessageEnum.PHYSICAL_MACHINE_OPERATION.name()))
		        oos.writeObject(attendPhysicalMachineOperation((PhysicalMachineOperationMessage) clouderServerRequest));
		    else if (clouderServerRequest.getType().equals(TCPMessageEnum.AGENT_OPERATION.name()))
		        oos.writeObject(attendAgentOperation((AgentMessage) clouderServerRequest));
		    else
	            oos.writeObject(new UnaCloudResponse("Operation " + clouderServerRequest.getType() + " is invalid as main operation.", ExecutionProcessEnum.FAIL));
	        oos.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		
	}
	

    /**
     * Method responsible for attending requests for operations on executions
     * @param clouderServerRequestSplitted Server request
     */
    private UnaCloudResponse attendExecutionOperation(ImageOperationMessage message) {
	    try {
	    	switch (message.getTask()) {
	            case ImageOperationMessage.VM_START:
	            	UnaCloudResponse resp = new UnaCloudResponse("Starting execution...", ExecutionProcessEnum.SUCCESS);
	            	ExecutionStartMessage msg = (ExecutionStartMessage) message;
	        		ExecutorService.executeBackgroundTask(new StartExecutionTask(Execution.getFromStartExecutionMessage(msg), msg.getTransmissionType()));
	            	return resp;
	            case ImageOperationMessage.VM_STOP:
	            	ExecutorService.executeBackgroundTask(new StopExecutionTask(message.getExecutionId()));
	                return new UnaCloudResponse("Stopping execution...", ExecutionProcessEnum.SUCCESS);
	            case ImageOperationMessage.VM_RESTART:
	            	//TODO Unused
	                return PersistentExecutionManager.restartMachine(message.getExecutionId());
	            case ImageOperationMessage.VM_TIME:
	            	//TODO Unused
	                return PersistentExecutionManager.extendsVMTime((ExecutionAddTimeMessage) message);
	            case ImageOperationMessage.VM_SAVE_IMG:
	            	return PersistentExecutionManager.sendImageCopy((ExecutionSaveImageMessage) message);  
	            default:            	
	                return new UnaCloudResponse("Invalid execution operation: " + message.getTask(), ExecutionProcessEnum.FAIL);
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
			return new UnaCloudResponse(e.getMessage(), ExecutionProcessEnum.FAIL);
		} 
    }
    
    /**
     * Responsible to process agent message
     * @param message
     * @return information response
     */
    private UnaCloudResponse attendAgentOperation(AgentMessage message) {
    	try {
	        switch (message.getTask()) {
	            case AgentMessage.UPDATE_OPERATION:            	
	                return AgentManager.updateAgent();
	            case AgentMessage.STOP_CLIENT:           
	                return AgentManager.stopAgent();
	            case AgentMessage.GET_VERSION:
	                return new UnaCloudResponse(AgentManager.getVersion(), ExecutionProcessEnum.SUCCESS);
	            case AgentMessage.CLEAR_CACHE:
	                return ImageCacheManager.clearCache();                
	            case AgentMessage.CLEAR_IMAGE_FROM_CACHE:
	                return ImageCacheManager.clearImageFromCache(((ClearImageFromCacheMessage)message).getImageId());
	            case AgentMessage.GET_DATA_SPACE:
	            	return new UnaCloudResponse(AgentManager.getFreeDataSpace() + "", ExecutionProcessEnum.SUCCESS);
	            case AgentMessage.GET_FILE:
	            	return FileManager.copyLogs();
	        }
	        return new UnaCloudResponse("Invalid operation: " + message.getTask(), ExecutionProcessEnum.FAIL);
	    } catch (Exception e) {
	    	e.printStackTrace();
			return new UnaCloudResponse(e.getMessage(), ExecutionProcessEnum.FAIL);
		} 
    }
    
    /**
     * Method responsible for attending requests for operations over the
     * physical machine
     *
     * @param clouderServerRequestSplitted Server request
     * @param con Channel used to interact with UnaCloud server to receive or
     * send additional data
     */
    private UnaCloudResponse attendPhysicalMachineOperation(PhysicalMachineOperationMessage message) {
    	try {
    		switch (message.getTask()) {
            case PhysicalMachineOperationMessage.PM_TURN_OFF:            	
                return new UnaCloudResponse(OSFactory.getOS().turnOff(), ExecutionProcessEnum.SUCCESS);
            case PhysicalMachineOperationMessage.PM_RESTART:            	
                return new UnaCloudResponse(OSFactory.getOS().restart(), ExecutionProcessEnum.SUCCESS);
            case PhysicalMachineOperationMessage.PM_LOGOUT:            	
                return new UnaCloudResponse(OSFactory.getOS().logOut(), ExecutionProcessEnum.SUCCESS);
            case PhysicalMachineOperationMessage.PM_TURN_ON:
                PhysicalMachineTurnOnMessage turnOn = (PhysicalMachineTurnOnMessage) message;                
                return new UnaCloudResponse(OSFactory.getOS().turnOnMachines(turnOn.getMacs()), ExecutionProcessEnum.SUCCESS);          
            default:
                return new UnaCloudResponse(ERROR_MESSAGE + " The server physical machine operation request is invalid: " + message.getTask(), ExecutionProcessEnum.FAIL);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			return new UnaCloudResponse(e.getMessage(), ExecutionProcessEnum.FAIL);
		}        
    }

	
}