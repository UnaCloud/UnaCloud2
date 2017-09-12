package uniandes.unacloud.file.net;

import java.net.Socket;

import uniandes.unacloud.common.net.tcp.AbstractTCPServerSocket;
import uniandes.unacloud.file.net.task.AgentProcessorTask;

/**
 * Class used to process message from agents to manage agent files
 * @author CesarF
 *
 */
public class AgentServerSocket extends AbstractTCPServerSocket {

	/**
	 * Construct a new Agent server socket
	 * @param listenPort
	 * @param threads
	 */
	public AgentServerSocket(int listenPort, int threads) {
		super(listenPort, threads);	
	}

	@Override
	protected Runnable processSocket(Socket s) throws Exception {		
		return new AgentProcessorTask(s);
	}
}
