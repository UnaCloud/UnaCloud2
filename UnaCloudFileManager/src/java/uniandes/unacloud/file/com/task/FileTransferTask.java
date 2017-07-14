package uniandes.unacloud.file.com.task;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import udt.UDTClient;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.db.ImageFileManager;
import uniandes.unacloud.file.db.entities.ImageFileEntity;

/**
 * This class sends files to agent when an agent doesn't have image in its cache folder.
 * @author CesarF
 *
 */
public class FileTransferTask implements Runnable{
	
	private static final int FTP = 1;
	private static final int SMB = 2;
	private static final int TCP = 3;
	private static final int P2P = 4;
	private static final int UDT = 5;
	
	Socket s;	
	public FileTransferTask(Socket s) {
		System.out.println("Working "+s.getRemoteSocketAddress());
		this.s = s;
	}
	@Override
	public void run() {		
		try(Socket ss=s;DataInputStream ds=new DataInputStream(s.getInputStream());DataOutputStream os= new DataOutputStream(s.getOutputStream());){			
						
			long imageId=ds.readLong();
			System.out.println("\tWorking "+imageId);	
			ImageFileEntity image = null;
			try (Connection con = FileManager.getInstance().getDBConnection();) {				
				image = ImageFileManager.getImageWithFile(imageId, ImageEnum.AVAILABLE, false,true, con);
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}			
			
			if(image!=null){
				
				System.out.println(image+" - "+imageId+" - "+image.getState());
				
				File zip = new File(image.getMainFile()+".zip");
				
				os.writeLong(zip.length());
				
				os.writeUTF(zip.getName());
				
				int tipo = ds.readInt();
				
				
				switch (tipo) {							
					case TCP:
						FilenameFilter filterZip = new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.endsWith(".zip");
							}
						};
						sendByTCP(os, zip.getParentFile(), filterZip);
						System.out.println("Files sent zip "+image.getMainFile());
						break;					
					case P2P:
						FilenameFilter filterTorrent = new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.endsWith(".torrent");
							}
						};
						sendByTCP(os, zip.getParentFile(), filterTorrent);
						System.out.println("Files sent torrent");
						break;
					case FTP:
						FilenameFilter filterFTP = new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.endsWith(".ftp");
							}
						};
						File root = new File(System.getProperty(UnaCloudConstants.ROOT_PATH),"agentSources/");
						System.out.println(root);
						sendByTCP(os, root, filterFTP);
						System.out.println("Files sent FTP");
						break;
					case SMB:
						os.writeUTF("\\\\157.253.236.162\\unacloud");
						System.out.println("Files sent SAMBA");
						break;
					case UDT:
						MyUDTThread thread = new MyUDTThread(zip, ss.getInetAddress());
						thread.start();
						System.out.println("Files sent UDT");
						break;
				}					
				
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class MyUDTThread extends Thread {
		
		private File zip;
		private InetAddress ip;
		
		public MyUDTThread(File f, InetAddress ip) {
			this.zip = f;
			this.ip = ip;
		}
		@Override
		public void run() {
			try {
				Date d = new Date();
				FilenameFilter filter = new FilenameFilter() {						
					@Override
					public boolean accept(File dir, String name) {							
						return name.startsWith("Part_");
					}
				};
				int totalFile = zip.getParentFile().listFiles(filter).length;
				if(totalFile == 0) {
					splitFile(zip);		
					totalFile = zip.getParentFile().listFiles(filter).length;
				}
				
				Date inicioTotal = new Date();
			
				int filesSent = 0;
				for(File f: zip.getParentFile().listFiles(filter)){
					
					Socket server = new Socket(ip, 10035);
					DataOutputStream dos = new DataOutputStream(server.getOutputStream());			
					dos.writeUTF(f.getName());			
					System.out.println("\t"+f.getName());
					filesSent++;
					dos.writeInt(totalFile-filesSent);
					System.out.println("\t"+(totalFile-filesSent));						
					
					UDTClient udt = new UDTClient(ip , 10035);
					System.out.println("\t connect to "+ip.getHostAddress());
					udt.connect(ip.getHostAddress(), 10034);
					byte[]buf=new byte[1024*100];
					try(FileInputStream fis=new FileInputStream(f)) {
						while(fis.read(buf)!=-1) {
							udt.send(buf);	
						}
					}
					
					System.out.println("Finish "+d+" - "+new Date()+" "+f.getName());
					udt.flush();
					dos.close();				
					udt.shutdown();
					server.close();
				}
				System.out.println("TERMINE!!!!! "+inicioTotal+" - "+new Date());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	}
	
	private void sendByTCP(DataOutputStream os, File folder, FilenameFilter filter)	throws IOException, FileNotFoundException {
		final byte[] buffer=new byte[1024*100];	
		ZipOutputStream zos=new ZipOutputStream(os);
		for(File f: folder.listFiles(filter)) {
			if(f.isFile()) {			
				System.out.println("\tprocessing: "+f.getName());
				zos.putNextEntry(new ZipEntry(f.getName()));					
				try(FileInputStream fis=new FileInputStream(f)){
					for(int n;(n=fis.read(buffer))!=-1;)zos.write(buffer,0,n);
				}
				zos.closeEntry();
			}
		}		

	}
	
	public static void splitFile(File f) throws IOException {
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