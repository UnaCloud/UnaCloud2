package uniandes.communication;

import java.net.SocketException;

import uniandes.communication.processor.PmMessageProcessor;
import communication.UnaCloudMessageUDP;

/**
 * Receive message as reports about physical machines states
 * @author Cesar
 *
 */
public class PmMessageReceiver extends AbstractMessageReceiver{

	public PmMessageReceiver(int port) throws SocketException {
		super(port);
	}

	@Override
	public void processMessage(UnaCloudMessageUDP message) {
		threadPool.submit(new PmMessageProcessor(message));
	}

}
