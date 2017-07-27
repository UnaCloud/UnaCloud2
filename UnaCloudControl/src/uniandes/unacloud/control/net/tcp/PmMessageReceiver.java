package uniandes.unacloud.control.net.tcp;

import java.net.Socket;
import java.net.SocketException;

import uniandes.unacloud.common.net.tcp.AbstractTCPServerSocket;

/**
 * Receives message as reports about physical machines states and put it in message processor to be executed in thread pool
 * @author CesarF
 *
 */
public class PmMessageReceiver extends AbstractTCPServerSocket{

	public PmMessageReceiver(int port, int threads) throws SocketException {		
		super(port,threads);
	}


	@Override
	protected Runnable processSocket(Socket socket) throws Exception {
		return new PmMessageProcessor(socket);
	}

}
