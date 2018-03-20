package uniandes.unacloud.common.net.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Responsible to send multiple messages to clients
 * @author CesarF
 *
 */
public class TCPMultipleSender extends Thread {

	/**
	 * Messages to be send
	 */
	private List<UnaCloudMessage> messages;
	
	/**
	 * Processor response
	 */
	private TCPResponseProcessor processor;

	/**
	 * Constructs a new TCP multiple sender
	 */
	public TCPMultipleSender(List<UnaCloudMessage> messageList, TCPResponseProcessor processor) {
		messages = messageList;
		this.processor = processor;
	}
	
	@Override
	public void run() {
		for (UnaCloudMessage unaCloudMessage : messages)
			sendMessage(unaCloudMessage);		
	}
	
	/**
	 * Send message
	 * @param message
	 */
	private void sendMessage(UnaCloudMessage message) {
		System.out.println("Sending message to " + message.getIp() + ":" + message.getPort());
		try (Socket s =  new Socket()) {	
			s.connect(new InetSocketAddress(message.getIp(), message.getPort()), UnaCloudConstants.SOCKET_TIME_OUT);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			oos.writeObject(message);
			oos.flush();
			if (processor != null)
				try {
					processor.attendResponse(ois.readObject(), message);
				} catch (Exception e) {
					System.out.println("Error in machine response; " + message.getIp() );	
					e.printStackTrace();
					processor.attendError(message, e.getMessage());
				}		
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error connecting to " + message.getIp());		
			if (processor != null)
				processor.attendError(message, e.getMessage());				
		}
		try {
			Thread.sleep(500);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
