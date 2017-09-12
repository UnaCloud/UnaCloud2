package uniandes.unacloud.file.net;

import java.net.Socket;

import uniandes.unacloud.file.net.task.AgentProcessorTask;

/**
 * Class used to process message from agents to manage agent files
 * @author CesarF
 *
 */
public class AgentServerSocket extends AbstractServerSocket {

	public AgentServerSocket(int listenPort, int threads) {
		super(listenPort, threads);	
	}

	@Override
	protected Runnable processSocket(Socket s) throws Exception {		
		return new AgentProcessorTask(s);
	}
}
