package uniandes.queue;

import queue.QueueMessage;
import queue.QueueReader;

public class QueueMessageProcessor implements QueueReader{

	@Override
	public void processMessage(QueueMessage message) {
		switch (message.getType()) {
		case CLEAR_CACHE:	
			System.out.println("Clear cache");
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

}
