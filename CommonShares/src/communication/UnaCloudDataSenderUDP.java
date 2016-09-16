package communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import communication.messages.udp.UnaCloudMessageUDP;

/**
 * Class used to send and receive message using UDP protocol
 * @author CesarF
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
		bufer = new byte[1024*100];
	}
	
	/**
	 * Sends an object as message using UDP protocol
	 * @param message to send
	 * @return true in case send message was successful, false in case not
	 */
	public boolean sendMessage(UnaCloudMessageUDP message){
		try {
			DatagramSocket socketUDP = new DatagramSocket();
			byte[] messageBytes =message.generateByteMessage();			
			InetAddress host = InetAddress.getByName(message.getIp());			
			DatagramPacket packg = new DatagramPacket(messageBytes, messageBytes.length, host, message.getPort());
			socketUDP.send(packg);
			socketUDP.close();
			System.out.println("Send message to: "+message.getIp()+":"+message.getPort()+" - "+message.getType().name()+":"+message.getMessage());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	/**
	 * Receives a message by UDP port
	 * @return udp message
	 */
	public UnaCloudMessageUDP getMessage(){
		if(!receiver)return null;
		try {
			DatagramPacket request = new DatagramPacket(bufer, bufer.length);
			udpReceiver.receive(request);	
			UnaCloudMessageUDP udpMessage = new UnaCloudMessageUDP();
			udpMessage.setMessageByBytes(request.getData());
			udpMessage.setIp(request.getAddress().getHostAddress());
			udpMessage.setPort(0);
			return udpMessage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}     
	}

}
