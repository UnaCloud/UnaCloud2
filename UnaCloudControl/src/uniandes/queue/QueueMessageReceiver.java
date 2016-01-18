package uniandes.queue;


import queue.QueueMessage;
import queue.QueueTaskerConnection;

/**
 * Class to create connection with queue and start receiving messages
 * @author Cesar
 *
 */
public class QueueMessageReceiver {
	
	/**
	 * Connection to queue
	 */
	private QueueTaskerConnection connection;
	
	private static QueueMessageReceiver instance;
	public synchronized static QueueMessageReceiver getInstance() {
	     if(instance==null)instance=new QueueMessageReceiver();
	         return instance;
	}
	
	/**
	 * Set the connection with an instance of some class that uses QueueTaskerConnection
	 * @param queueProcessor
	 */
	public void createConnection(QueueTaskerConnection queueProcessor){
		connection = queueProcessor;
	}
	
	/**
	 * Creates processor and start process to receive messages
	 */
	public void startReceiver(){
		QueueMessageProcessor processor = new QueueMessageProcessor();
		connection.getMessage(processor);
	}
	
	/**
	 * Send a message throws the queue
	 * @param message
	 */
	public void sendMessage(QueueMessage message){
		connection.sendMessage(message);
	}

}
