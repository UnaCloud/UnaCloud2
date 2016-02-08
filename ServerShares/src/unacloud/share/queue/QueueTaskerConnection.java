package unacloud.share.queue;

/**
 * Abstract class to represent connection to Queue
 * It allows to send message and collect one
 * @author Cesar
 *
 */

public abstract class QueueTaskerConnection {
	
	protected String username;
	protected String password;
	protected String ip;
	protected int port;
	protected String queueName;
	
	protected QueueTaskerConnection(String username, String password, String ip,
			int port, String queueName) {
		this.username = username;
		this.password = password;
		this.ip = ip;
		this.port = port;
		this.queueName = queueName;
	}

	/**
	 * Abstract method to be implemented by other classes to send message
	 * @param message
	 */
	public abstract void sendMessage(QueueMessage message);
	
	/**
	 * Abstract method to be implemented by other classes to receive message
	 * @param object which implements QueueReader to process messages
	 */
	public abstract void getMessage(QueueReader reader);
	
}
