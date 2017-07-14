package uniandes.unacloud.file.com.udt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import udt.UDTInputStream;
import udt.UDTServerSocket;
import udt.UDTSocket;

public class UDTServer extends Thread{
	
	private final int port;
	private final ExecutorService threadPool;
	private static HashMap<String, File> files;
	
	public UDTServer(int port, int threads) {
		this.port = port;
		threadPool=Executors.newFixedThreadPool(threads);
		FilenameFilter filter = new FilenameFilter() {						
			@Override
			public boolean accept(File dir, String name) {							
				return name.startsWith("Part_");
			}
		};
		
	}
	
	@Override
	public void run() {
		UDTServerSocket server = null;
		try {
			server = new UDTServerSocket(port);
			while(true){
				UDTSocket socket = server.accept();
				UDTThread th = new UDTThread(socket);
				threadPool.submit(th);				
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(server!=null)server.shutDown();
		}
	}
	
	private class UDTThread extends Thread {
		
		private UDTSocket socket;
		
		public UDTThread(UDTSocket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try {
				byte[]buf=new byte[1024*100];
				UDTInputStream isD = socket.getInputStream();							
				System.out.println("Start "+new Date());
				try(FileOutputStream fis=new FileOutputStream(f)){
					for(int n;(n=isD.read(buf))!=-1;){
						fis.write(buf,0,n);
					}
				}
				try {
					isD.close();
					socket.close();
				} catch (Exception e) {
					// TODO: handle exception
				}	
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
