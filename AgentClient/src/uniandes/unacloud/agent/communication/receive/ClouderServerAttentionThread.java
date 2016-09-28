package uniandes.unacloud.agent.communication.receive;

import static uniandes.unacloud.common.utils.Constants.ERROR_MESSAGE;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.agent.execution.entities.VirtualMachineExecution;
import uniandes.unacloud.agent.execution.task.ExecutorService;
import uniandes.unacloud.agent.execution.task.StartVirtualMachineTask;
import uniandes.unacloud.agent.execution.task.StopVirtualMachineTask;
import uniandes.unacloud.agent.system.OSFactory;
import uniandes.unacloud.common.com.UnaCloudAbstractMessage;
import uniandes.unacloud.common.com.UnaCloudAbstractResponse;
import uniandes.unacloud.common.com.messages.AgentMessage;
import uniandes.unacloud.common.com.messages.InvalidOperationResponse;
import uniandes.unacloud.common.com.messages.PhysicalMachineOperationMessage;
import uniandes.unacloud.common.com.messages.VirtualMachineOperationMessage;
import uniandes.unacloud.common.com.messages.agent.ClearImageFromCacheMessage;
import uniandes.unacloud.common.com.messages.agent.InformationResponse;
import uniandes.unacloud.common.com.messages.pmo.PhysicalMachineTurnOnMessage;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineAddTimeMessage;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineRestartMessage;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineSaveImageMessage;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineStartMessage;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineStartResponse;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineStopMessage;
import uniandes.unacloud.common.utils.UnaCloudConstants;


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
        try(Socket s=communication;ObjectInputStream ois=new ObjectInputStream(communication.getInputStream());ObjectOutputStream oos=new ObjectOutputStream(communication.getOutputStream())){
        	UnaCloudAbstractMessage clouderServerRequest=(UnaCloudAbstractMessage)ois.readObject();
            System.out.println("message: "+clouderServerRequest);
            switch (clouderServerRequest.getMainOp()) {
		        case UnaCloudAbstractMessage.VIRTUAL_MACHINE_OPERATION:
		            oos.writeObject(attendVirtualMachineOperation(clouderServerRequest,ois,oos));
		            break;
		        case UnaCloudAbstractMessage.PHYSICAL_MACHINE_OPERATION:
		        	oos.writeObject(attendPhysicalMachineOperation(clouderServerRequest));
		            break;
		        case UnaCloudAbstractMessage.AGENT_OPERATION:
		            oos.writeObject(attendAgentOperation(clouderServerRequest));
		            break;
		        default:
	                oos.writeObject(new InvalidOperationResponse("Opeartion "+clouderServerRequest.getMainOp()+" is invalid as main operation."));
	            break;
	        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     }

    /**
     * Method responsible for attending requests for operations over virtual machines
     * @param clouderServerRequestSplitted Server request
     */
    private UnaCloudAbstractResponse attendVirtualMachineOperation(UnaCloudAbstractMessage message,ObjectInputStream ois,ObjectOutputStream pw) {
        switch (message.getSubOp()) {
            case VirtualMachineOperationMessage.VM_START:
            	VirtualMachineStartResponse resp=new VirtualMachineStartResponse();
        		resp.setState(VirtualMachineStartResponse.VirtualMachineState.STARTING);
        		resp.setMessage("Starting virtual machine...");
        		ExecutorService.executeBackgroundTask(new StartVirtualMachineTask(VirtualMachineExecution.getFromStartVirtualMachineMessage((VirtualMachineStartMessage)message)));
            	return resp;
            case VirtualMachineOperationMessage.VM_STOP:
            	ExecutorService.executeBackgroundTask(new StopVirtualMachineTask((VirtualMachineStopMessage)message));
                return null;
            case VirtualMachineOperationMessage.VM_RESTART:
                return PersistentExecutionManager.restartMachine((VirtualMachineRestartMessage)message);
            case VirtualMachineOperationMessage.VM_TIME:
                return PersistentExecutionManager.extendsVMTime((VirtualMachineAddTimeMessage)message);
            case VirtualMachineOperationMessage.VM_SAVE_IMG:
            	return PersistentExecutionManager.sendImageCopy((VirtualMachineSaveImageMessage)message);  
            default:
                return new InvalidOperationResponse("Invalid virtual machine operation: "+message.getSubOp());
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
            	ClouderClientAttention.close();
                try {
        			Runtime.getRuntime().exec(new String[]{"javaw","-jar",UnaCloudConstants.UPDATER_JAR,UnaCloudConstants.DELAY+""});
                } catch (Exception e) {
                }
                new Thread(){
                	public void run() {
                		try {
                			Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                		System.exit(6);
                	};
                }.start();
                return new InformationResponse("successful");
            case AgentMessage.STOP_CLIENT:
                ClouderClientAttention.close();
                new Thread(){
                	public void run() {
                		try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                		System.exit(0);
                	};
                }.start();
                return  new InformationResponse("successful");
            case AgentMessage.GET_VERSION:
            	//TODO unuseful
                return new InformationResponse("1.30");
            case AgentMessage.CLEAR_CACHE:
                return new InformationResponse(ImageCacheManager.clearCache());
                
            case AgentMessage.CLEAR_IMAGE_FROM_CACHE:
                return new InformationResponse(ImageCacheManager.clearImageFromCache(((ClearImageFromCacheMessage)message).getImageId()));
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
            	OSFactory.getOS().turnOff();
                return new InformationResponse("PM_TURN_OFF");
            case PhysicalMachineOperationMessage.PM_RESTART:
            	OSFactory.getOS().restart();
                return new InformationResponse("PM_RESTART");
            case PhysicalMachineOperationMessage.PM_LOGOUT:
            	OSFactory.getOS().logOut();
                return new InformationResponse("PM_LOGOUT");
            case PhysicalMachineOperationMessage.PM_TURN_ON:
                    PhysicalMachineTurnOnMessage turnOn=(PhysicalMachineTurnOnMessage)message;
                for (String mac : turnOn.getMacs()) {
                    try {
                        Runtime.getRuntime().exec("wol.exe " + mac.replace(":", ""));
                    } catch (IOException ex) {
                    }
                }
                return new InformationResponse("successful");          
            default:
                return new InformationResponse(ERROR_MESSAGE + "The server physical machine operation request is invalid: " + message.getSubOp());
    		}
		} catch (Exception e) {
			return new InformationResponse(e.getMessage());
		}        
    }
}