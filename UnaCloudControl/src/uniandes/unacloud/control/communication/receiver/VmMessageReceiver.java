package uniandes.unacloud.control.communication.receiver;

import java.net.SocketException;

import uniandes.unacloud.common.com.messages.udp.UnaCloudMessageUDP;
import uniandes.unacloud.control.communication.processor.VmMessageProcessor;


/**
 * Receive message as reports about virtual machines states and put it in message processor to be executed in thread pool
 * @author CesarF
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
