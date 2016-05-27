package unacloud.share.queue;

/**
 * Interface to implements processes to manage message from queue
 * @author CesarF
 *
 */
public interface QueueReader {

	/**
	 * Process a message from queue based in message type
	 * @param message
	 */
	public void processMessage(QueueMessage message);
}
