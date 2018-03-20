package uniandes.unacloud.common.net.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Responsible to send unacloud message
 * @author CesarF
 *
 */
public class TCPSender {
	
	
	/**
	 * Constructor
	 */
	public TCPSender() {
		
	}
	
	/**
	 * Responsible to send message
	 */
	public void sendMessage(UnaCloudMessage message) {
		sendMessage(message, null);
	}
	
	/**
	 * Send message using an object to process response 
	 * @param message
	 * @param processor
	 */
	public void sendMessageWithProcessor(UnaCloudMessage message, TCPResponseProcessor processor) {
		sendMessage(message, processor);
	}
	
	/**
	 * Send message by TCP protocol
	 * @param message
	 * @param processor
	 */
	private void sendMessage(UnaCloudMessage message, TCPResponseProcessor processor) {
		System.out.println("Sending message to " + message.getIp() + ":" + message.getPort() + " message: " + message);
		try (Socket s =  new Socket()) {	
		s.connect(new InetSocketAddress(message.getIp(), message.getPort()), UnaCloudConstants.SOCKET_TIME_OUT);			
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			oos.writeObject(message);
			oos.flush();
			if (processor != null)
				try {
					processor.attendResponse(ois.readObject(), null);
				} catch (Exception e) {
					System.out.println("Error in machine response; " + message.getIp() );	
					e.printStackTrace();
					processor.attendError(e, e.getMessage());
				}				
			s.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error connecting to " + message.getIp());		
			if (processor != null)
				processor.attendError(e, e.getMessage());				
		}
		try {
			Thread.sleep(500);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
