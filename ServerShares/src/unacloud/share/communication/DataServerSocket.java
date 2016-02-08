package unacloud.share.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class used to control Serversocket communication
 * @author Cesar
 *
 */
public abstract class DataServerSocket extends Thread{
	
	/**
	 * Configured port
	 */
	private int listenPort;
	
	public DataServerSocket(int listenPort) {
		this.listenPort = listenPort;
	}
	
	/**
	 * Loop method to receive socket
	 */
	@Override
	public void run(){
		System.out.println("starting ss on port "+listenPort);
		try(ServerSocket ss = new ServerSocket(listenPort)){
			while(true){
				Socket s=ss.accept();
				processRequest(s);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used to process socket request
	 * @param socket
	 */
	public abstract void processRequest(Socket socket);

}
