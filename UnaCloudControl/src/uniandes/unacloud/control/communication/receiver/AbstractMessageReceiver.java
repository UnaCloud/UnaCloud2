package uniandes.unacloud.control.communication.receiver;

import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uniandes.unacloud.common.com.UnaCloudDataSenderUDP;
import uniandes.unacloud.common.com.messages.udp.UnaCloudMessageUDP;

/**
 * Class used to receive UDP messages and put the in threads to be executed
 * @author CesarF
 *
 */
public abstract class AbstractMessageReceiver extends Thread{
	
    private UnaCloudDataSenderUDP receiver;
    protected ExecutorService threadPool;
	
	public AbstractMessageReceiver(int port, int threads) throws SocketException {
		threadPool=Executors.newFixedThreadPool(threads);
		receiver = new UnaCloudDataSenderUDP();
		receiver.enableReceiver(port);
		System.out.println("listening in "+port);
	}
	
	@Override
	public void run() {
		
		while(true){
			try {
				UnaCloudMessageUDP message = receiver.getMessage();
				processMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Responsible to process message and put it in thread pool
	 * @param message
	 */
	public abstract void processMessage(UnaCloudMessageUDP message);

}
