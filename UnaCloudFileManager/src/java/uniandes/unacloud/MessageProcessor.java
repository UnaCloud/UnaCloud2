package uniandes.unacloud;

import queue.QueueMessage;

/**
 * Abstract class used to process a Message from queue
 * @author Cesar
 *
 */
public abstract class MessageProcessor extends Thread{	
	
	private QueueMessage message;
	
	public MessageProcessor(QueueMessage message) {
		this.message = message;
	}
	
	@Override
	public void run() {
		try {
			processMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
			processError(e);
		}
	}
	
	/**
	 * Method to be implemented to process message
	 */
	protected abstract void processMessage(QueueMessage message)throws Exception;
	
	/**
	 * Method to be implemented to process exception in process message method
	 * @param e Exception in run method
	 */
	protected abstract void processError(Exception e);

}
