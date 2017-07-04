package uniandes.unacloud.file.com.task;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.common.utils.Zipper;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.com.torrent.TorrentServer;
import uniandes.unacloud.file.db.ImageFileManager;
import uniandes.unacloud.file.db.entities.ImageFileEntity;

/**
 * This class sends files to agent when an agent doesn't have image in its cache folder.
 * @author CesarF
 *
 */
public class FileTransferTask implements Runnable{
	Socket s;	
	public FileTransferTask(Socket s) {
		System.out.println("Working "+s.getRemoteSocketAddress());
		this.s = s;
	}
	@Override
	public void run() {		
		try(Socket ss=s;DataInputStream ds=new DataInputStream(s.getInputStream());OutputStream os=s.getOutputStream();){			
			
			ZipOutputStream zos=new ZipOutputStream(os);
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
				
				String requester = ds.readUTF();
				
				System.out.println(image+" - "+imageId+" - "+image.getState());
				final byte[] buffer=new byte[1024*100];				
				System.out.println("\t Sending files "+image.getMainFile());
				
				//if (requester.equals("1")) {
					
					FilenameFilter filter = new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.endsWith(".torrent");
						}
					};
					//TODO Only with one deploy
					if (new java.io.File(image.getMainFile()).getParentFile().listFiles(filter).length == 0) {
						File zipParent = new File(image.getMainFile()).getParentFile();
						File zip = new File(image.getMainFile()+".zip");
						Zipper.zipIt(zip, zipParent);
						TorrentServer.getInstance().publishFile(zip);
					}
					for(java.io.File f:new java.io.File(image.getMainFile()).getParentFile().listFiles(filter))if(f.isFile()){
						
						System.out.println("\tprocessing: "+f.getName());
						zos.putNextEntry(new ZipEntry(f.getName()));					
						try(FileInputStream fis=new FileInputStream(f)){
							for(int n;(n=fis.read(buffer))!=-1;)zos.write(buffer,0,n);
						}
						zos.closeEntry();
					}
					System.out.println("Send torrent "+image.getMainFile());
//				}
//				else {
//					for(java.io.File f:new java.io.File(image.getMainFile()).getParentFile().listFiles())if(f.isFile()){
//						
//						System.out.println("\tprocessing: "+f.getName());
//						zos.putNextEntry(new ZipEntry(f.getName()));					
//						try(FileInputStream fis=new FileInputStream(f)){
//							for(int n;(n=fis.read(buffer))!=-1;)zos.write(buffer,0,n);
//						}
//						zos.closeEntry();
//					}
//
//					System.out.println("Files sent "+image.getMainFile());
//				}		
				
				zos.putNextEntry(new ZipEntry("unacloudinfo"));
				PrintWriter pw=new PrintWriter(zos);
				pw.println(image.getPlatform().getConfigurer());
				pw.println(new File(image.getMainFile()).getName());
				pw.println(image.getPassword());
				pw.println(image.getUser());
				pw.println(image.getName());
				pw.println(image.getConfigurer());
				pw.flush();
				
				zos.closeEntry();
				zos.flush();
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}