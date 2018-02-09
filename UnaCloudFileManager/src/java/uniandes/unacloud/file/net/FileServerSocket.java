package uniandes.unacloud.file.net;

import java.io.DataInputStream;
import java.net.Socket;

import uniandes.unacloud.common.enums.FileEnum;
import uniandes.unacloud.common.net.tcp.AbstractTCPServerSocket;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.file.net.task.FileImageReceiverTask;
import uniandes.unacloud.file.net.task.FileTransferTask;

/**
 * Class used to receive messages to manage image files.
 * Sends or receives files from agents.
 * @author CesarF
 */
public class FileServerSocket extends AbstractTCPServerSocket {	
	
	/**
	 * Construct a new File Server socket
	 * @param listenPort
	 * @param threads
	 */
	public FileServerSocket(int listenPort, int threads) {
		super(listenPort, threads);		
	}

	@Override
	protected Runnable processSocket(Socket s) throws Exception {
		DataInputStream ds = new DataInputStream(s.getInputStream());
		int byteOp = ds.readInt();					
		System.out.println("Request from: " + s.getInetAddress() + " - operation: " + byteOp);
		if (byteOp == UnaCloudConstants.REQUEST_IMAGE) {//Agent request for image
			System.out.println("Start service to send file");
			return new FileTransferTask(s);
		} else if (byteOp == UnaCloudConstants.SEND_IMAGE) {//Agent sends an image
			System.out.println("Start service to request file");
			FileEnum type = FileEnum.getFileEnum(ds.readUTF());
			if(type == FileEnum.IMAGE)
				return new FileImageReceiverTask(s);			
		}
		return null;
	}
}