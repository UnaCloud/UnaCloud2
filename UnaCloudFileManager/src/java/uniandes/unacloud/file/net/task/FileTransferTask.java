package uniandes.unacloud.file.net.task;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.common.net.tcp.AbstractTCPSocketProcessor;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.db.ImageFileManager;
import uniandes.unacloud.file.db.entities.ImageFileEntity;

/**
 * This class sends files to agent when an agent doesn't have image in its cache folder.
 * @author CesarF
 *
 */
public class FileTransferTask extends AbstractTCPSocketProcessor {
	
	public FileTransferTask(Socket s) {
		super(s);
		System.out.println("Working " + s.getRemoteSocketAddress());
	}

	@Override
	public void processMessage(Socket s) throws Exception {
		try (Socket ss = s; DataInputStream ds = new DataInputStream(s.getInputStream()); OutputStream os = s.getOutputStream(); ) {			
			
			ZipOutputStream zos = new ZipOutputStream(os);
			long imageId = ds.readLong();
			System.out.println("\tWorking " + imageId);	
			
			TransmissionProtocolEnum protocol = TransmissionProtocolEnum.getEnum(ds.readUTF());
			
			ImageFileEntity image = null;
			try (Connection con = FileManager.getInstance().getDBConnection();) {
				image = ImageFileManager.getImageWithFile(imageId, ImageEnum.AVAILABLE, false, true, con);
			} catch (Exception e) {
				e.printStackTrace();
			}
						
			if (image != null) {
				System.out.println(image + " - " + imageId + " - " + image.getState());
				final byte[] buffer = new byte[1024 * 100];
				System.out.println("\t Sending files " + image.getMainFile());
				File file = null;
				
				if (protocol == TransmissionProtocolEnum.TCP)	
					file = image.getFileConversor().getZipFile();			
				else if (protocol == TransmissionProtocolEnum.P2P)
					file = image.getFileConversor().getTorrentFile();	
				
				System.out.println("\tprocessing: " + file.getName());
				zos.putNextEntry(new ZipEntry(file.getName()));					
				try (FileInputStream fis = new FileInputStream(file)) {
					for (int n; (n = fis.read(buffer)) != -1;)
						zos.write(buffer,0,n);
				}
				System.out.println("Files sent " + image.getMainFile());
				zos.closeEntry();
				
				zos.putNextEntry(new ZipEntry("unacloudinfo"));
				PrintWriter pw = new PrintWriter(zos);
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