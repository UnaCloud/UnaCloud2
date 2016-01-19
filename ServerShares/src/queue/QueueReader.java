package queue;

/**
 * Interface to implements processes to manage message from queue
 * @author Cesar
 *
 */
public interface QueueReader {

	/**
	 * Process a message from queue based in message type
	 * @param message
	 */
	public void processMessage(QueueMessage message);
}
