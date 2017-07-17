package uniandes.unacloud.file.com.udt;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
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
	private static HashMap<Integer, File> files;
	private final int portClient;
	
	public UDTServer(int port, int threads, int portClient) throws IOException {
		this.port = port;
		this.portClient = portClient;
		threadPool=Executors.newFixedThreadPool(threads);
		FilenameFilter filter = new FilenameFilter() {						
			@Override
			public boolean accept(File dir, String name) {							
				return name.startsWith("Part_");
			}
		};
		File root = new File("/main_repo/Stack_admin/Spark_Hadoop.vbox.zip");
		int totalFile = root.getParentFile().listFiles(filter).length;
		if(totalFile == 0) {
			splitFile(root);		
		}
		int t = 1;
		for(File f: root.getParentFile().listFiles(filter)) files.put(t++, f);
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
				System.out.println("Request from: "+socket.getEndpoint().getLocalAddress());				
				System.out.println("Start "+new Date());
				Socket tcp = new Socket(socket.getEndpoint().getLocalAddress(), portClient);
				DataInputStream ds = new DataInputStream(tcp.getInputStream());
				DataOutputStream ou = new DataOutputStream(tcp.getOutputStream());
				int file = ds.readInt();
				ou.writeUTF(files.get(file).getName());
				
				UDTInputStream isD = socket.getInputStream();	
				try(FileOutputStream fis=new FileOutputStream(files.get(file))){
					for(int n;(n=isD.read(buf))!=-1;){
						fis.write(buf,0,n);
					}
				}
				try {
					isD.close();
					socket.close();
					tcp.close();
				} catch (Exception e) {
					e.printStackTrace();
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void splitFile(File f) throws IOException {
        int partCounter = 1;//I like to name parts from 001, 002, 003, ...
                            //you can change it to 0 if you want 000, 001, ...

        int sizeOfFiles = 1024 * 1024 * 500;// 1MB
        byte[] buffer = new byte[sizeOfFiles];

        String fileName = f.getName();

        //try-with-resources to ensure closing stream
        try (FileInputStream fis = new FileInputStream(f);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            int bytesAmount = 0;
            while ((bytesAmount = bis.read(buffer)) > 0) {
                //write each chunk of data into separate file with different number in name
                String filePartName = String.format("%s.%03d", "Part_"+fileName, partCounter++);
                File newFile = new File(f.getParent(), filePartName);
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, bytesAmount);
                }
            }
        }
    }
}
