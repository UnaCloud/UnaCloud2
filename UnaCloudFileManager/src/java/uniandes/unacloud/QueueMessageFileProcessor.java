package uniandes.unacloud;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import queue.QueueMessage;
import queue.QueueReader;

/**
 * Class to process messages sent to manage files
 * @author Cesar
 *
 */
public class QueueMessageFileProcessor implements QueueReader{
	
	private ExecutorService threadPool=Executors.newFixedThreadPool(5);

	@Override
	public void processMessage(QueueMessage message) {
		System.out.println("Receive message "+message.getType());
		switch (message.getType()) {
		case CREATE_PUBLIC_IMAGE:	
		
			break;
		case CREATE_COPY_FROM_PUBLIC:			
			
			break;
		case DELETE_IMAGE:
			
			break;
		case DELETE_PUBLIC_IMAGE:	
			
			break;
		case DELETE_USER:		
			
			break;
		default:
			break;
		}
	}

}
