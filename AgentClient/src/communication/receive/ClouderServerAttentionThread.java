package communication.receive;

import static com.losandes.utils.Constants.ERROR_MESSAGE;
import static com.losandes.utils.Constants.MESSAGE_SEPARATOR_TOKEN;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.losandes.utils.OperatingSystem;
import com.losandes.utils.UnaCloudConstants;

import tasks.ExecutorService;
import tasks.StartVirtualMachineTask;
import tasks.StopVirtualMachineTask;
import virtualMachineManager.ImageCacheManager;
import virtualMachineManager.PersistentExecutionManager;
import virtualMachineManager.entities.VirtualMachineExecution;
import communication.UnaCloudAbstractMessage;
import communication.UnaCloudAbstractResponse;
import communication.messages.AgentMessage;
import communication.messages.InvalidOperationResponse;
import communication.messages.PhysicalMachineOperationMessage;
import communication.messages.VirtualMachineOperationMessage;
import communication.messages.ao.ClearImageFromCacheMessage;
import communication.messages.ao.InformationResponse;
import communication.messages.pmo.PhysicalMachineTurnOnMessage;
import communication.messages.vmo.VirtualMachineAddTimeMessage;
import communication.messages.vmo.VirtualMachineRestartMessage;
import communication.messages.vmo.VirtualMachineSaveImageMessage;
import communication.messages.vmo.VirtualMachineStartMessage;
import communication.messages.vmo.VirtualMachineStartResponse;
import communication.messages.vmo.VirtualMachineStopMessage;



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
        switch (message.getSubOp()) {
            case PhysicalMachineOperationMessage.PM_TURN_OFF:
                return new InformationResponse("PM_TURN_OFF" + MESSAGE_SEPARATOR_TOKEN + new OperatingSystem().turnOff());
            case PhysicalMachineOperationMessage.PM_RESTART:
                return new InformationResponse("PM_RESTART" + MESSAGE_SEPARATOR_TOKEN + new OperatingSystem().restart());
            case PhysicalMachineOperationMessage.PM_LOGOUT:
                return new InformationResponse("PM_LOGOUT" + MESSAGE_SEPARATOR_TOKEN + new OperatingSystem().logOut());
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
    }
}