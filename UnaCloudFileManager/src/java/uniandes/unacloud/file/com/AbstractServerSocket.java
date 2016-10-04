package uniandes.unacloud.file.com;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Abstract class to be implemented by processes to receive messages from agents.
 * @author CesarF
 *
 */
public abstract class AbstractServerSocket extends Thread{
	
	private ExecutorService threadPool;
	private int listenPort;
	
	public AbstractServerSocket(int listenPort, int threads) {
		this.listenPort = listenPort;
		threadPool=Executors.newFixedThreadPool(3);
	}
	@Override
	public void run(){
		System.out.println("starting ss on port "+listenPort);
		try(ServerSocket ss = new ServerSocket(listenPort)){
			while(true){
				Socket s=ss.accept();
				try {		
					threadPool.submit(processSocket(s));
				} catch (Exception e) {
					e.printStackTrace();
				}
						
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Returns the runnable where socket is processed
	 * @param socket
	 * @return runnable process to be added to thread pool
	 */
	protected abstract Runnable processSocket(Socket socket) throws Exception;
}
