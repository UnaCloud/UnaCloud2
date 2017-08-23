package uniandes.unacloud.common.net.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import uniandes.unacloud.common.net.UnaCloudMessage;

/**
 * Responsible to send messages in UDP
 * @author CesarF
 *
 */
public class UDPSender {
	
	
	/**
	 * Responsible to creates a sender
	 */
	public UDPSender() {
		
	}
	
	/**
	 * Sends an object as message using UDP protocol
	 * @param message to send
	 * @return true in case send message was successful, false in case not
	 */
	public boolean sendMessage(UnaCloudMessage message) {
		try {
			DatagramSocket socketUDP = new DatagramSocket();
			byte[] messageBytes = message.generateByteMessage();			
			InetAddress host = InetAddress.getByName(message.getIp());			
			DatagramPacket packg = new DatagramPacket(messageBytes, 1024*100, host, message.getPort());
			socketUDP.send(packg);
			socketUDP.close();
			System.out.println("Send message to: " + message.getIp() + ":" + message.getPort() + " - " + message.getType() + ":" + message.getMessage());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}

}
