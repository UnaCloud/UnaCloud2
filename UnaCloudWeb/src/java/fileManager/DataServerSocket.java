package fileManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataServerSocket extends Thread{
	private static DataServerSocket instance;
	private ExecutorService threadPool=Executors.newFixedThreadPool(3);
	private int listenPort;
	public DataServerSocket(int listenPort) {
		this.listenPort = listenPort;
	}
	public static void startServices(int port){
		if(instance==null){
			System.out.println("DataServerSocket on port "+port);
			instance=new DataServerSocket(port);
			instance.start();
		}
	}
	@Override
	public void run(){
		System.out.println("starting ss on port "+listenPort);
		try(ServerSocket ss = new ServerSocket(listenPort)){
			while(true){
				Socket s=ss.accept();
				System.out.println("Communication from "+s);
				try {		
					DataInputStream ds = new DataInputStream(s.getInputStream());
					int byteOp=ds.read();					
					System.out.println("Solicitud de: "+s+" - operacion: "+byteOp);
					if(byteOp==1){
						System.out.println("Comienzo servicio de envio de archivo");
						threadPool.submit(new FileTransferTask(s));
					}else if(byteOp==2){
						System.out.println("Comienzo servicio de recepcion de archivo");
						threadPool.submit(new FileReceiverTask(s));
					}
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}