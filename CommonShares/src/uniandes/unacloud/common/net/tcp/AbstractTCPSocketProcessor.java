package uniandes.unacloud.common.net.tcp;

import java.net.Socket;

/**
 * Abstract class to be implemented by classes to manage communication with sockets
 * @author CesarF
 *
 */
public abstract class AbstractTCPSocketProcessor implements Runnable {
	
	/**
	 * socket to be process
	 */
	private Socket socket;
	
	/**
	 * Creates a new TCP Socket
	 * @param socket
	 */
	public AbstractTCPSocketProcessor(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try(Socket ss = socket) {
			processMessage(ss);
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	/**
	 * To be implemented to process message
	 * @param message
	 * @throws Exception
	 */
	public abstract void processMessage(Socket socket)  throws Exception;

}
