package uniandes.unacloud.common.net.udp;

import uniandes.unacloud.common.net.UnaCloudMessage;

/**
 * Class to process UDP messages sent by agents
 * @author CesarF
 *
 */
public abstract class AbstractUDPReceiverProcessor implements Runnable {
	
	/**
	 * Message to be processed
	 */
	private UnaCloudMessage message;

	/**
	 * Creates a new receiver
	 * @param message
	 */
	public AbstractUDPReceiverProcessor(UnaCloudMessage message) {
		this.message = message;
	}
	
	@Override
	public void run() {		
		try {
			processMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * To be implemented to process message
	 * @param message
	 * @throws Exception
	 */
	public abstract void processMessage(UnaCloudMessage message)  throws Exception;
	

}
