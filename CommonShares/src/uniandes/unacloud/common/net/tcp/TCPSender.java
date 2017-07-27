package uniandes.unacloud.common.net.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import uniandes.unacloud.common.net.UnaCloudMessage;

/**
 * Responsible to send unacloud message
 * @author CesarF
 *
 */
public class TCPSender{
	
	
	/**
	 * 
	 */
	public TCPSender() {
		
	}
	
	/**
	 * Responsible to send message
	 */
	public void sendMessage(UnaCloudMessage message) {
		sendMessage(message, null);
	}
	
	public void sendMessageWithProcessor(UnaCloudMessage message, TCPResponseProcessor processor) {
		sendMessage(message, processor);
	}
	
	private void sendMessage(UnaCloudMessage message, TCPResponseProcessor processor) {
		try (Socket s =  new Socket(message.getIp(), message.getPort())) {
			System.out.println("Sending message to " + message.getIp() + ":" + message.getPort());
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
			System.out.println("Error connectiong to " + message.getIp());		
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
