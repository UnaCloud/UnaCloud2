package uniandes.unacloud.control.net.udp;

import java.net.SocketException;

import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.net.udp.AbstractUDPMessageReceiver;

/**
 * Receives message as reports about physical machines states and put it in message processor to be executed in thread pool
 * @author CesarF
 *
 */
public class PmMessageReceiver extends AbstractUDPMessageReceiver {

	public PmMessageReceiver(int port, int threads) throws SocketException {		
		super(port,threads);
	}

	@Override
	public Runnable processMessage(UnaCloudMessage message) {
		return new PmMessageProcessor(message);
	}

}
