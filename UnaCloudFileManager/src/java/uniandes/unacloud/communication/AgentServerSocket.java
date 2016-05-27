package uniandes.unacloud.communication;

import java.net.Socket;

/**
 * Class used to process message from agents to manage agent files
 * @author Cesar
 *
 */
public class AgentServerSocket extends AbstractServerSocket{

	public AgentServerSocket(int listenPort, int threads) {
		super(listenPort, threads);	
	}

	@Override
	protected Runnable processSocket(Socket s) throws Exception {		
		return new AgentProcessorTask(s);
	}
}
