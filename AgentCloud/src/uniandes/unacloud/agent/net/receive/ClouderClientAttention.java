package uniandes.unacloud.agent.net.receive;

import java.io.IOException;
import java.net.Socket;

import uniandes.unacloud.common.net.tcp.AbstractTCPServerSocket;

/**
 * Responsible for listening messages from Server
 * @author CesarF
 * @author Clouder
 */
public class ClouderClientAttention extends AbstractTCPServerSocket {
    
	public ClouderClientAttention(int listenPort, int threads) {
		super(listenPort, threads);
	}

	 
	@Override
	protected Runnable processSocket(Socket socket) throws Exception {
		return new ClouderServerAttentionProcessor(socket);
	}
	
	public void stopSocket(){
		try {
			super.ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}