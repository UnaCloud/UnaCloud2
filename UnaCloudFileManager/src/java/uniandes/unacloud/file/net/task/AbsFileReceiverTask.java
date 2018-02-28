package uniandes.unacloud.file.net.task;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import uniandes.unacloud.common.net.tcp.AbstractTCPSocketProcessor;

/**
 * This class receives files from agent when user requests to save file in server.
 * @author CesarF
 *
 */
public abstract class AbsFileReceiverTask extends AbstractTCPSocketProcessor {
	
	/**
	 * Creates a new file receiver task
	 * @param s socket to process task
	 */
	public AbsFileReceiverTask(Socket s) {
		super(s);
		System.out.println("Attending " + s.getRemoteSocketAddress());
	}
	

	@Override
	public void processMessage(Socket s) throws Exception {
		File fileFromAgent = null;
		long fileSize = 0;
		boolean success = false;
		//Receiving file from agent
		try (Socket ss = s; DataInputStream is = new DataInputStream(s.getInputStream());) {
			
			String token = is.readUTF();
			
			if (validateToken(token)) {
				fileSize = is.readLong();
				System.out.println("\tRequest " + token);
				String mainFolder = getRepoPath();
				try (ZipInputStream zis = new ZipInputStream(is)) {
					
					System.out.println("\tZip open");
					final byte[] buffer = new byte[1024 * 100];
					ZipEntry entry = zis.getNextEntry();
					System.out.println("\t\tFile: " + entry.getName());
					if(!new File(mainFolder).exists())
						new File(mainFolder).mkdirs();
					fileFromAgent = new File(mainFolder + entry.getName());
					try (FileOutputStream fos = new FileOutputStream(fileFromAgent)) {
						for (int n; (n = zis.read(buffer)) != -1;)
							fos.write(buffer, 0, n);							
					}	
					System.out.println("Reception finished " + fileFromAgent);
					zis.closeEntry();				
					success = true;
				} catch (Exception e) {		
				    e.printStackTrace();
				    success = false;
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		if (success)
			successReceive(fileFromAgent, fileSize);
		else 
			failedReceive();		
	}
	
	public abstract boolean validateToken(String token);
	
	public abstract void successReceive(File file, long originalSize);
	
	public abstract void failedReceive();
	
	public abstract String getRepoPath();
}