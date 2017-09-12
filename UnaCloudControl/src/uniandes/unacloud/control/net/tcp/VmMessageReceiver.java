package uniandes.unacloud.control.net.tcp;

import java.net.Socket;
import java.net.SocketException;

import uniandes.unacloud.common.net.tcp.AbstractTCPServerSocket;


/**
 * Receive message as reports about execution states and put it in message processor to be executed in thread pool
 * @author CesarF
 *
 */
public class VmMessageReceiver extends AbstractTCPServerSocket {

	public VmMessageReceiver(int port, int threads) throws SocketException {
		super(port, threads);
	}

	@Override
	public Runnable processSocket(Socket socket) {
		return new VmMessageProcessor(socket);
	}

}
