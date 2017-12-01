package uniandes.unacloud.common.net.udp;

import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uniandes.unacloud.common.net.UnaCloudMessage;

/**
 * Class used to receive UDP messages and put the in threads to be executed
 * @author CesarF
 *
 */
public abstract class AbstractUDPMessageReceiver extends Thread {
	
	/**
	 * Port to listen
	 */
    private int port;
    
    /**
     * Thread pool to process sockets in batch
     */
    protected Executor threadPool;
	
    /**
     * Creates a new abstract udp message receiver
     * @param port to listen
     * @param threads quantity
     * @throws SocketException
     */
	public AbstractUDPMessageReceiver(int port, int threads) throws SocketException {
		threadPool = Executors.newFixedThreadPool(threads);		
		this.port = port;
	}
	
	@Override
	public void run() {	
		System.out.println("Start UDP service listening in " + port);
		try (UDPReceiver receiver = new UDPReceiver(port)) {
			while(true) {
				try  {
					UnaCloudMessage message = receiver.getMessage();
					threadPool.execute(processMessage(message));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Responsible to process message and put it in thread pool
	 * @param message
	 */
	public abstract Runnable processMessage(UnaCloudMessage message) throws Exception;

}
