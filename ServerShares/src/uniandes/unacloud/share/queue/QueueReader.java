package uniandes.unacloud.share.queue;

import uniandes.unacloud.share.queue.messages.QueueMessage;

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
