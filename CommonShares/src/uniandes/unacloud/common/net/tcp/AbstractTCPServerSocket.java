package uniandes.unacloud.common.net.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Abstract class to be implemented by processes to receive messages from agents.
 * @author CesarF
 *
 */
public abstract class AbstractTCPServerSocket extends Thread {
	/**
	 * Thread pool to process sockets in background
	 */
	private Executor threadPool;

	/**
	 * Server socket for service
	 */
	private ServerSocket ss ;
	
	/**
	 * Port to listen
	 */
	private int listenPort;
	
	/**
	 * Creates a new TCP server socket
	 * @param listenPort port
	 * @param threads quantity
	 */
	public AbstractTCPServerSocket(int listenPort, int threads) {
		this.listenPort = listenPort;
		threadPool = Executors.newFixedThreadPool(threads);
	}
	
	@Override
	public void run() {
		System.out.println("starting ss on port " + listenPort);
		try  {
			ss = new ServerSocket(listenPort);
			while (true) {
				try {
					Socket s = ss.accept();
					threadPool.execute(processSocket(s));
				} catch (SocketException soe) {
					ss.close();
					soe.printStackTrace();
					break;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop service if it is running
	 * @throws IOException
	 */
	public void stopService() throws IOException {
		if (ss != null)
			ss.close();
	}

	/**
	 * Returns the runnable where socket is processed
	 * @param socket
	 * @return runnable process to be added to thread pool
	 */
	protected abstract Runnable processSocket(Socket socket) throws Exception;
}