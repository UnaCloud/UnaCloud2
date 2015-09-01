package queue.rabbitmq;

import queue.entities.QueueMessage;
import queue.manager.QueueManager;

public class QueueManagerRabbit extends QueueManager{
	
	public QueueManagerRabbit(String queueName, String host) {
		this.queueName = queueName;
		this.host = host;
	}

	@Override
	public void sendMessage(QueueMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueueMessage receiveMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
