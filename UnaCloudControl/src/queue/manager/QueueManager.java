package queue.manager;

import queue.entities.QueueMessage;

public abstract class QueueManager {
	
	protected String queueName, host;
		
	public abstract void sendMessage(QueueMessage message);
	
	public abstract QueueMessage receiveMessage();

}
