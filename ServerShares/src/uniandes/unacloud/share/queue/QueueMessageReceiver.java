package uniandes.unacloud.share.queue;

import uniandes.unacloud.share.queue.messages.QueueMessage;

/**
 * Class to create connection with queue and start receiving messages
 * @author CesarF
 */
public class QueueMessageReceiver {
	
	/**
	 * Connection to queue
	 */
	private QueueTaskerConnection connection;
	/**
	 * Sets the connection with an instance of some class that uses QueueTaskerConnection
	 * @param queueProcessor
	 */
	public void createConnection(QueueTaskerConnection queueProcessor) {
		connection = queueProcessor;
	}
	
	/**
	 * Creates processor and start process to receive messages
	 */
	public void startReceiver(QueueReader reader) {
		connection.getMessage(reader);
	}
	
	/**
	 * Sends a message throws the queue
	 * @param message
	 */
	public void sendMessage(QueueMessage message) {
		connection.sendMessage(message);
	}

}
