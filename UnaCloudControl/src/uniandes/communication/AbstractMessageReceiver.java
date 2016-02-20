package uniandes.communication;

import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.losandes.utils.Constants;

import communication.UnaCloudDataSenderUDP;
import communication.UnaCloudMessageUDP;

/**
 * Class used to receive UDP messages and put the in threads to be executed
 * @author Cesar
 *
 */
public abstract class AbstractMessageReceiver extends Thread{
	
    private UnaCloudDataSenderUDP receiver;
    protected ExecutorService threadPool=Executors.newFixedThreadPool(Constants.AGENT_QUANTITY_MESSAGE);
	
	public AbstractMessageReceiver(int port) throws SocketException {
		receiver = new UnaCloudDataSenderUDP();
		receiver.enableReceiver(port);
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
