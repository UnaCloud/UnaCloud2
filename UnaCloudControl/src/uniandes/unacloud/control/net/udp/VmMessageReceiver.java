package uniandes.unacloud.control.net.udp;

import java.net.SocketException;

import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.net.udp.AbstractUDPMessageReceiver;


/**
 * Receive message as reports about execution states and put it in message processor to be executed in thread pool
 * @author CesarF
 *
 */
public class VmMessageReceiver extends AbstractUDPMessageReceiver {

	public VmMessageReceiver(int port, int threads) throws SocketException {
		super(port, threads);
	}

	@Override
	public Runnable processMessage(UnaCloudMessage message) {
		return new VmMessageProcessor(message);
	}

}
