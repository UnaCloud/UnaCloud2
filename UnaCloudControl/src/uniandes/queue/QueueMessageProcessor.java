package uniandes.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import communication.UnaCloudAbstractResponse;
import communication.messages.ao.ClearVMCacheMessage;
import queue.QueueMessage;
import queue.QueueReader;
import unacloud.entities.PhysicalMachine;
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
	 * Get list of machines in queuemessage and process request to clear cache from agents
	 * @param message
	 */
	private void clearCache(QueueMessage message){
		List<PhysicalMachine> machines = new ArrayList<PhysicalMachine>();
		for(String id: message.getMessageParts()){
			
		}
		MessageSender sender = new MessageSender(machines, new ClearVMCacheMessage(), new ResponseProcessor() {			
			@Override
			public void attendResponse(UnaCloudAbstractResponse response) {
				
			}
			@Override
			public void attendError(String message) {
				
			}
		});
	}

}
