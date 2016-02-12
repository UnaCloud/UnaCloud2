package communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Class used to send and receive message using UDP protocol
 * @author Cesar
 *
 */
public class UnaCloudDataSenderUDP {
	
	/**
	 * Socket to receive message
	 */
	private DatagramSocket udpReceiver;
	private byte[] bufer;
	private boolean receiver;
	
	/**
	 * Enables receiver socket
	 * @throws SocketException 
	 */	
	public void enableReceiver(int port) throws SocketException{
		udpReceiver = new DatagramSocket(port);
		receiver = true;
		bufer = new byte[1024];
	}
	
	/**
	 * Sends an object as message using UDP protocol
	 * @param message
	 * @param ip
	 * @param port
	 * @return
	 */
	public boolean sendMessage(UnaCloudMessageUDP message){
		try {
			DatagramSocket socketUDP = new DatagramSocket();
			byte[] messageBytes =message.getMessage().getBytes();
			InetAddress host = InetAddress.getByAddress(message.getIp().getBytes());			
			DatagramPacket packg = new DatagramPacket(messageBytes, message.getMessage().length(), host, message.getPort());
			socketUDP.send(packg);
			socketUDP.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	/**
	 * Receives a message by UDP port
	 * @return
	 */
	public UnaCloudMessageUDP getMessage(){
		if(!receiver)return null;
		try {
			DatagramPacket request = new DatagramPacket(bufer, bufer.length);
			udpReceiver.receive(request);
			String message = new String(request.getData(), "UTF-8");
			UnaCloudMessageUDP udpMessage = new UnaCloudMessageUDP(message, request.getAddress().getHostAddress(), 0, request.getAddress().getHostName());
			return udpMessage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}     
	}

}
