package uniandes.communication;

import java.net.SocketException;

import uniandes.communication.processor.VmMessageProcessor;
import communication.UnaCloudMessageUDP;


/**
 * Receive message as reports about virtual machines states
 * @author Cesar
 *
 */
public class VmMessageReceiver extends AbstractMessageReceiver{

	public VmMessageReceiver(int port, int threads) throws SocketException {
		super(port, threads);
	}

	@Override
	public void processMessage(UnaCloudMessageUDP message) {
		threadPool.submit(new VmMessageProcessor(message));
	}

}
