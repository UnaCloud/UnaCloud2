package unacloud.task.queue;

public class QueueRabbitManager extends QueueTaskerConnection{

	protected QueueRabbitManager(String username, String password, String ip,
			int port, String queueName) {
		super(username, password, ip, port, queueName);
	}

	@Override
	protected void sendMessage(QueueMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected QueueMessage getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
