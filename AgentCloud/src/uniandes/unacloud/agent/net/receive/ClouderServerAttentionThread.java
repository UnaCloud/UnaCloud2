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
import uniandes.unacloud.agent.system.OSFactory;
import uniandes.unacloud.common.net.messages.ImageOperationMessage;
import uniandes.unacloud.common.net.messages.InvalidOperationResponse;
import uniandes.unacloud.common.net.messages.PhysicalMachineOperationMessage;
import uniandes.unacloud.common.net.messages.UnaCloudAbstractMessage;
import uniandes.unacloud.common.net.messages.UnaCloudAbstractResponse;
import uniandes.unacloud.common.net.messages.agent.AgentMessage;
import uniandes.unacloud.common.net.messages.agent.ClearImageFromCacheMessage;
import uniandes.unacloud.common.net.messages.agent.InformationResponse;
import uniandes.unacloud.common.net.messages.exeo.ExecutionAddTimeMessage;
import uniandes.unacloud.common.net.messages.exeo.ExecutionRestartMessage;
import uniandes.unacloud.common.net.messages.exeo.ExecutionSaveImageMessage;
import uniandes.unacloud.common.net.messages.exeo.ExecutionStartMessage;
import uniandes.unacloud.common.net.messages.exeo.ExecutionStartResponse;
import uniandes.unacloud.common.net.messages.exeo.ExecutionStopMessage;
import uniandes.unacloud.common.net.messages.pmo.PhysicalMachineTurnOnMessage;


/**
 * Responsible for attending or discarding a
 * Server operation request in a thread
 */
public class ClouderServerAttentionThread implements Runnable {
	
	//-----------------------------------------------------------------
	// Variables
	//-----------------------------------------------------------------

    /**
     * Abstract communicator used to receive the request and send a response
     */
    private Socket communication;
    
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

    /**
     * Constructs an attention thread for a given communicator channel. Receives an owner object for stop requests
     *
     * @param clientSocket
     */
    public ClouderServerAttentionThread(Socket clientSocket) {
        communication = clientSocket;
    }

    /**
     * Responsible for attending or discarding the Server operation
     * request
     */
    public void run() {
        try (Socket s = communication; ObjectInputStream ois = new ObjectInputStream(communication.getInputStream()); ObjectOutputStream oos = new ObjectOutputStream(communication.getOutputStream())) {
        	UnaCloudAbstractMessage clouderServerRequest = (UnaCloudAbstractMessage) ois.readObject();
            System.out.println("message: " + clouderServerRequest);
            switch (clouderServerRequest.getMainOp()) {
		        case UnaCloudAbstractMessage.EXECUTION_OPERATION:
		            oos.writeObject(attendExecutionOperation(clouderServerRequest, ois, oos));
		            break;
		        case UnaCloudAbstractMessage.PHYSICAL_MACHINE_OPERATION:
		        	oos.writeObject(attendPhysicalMachineOperation(clouderServerRequest));
		            break;
		        case UnaCloudAbstractMessage.AGENT_OPERATION:
		            oos.writeObject(attendAgentOperation(clouderServerRequest));
		            break;
		        default:
	                oos.writeObject(new InvalidOperationResponse("Operation " + clouderServerRequest.getMainOp() + " is invalid as main operation."));
	            break;
	        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     }

    /**
     * Method responsible for attending requests for operations on executions
     * @param clouderServerRequestSplitted Server request
     */
    private UnaCloudAbstractResponse attendExecutionOperation(UnaCloudAbstractMessage message, ObjectInputStream ois, ObjectOutputStream pw) {
        switch (message.getSubOp()) {
            case ImageOperationMessage.VM_START:
            	ExecutionStartResponse resp = new ExecutionStartResponse();
        		resp.setState(ExecutionStartResponse.ExecutionState.STARTING);
        		resp.setMessage("Starting execution...");
        		ExecutorService.executeBackgroundTask(new StartExecutionTask(Execution.getFromStartExecutionMessage((ExecutionStartMessage)message)));
            	return resp;
            case ImageOperationMessage.VM_STOP:
            	ExecutorService.executeBackgroundTask(new StopExecutionTask((ExecutionStopMessage)message));
                return null;
            case ImageOperationMessage.VM_RESTART:
                return PersistentExecutionManager.restartMachine((ExecutionRestartMessage)message);
            case ImageOperationMessage.VM_TIME:
                return PersistentExecutionManager.extendsVMTime((ExecutionAddTimeMessage)message);
            case ImageOperationMessage.VM_SAVE_IMG:
            	return PersistentExecutionManager.sendImageCopy((ExecutionSaveImageMessage)message);  
            default:
                return new InvalidOperationResponse("Invalid execution operation: " + message.getSubOp());
        }
    }
    /**
     * Responsible to process agent message
     * @param message
     * @return information response
     */
    private UnaCloudAbstractResponse attendAgentOperation(UnaCloudAbstractMessage message) {
        switch (message.getSubOp()) {
            case AgentMessage.UPDATE_OPERATION:            	
                return new InformationResponse(AgentManager.updateAgent());
            case AgentMessage.STOP_CLIENT:           
                return  new InformationResponse(AgentManager.stopAgent());
            case AgentMessage.GET_VERSION:
                return new InformationResponse(AgentManager.getVersion());
            case AgentMessage.CLEAR_CACHE:
                return new InformationResponse(ImageCacheManager.clearCache());                
            case AgentMessage.CLEAR_IMAGE_FROM_CACHE:
                return new InformationResponse(ImageCacheManager.clearImageFromCache(((ClearImageFromCacheMessage)message).getImageId()));
            case AgentMessage.GET_DATA_SPACE:
            	return new InformationResponse(AgentManager.getFreeDataSpace()+"");
        }
        return  new InformationResponse("Invalid operation");
    }
    /**
     * Method responsible for attending requests for operations over the
     * physical machine
     *
     * @param clouderServerRequestSplitted Server request
     * @param con Channel used to interact with UnaCloud server to receive or
     * send additional data
     */
    private UnaCloudAbstractResponse attendPhysicalMachineOperation(UnaCloudAbstractMessage message) {
    	try {
    		switch (message.getSubOp()) {
            case PhysicalMachineOperationMessage.PM_TURN_OFF:            	
                return new InformationResponse(OSFactory.getOS().turnOff());
            case PhysicalMachineOperationMessage.PM_RESTART:            	
                return new InformationResponse(OSFactory.getOS().restart());
            case PhysicalMachineOperationMessage.PM_LOGOUT:            	
                return new InformationResponse(OSFactory.getOS().logOut());
            case PhysicalMachineOperationMessage.PM_TURN_ON:
                PhysicalMachineTurnOnMessage turnOn=(PhysicalMachineTurnOnMessage)message;                
                return new InformationResponse(OSFactory.getOS().turnOnMachines(turnOn.getMacs()));          
            default:
                return new InformationResponse(ERROR_MESSAGE + "The server physical machine operation request is invalid: " + message.getSubOp());
    		}
		} catch (Exception e) {
			return new InformationResponse(e.getMessage());
		}        
    }
}