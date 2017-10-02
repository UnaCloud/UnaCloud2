package uniandes.unacloud.agent.net.receive;

import java.net.Socket;

import uniandes.unacloud.common.net.tcp.AbstractTCPServerSocket;

/**
 * Responsible for listening messages from Server
 * @author CesarF
 * @author Clouder
 */
public class ClouderClientAttention extends AbstractTCPServerSocket {

	/**
	 * Singleton instance
	 */
	private static ClouderClientAttention instance;
    
	/**
	 * Creates a new clouder service with port and threads number
	 * @param listenPort
	 * @param threads
	 */
	private ClouderClientAttention(int listenPort, int threads) {
		super(listenPort, threads);
	}

	/**
	 * Return a new instance listening in port by parameters
	 * @param listenPort
	 * @param threads
	 * @return instance
	 * @throws Exception
	 */
	public synchronized static ClouderClientAttention getInstance(int listenPort, int threads) throws Exception {
		if (instance == null)
			instance = new ClouderClientAttention(listenPort, threads);
		return instance;
	}
	
	/**
	 * Returns the current instance
	 * @return instance
	 * @throws Exception
	 */
	public static ClouderClientAttention getInstance() throws Exception {
		if (instance == null) 
			throw new Exception("Service is not running");
		return instance;
	}

	 
	@Override
	protected Runnable processSocket(Socket socket) throws Exception {
		return new ClouderServerAttentionProcessor(socket);
	}
}