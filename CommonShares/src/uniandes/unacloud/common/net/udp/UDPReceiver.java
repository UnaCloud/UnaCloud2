package uniandes.unacloud.common.net.udp;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import uniandes.unacloud.common.net.UnaCloudMessage;

public class UDPReceiver implements Closeable {
	
	/**
	 * Socket to receive message
	 */
	private DatagramSocket udpReceiver;
	
	/**
	 * Buffer to send message
	 */
	private byte[] buffer;
	
	/**
	 * Responsible to create receiver
	 * @param port
	 * @throws SocketException
	 */
	public UDPReceiver(int port) throws SocketException {
		udpReceiver = new DatagramSocket(port);
		buffer = new byte[1024*100];
	}
	

	/**
	 * Receives a message by UDP port
	 * @return udp message
	 * @throws IOException 
	 */
	public UnaCloudMessage getMessage() throws IOException {
		DatagramPacket request = new DatagramPacket(buffer, buffer.length);
		udpReceiver.receive(request);	
		UnaCloudMessage udpMessage = new UnaCloudMessage();
		udpMessage.setMessageByBytes(request.getData());
		udpMessage.setIp(request.getAddress().getHostAddress());
		udpMessage.setPort(0);
		return udpMessage;
	}


	@Override
	public void close() throws IOException {
		udpReceiver.close();
	}

}
