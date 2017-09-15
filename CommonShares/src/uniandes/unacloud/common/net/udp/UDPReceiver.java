package uniandes.unacloud.common.net.udp;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
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
	 * @throws ClassNotFoundException 
	 */
	public UnaCloudMessage getMessage() throws IOException, ClassNotFoundException {
		DatagramPacket request = new DatagramPacket(buffer, buffer.length);
		try {
			udpReceiver.receive(request);	
			UnaCloudMessage udpMessage = deserialize(request.getData());
			udpMessage.setIp(request.getAddress().getHostAddress());
			udpMessage.setPort(0);
			return udpMessage;
		} catch (Exception e) {
			System.out.println("\t Error in data: Message from " + request.getAddress().getHostName());
			throw e;
		}
	}


	@Override
	public void close() throws IOException {
		udpReceiver.close();
	}

	private static UnaCloudMessage deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return (UnaCloudMessage) is.readObject();
	}
}
