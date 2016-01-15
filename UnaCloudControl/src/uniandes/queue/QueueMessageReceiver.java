package uniandes.queue;

import queue.QueueMessage;
import queue.QueueTaskerConnection;

/**
 * Class to create connection with queue and start receiving messages
 * @author Cesar
 *
 */
public class QueueMessageReceiver {
	
	private static QueueTaskerConnection connection;
	
	/**
	 * Set the connection with an instance of some class that uses QueueTaskerConnection
	 * @param queueProcessor
	 */
	public static void createConnection(QueueTaskerConnection queueProcessor){
		connection = queueProcessor;
	}
	
	/**
	 * Creates processor and start process to receive messages
	 */
	public static void startReceiver(){
		QueueMessageProcessor processor = new QueueMessageProcessor();
		connection.getMessage(processor);
	}
	
	/**
	 * Send a message throws the queue
	 * @param message
	 */
	public static void sendMessage(QueueMessage message){
		connection.sendMessage(message);
	}

}
