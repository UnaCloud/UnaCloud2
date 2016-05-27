package uniandes.communication;

import java.net.SocketException;

import uniandes.communication.processor.PmMessageProcessor;
import communication.UnaCloudMessageUDP;

/**
 * Receives message as reports about physical machines states and put it in message processor to be executed in thread pool
 * @author CesarF
 *
 */
public class PmMessageReceiver extends AbstractMessageReceiver{

	public PmMessageReceiver(int port, int threads) throws SocketException {		
		super(port,threads);
	}

	@Override
	public void processMessage(UnaCloudMessageUDP message) {
		threadPool.submit(new PmMessageProcessor(message));
	}

}
