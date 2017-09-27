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

	private static ClouderClientAttention instance;
    
	public ClouderClientAttention(int listenPort, int threads) {
		super(listenPort, threads);
	}

	public synchronized static ClouderClientAttention getInstance(int listenPort, int threads) throws Exception {
		if(instance==null)instance=new ClouderClientAttention(listenPort, threads);
		return instance;
	}

	 
	@Override
	protected Runnable processSocket(Socket socket) throws Exception {
		return new ClouderServerAttentionProcessor(socket);
	}
	
	public void stopSocket(){
		try {
			super.stopService();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}