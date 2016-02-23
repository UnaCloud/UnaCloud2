package uniandes.unacloud.communication;

import java.io.DataInputStream;
import java.net.Socket;

import com.losandes.utils.UnaCloudConstants;

/**
 * Class used to receive messages to manage image files.
 * Send and receive messages to/from agents
 * @author Cesar
 *
 */
public class DataServerSocket extends AbstractServerSocket{	
	
	public DataServerSocket(int listenPort, int threads) {
		super(listenPort, threads);		
	}

	@Override
	protected Runnable processSocket(Socket s) throws Exception {
		DataInputStream ds = new DataInputStream(s.getInputStream());
		int byteOp=ds.read();					
		System.out.println("Request from: "+s+" - operation: "+byteOp);
		if(byteOp==UnaCloudConstants.SEND_IMAGE){
			System.out.println("Start service to send file");
			return new FileTransferTask(s);
		}else if(byteOp==UnaCloudConstants.REQUEST_IMAGE){
			System.out.println("Start service to request file");
			return new FileReceiverTask(s);
		}
		return null;
	}
}