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
import uniandes.unacloud.common.net.tcp.AbstractTCPSocketProcessor;
import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.net.tcp.message.AgentMessage;
import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;
import uniandes.unacloud.common.net.tcp.message.InformationResponse;
import uniandes.unacloud.common.net.tcp.message.PhysicalMachineOperationMessage;
import uniandes.unacloud.common.net.tcp.message.TCPMessageEnum;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.common.net.tcp.message.agent.ClearImageFromCacheMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionAddTimeMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionSaveImageMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionStartMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionStartResponse;
import uniandes.unacloud.common.net.tcp.message.exe.InvalidOperationResponse;
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
		try (Socket s = socket; ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()); ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
        	UnaCloudMessage clouderServerRequest = (UnaCloudMessage) ois.readObject();
            System.out.println("message: " + clouderServerRequest);
            if (clouderServerRequest.getType().equals(TCPMessageEnum.EXECUTION_OPERATION.name()))
		            oos.writeObject(attendExecutionOperation((ImageOperationMessage) clouderServerRequest, ois, oos));
            else if (clouderServerRequest.getType().equals(TCPMessageEnum.PHYSICAL_MACHINE_OPERATION.name()))
		        	oos.writeObject(attendPhysicalMachineOperation((PhysicalMachineOperationMessage) clouderServerRequest));
		    else if (clouderServerRequest.getType().equals(TCPMessageEnum.AGENT_OPERATION.name()))
		            oos.writeObject(attendAgentOperation((AgentMessage) clouderServerRequest));
		    else
	                oos.writeObject(new InvalidOperationResponse("Operation " + clouderServerRequest.getType() + " is invalid as main operation."));
	          
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		
	}
	

    /**
     * Method responsible for attending requests for operations on executions
     * @param clouderServerRequestSplitted Server request
     */
    private UnaCloudResponse attendExecutionOperation(ImageOperationMessage message, ObjectInputStream ois, ObjectOutputStream pw) {
        switch (message.getTask()) {
            case ImageOperationMessage.VM_START:
            	ExecutionStartResponse resp = new ExecutionStartResponse();
        		resp.setState(ExecutionStartResponse.ExecutionState.STARTING);
        		resp.setMessage("Starting execution...");
        		ExecutorService.executeBackgroundTask(new StartExecutionTask(Execution.getFromStartExecutionMessage((ExecutionStartMessage) message)));
            	return resp;
            case ImageOperationMessage.VM_STOP:
            	ExecutorService.executeBackgroundTask(new StopExecutionTask(message.getExecutionId()));
                return null;
            case ImageOperationMessage.VM_RESTART:
                return PersistentExecutionManager.restartMachine(message.getExecutionId());
            case ImageOperationMessage.VM_TIME:
                return PersistentExecutionManager.extendsVMTime((ExecutionAddTimeMessage) message);
            case ImageOperationMessage.VM_SAVE_IMG:
            	return PersistentExecutionManager.sendImageCopy((ExecutionSaveImageMessage) message);  
            default:
                return new InvalidOperationResponse("Invalid execution operation: " + message.getTask());
        }
    }
    
    /**
     * Responsible to process agent message
     * @param message
     * @return information response
     */
    private UnaCloudResponse attendAgentOperation(AgentMessage message) {
        switch (message.getTask()) {
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
            	return new InformationResponse(AgentManager.getFreeDataSpace() + "");
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
    private UnaCloudResponse attendPhysicalMachineOperation(PhysicalMachineOperationMessage message) {
    	try {
    		switch (message.getTask()) {
            case PhysicalMachineOperationMessage.PM_TURN_OFF:            	
                return new InformationResponse(OSFactory.getOS().turnOff());
            case PhysicalMachineOperationMessage.PM_RESTART:            	
                return new InformationResponse(OSFactory.getOS().restart());
            case PhysicalMachineOperationMessage.PM_LOGOUT:            	
                return new InformationResponse(OSFactory.getOS().logOut());
            case PhysicalMachineOperationMessage.PM_TURN_ON:
                PhysicalMachineTurnOnMessage turnOn = (PhysicalMachineTurnOnMessage) message;                
                return new InformationResponse(OSFactory.getOS().turnOnMachines(turnOn.getMacs()));          
            default:
                return new InformationResponse(ERROR_MESSAGE + "The server physical machine operation request is invalid: " + message.getTask());
    		}
		} catch (Exception e) {
			return new InformationResponse(e.getMessage());
		}        
    }

	
}