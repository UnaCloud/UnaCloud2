package uniandes.queue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.losandes.utils.Constants;

import communication.UnaCloudAbstractMessage;
import communication.UnaCloudAbstractResponse;
import communication.messages.ao.ClearImageFromCacheMessage;
import communication.messages.ao.ClearVMCacheMessage;
import communication.messages.ao.StopAgentMessage;
import communication.messages.ao.UpdateAgentMessage;
import db.PhysicalMachineManager;
import db.VirtualImageManager;
import queue.QueueMessage;
import queue.QueueReader;
import unacloud.entities.PhysicalMachine;
import unacloud.entities.VirtualMachineImage;
import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.TaskEnum;
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
		case SEND_TASK:			
			sendTaskToAgents(message);
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
	 * Get virtual image in queuemessage and process request to remove the image from agents cache
	 * @param message
	 */
	private void clearCache(QueueMessage message){
		try {		
			final Long imageId =  Long.parseLong(message.getMessageParts()[0]);
			VirtualMachineImage image = new VirtualMachineImage(imageId, null, null, VirtualMachineImageEnum.REMOVING_CACHE);
			VirtualImageManager.setVirtualMachine(image);
			try {				
				List<PhysicalMachine> machines=PhysicalMachineManager.getAllPhysicalMachine(PhysicalMachineStateEnum.ON);			
				for (int i = 0; i < machines.size(); i+=Constants.AGENT_QUANTITY_MESSAGE) {
					threadPool.submit(new MessageSender(machines.subList(i, i+Constants.AGENT_QUANTITY_MESSAGE), new ClearImageFromCacheMessage(imageId), new ResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							VirtualMachineImage image = new VirtualMachineImage(imageId, null, null, VirtualMachineImageEnum.AVAILABLE);
							VirtualImageManager.setVirtualMachine(image);
						}
						@Override
						public void attendError(String message, Long id) {
							VirtualMachineImage image = new VirtualMachineImage(imageId, null, null, VirtualMachineImageEnum.AVAILABLE);
							VirtualImageManager.setVirtualMachine(image);
							PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm);
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
	
	/**
	 * Method to send stop, update o clear cache message to specific list of physical machines
	 * @param message
	 */
	private void sendTaskToAgents(QueueMessage message){
		try {
			TaskEnum task = TaskEnum.getEnum(message.getMessageParts()[0]);
			Long[] ids = new Long[message.getMessageParts().length-1];
			for (int i = 1, j=0; i < message.getMessageParts().length; i++, j++) {
				ids[j]=Long.parseLong(message.getMessageParts()[i]);
			}
			List<PhysicalMachine> machines=PhysicalMachineManager.getPhysicalMachineList(ids,PhysicalMachineStateEnum.PROCESSING);			
			for (int i = 0; i < machines.size(); i+=Constants.AGENT_QUANTITY_MESSAGE) {
				UnaCloudAbstractMessage absMessage = task.equals(TaskEnum.CACHE)?
						new ClearVMCacheMessage():task.equals(TaskEnum.STOP)?
								new StopAgentMessage():new UpdateAgentMessage();
				threadPool.submit(new MessageSender(machines.subList(i, i+Constants.AGENT_QUANTITY_MESSAGE), 
						absMessage, new ResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.ON);
						PhysicalMachineManager.setPhysicalMachine(pm);
					}
					@Override
					public void attendError(String message, Long id) {
						PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
						PhysicalMachineManager.setPhysicalMachine(pm);
					}
				}));
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
