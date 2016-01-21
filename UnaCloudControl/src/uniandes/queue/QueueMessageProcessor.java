package uniandes.queue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.losandes.utils.Constants;

import communication.UnaCloudAbstractResponse;
import communication.messages.ao.ClearImageFromCacheMessage;
import db.PhysicalMachineManager;
import db.VirtualImageManager;
import queue.QueueMessage;
import queue.QueueReader;
import unacloud.entities.PhysicalMachine;
import unacloud.entities.VirtualMachineImage;
import unacloud.enums.VirtualMachineImageEnum;
import uniandes.communication.MessageSender;
import uniandes.communication.ResponseProcessor;

/**
 * Class to process each message from queue
 * @author Cesar
 *
 */
public class QueueMessageProcessor implements QueueReader{
	
	private ExecutorService threadPool=Executors.newFixedThreadPool(3);

	@Override
	public void processMessage(QueueMessage message) {
		System.out.println("Receive message "+message.getType());
		switch (message.getType()) {
		case CLEAR_CACHE:	
			clearCache(message);
			break;
		case DELETE_USER:	
			System.out.println("Delete User");
			break;
		case SEND_TASK:			
			System.out.println("Send task");
			break;
		case DEPLOY_CLUSTER:
			System.out.println("Deploy cluster");
			break;
		case STOP_DEPLOYS:	
			System.out.println("Stop deploy");
			break;
		case ADD_INSTANCES:		
			System.out.println("Add Instances");
			break;
		case CREATE_COPY:	
			System.out.println("Create copy");
			break;
		default:
			break;
		}
	}
	
	/**
	 * Get list of machines in queuemessage and process request to remove an image from agents cache
	 * @param message
	 */
	private void clearCache(QueueMessage message){
		try {		
			Long id =  Long.parseLong(message.getMessageParts()[0]);
			VirtualMachineImage image = new VirtualMachineImage(id, null, null, VirtualMachineImageEnum.REMOVING_CACHE);
			VirtualImageManager.setVirtualMachine(image);
			try {				
				List<PhysicalMachine> machines=PhysicalMachineManager.getAllPhysicalMachine();			
				for (int i = 0; i < machines.size(); i+=Constants.AGENT_QUANTITY_MESSAGE) {
					threadPool.submit(new MessageSender(machines.subList(i, i+Constants.AGENT_QUANTITY_MESSAGE), new ClearImageFromCacheMessage(id), new ResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							VirtualMachineImage image = new VirtualMachineImage(id, null, null, VirtualMachineImageEnum.AVAILABLE);
							VirtualImageManager.setVirtualMachine(image);
						}
						@Override
						public void attendError(String message, Long id) {
							VirtualMachineImage image = new VirtualMachineImage(id, null, null, VirtualMachineImageEnum.AVAILABLE);
							VirtualImageManager.setVirtualMachine(image);
						}
					}));
				}				
			} catch (Exception e) {
				image.setState(VirtualMachineImageEnum.AVAILABLE);
				VirtualImageManager.setVirtualMachine(image);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
