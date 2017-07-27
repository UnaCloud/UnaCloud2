package uniandes.unacloud.file.net.task;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.share.db.ExecutionManager;
import uniandes.unacloud.share.db.PlatformManager;
import uniandes.unacloud.share.db.entities.PlatformEntity;
import uniandes.unacloud.share.db.entities.ExecutionEntity;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.db.UserManager;
import uniandes.unacloud.file.db.ImageFileManager;
import uniandes.unacloud.file.db.entities.UserEntity;
import uniandes.unacloud.file.db.entities.ImageFileEntity;
import uniandes.unacloud.file.processor.FileProcessor;

/**
 * This class receives files from agent when user requests to save image in server.
 * @author CesarF
 *
 */
public class FileReceiverTask implements Runnable {
	
	/**
	 * Socket to process task
	 */
	private Socket s;	
	
	/**
	 * Creates a new file receiver task
	 * @param s socket to process task
	 */
	public FileReceiverTask(Socket s) {
		System.out.println("Attending " + s.getRemoteSocketAddress());
		this.s = s;
	}
	
	@Override
	public void run() {
		
		String newMainFile = null;
		String message = null;
		try (Socket ss = s; DataInputStream is = new DataInputStream(s.getInputStream());) {
			
			Long execution = is.readLong();
			String token= is.readUTF();
			System.out.println("\tRequest " + execution + " - " + token);
			
			ImageFileEntity image = null;
			UserEntity user = null;
			List<PlatformEntity> platforms = null;
			try (Connection con = FileManager.getInstance().getDBConnection();) {
				image = ImageFileManager.getImageWithFile(token, con);
				if (image != null) {
					user = UserManager.getUser(image.getOwner().getId(), con);
					platforms = PlatformManager.getAll(con);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("\tImage requested " + image);	
			
			if (image != null) {
				String mainFolder = image.getRepository().getRoot() + image.getName() + "_" + user.getUsername() + File.separator;
				boolean received = false;
				Long sizeImage = 0l;
				System.out.println("save in path: " + mainFolder);
				TreeMap<File, String> filesTemp = new TreeMap<File, String>();
				
				try (ZipInputStream zis = new ZipInputStream(is)) {
					
					System.out.println("\tZip open");
					final byte[] buffer = new byte[1024 * 100];
					
					for (ZipEntry entry; (entry = zis.getNextEntry()) != null;) {
						boolean goodExtension = false;
						String mainExtension = null;
						System.out.println("\t\tFile: " + entry.getName());
						for (PlatformEntity hyperv : platforms)
							if (hyperv.validatesExtension(entry.getName())) {		
								goodExtension = true;
								mainExtension = hyperv.getExtension();
								break;
							}	
						if (goodExtension) {
							File tempFile = File.createTempFile(entry.getName(), null);
							try (FileOutputStream fos = new FileOutputStream(tempFile)) {
								for (int n; (n = zis.read(buffer)) != -1;)
									fos.write(buffer, 0, n);
								if (entry.getName().endsWith(mainExtension))
									newMainFile = mainFolder + entry.getName();
							}	
							System.out.println("Save temp " + tempFile);
							filesTemp.put(tempFile, entry.getName());
							tempFile.deleteOnExit();
						}
						zis.closeEntry();
					}
					System.out.println("There are " + filesTemp.size() + ", Delete old files");					
					
					if (filesTemp.size() > 0) {			
						
						FileProcessor.deleteFileSync(mainFolder);						
						File dir = new File(mainFolder);						
						System.out.println("Creates: " + dir.mkdirs());
												
						for (File temp : filesTemp.descendingKeySet()) {
							
							File newFile = FileProcessor.copyFileSync(temp.getAbsolutePath(), mainFolder + filesTemp.get(temp));
							System.out.println("save: " + newFile);
							sizeImage += temp.length();
							temp.delete();
						}
					}				
					received = true;
					System.out.println("reception finished: " + newMainFile);	
					message = "Image has been saved in server";
				} catch (Exception e) {		
				    e.printStackTrace();
				    message = e.getMessage();
				}	
				
				try (Connection con = FileManager.getInstance().getDBConnection();) {
					if (received) {
						ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null, null, null, null, sizeImage, newMainFile, null, null), false, con, false);
						System.out.println("Status changed, process closed");
					} else {
						 ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.UNAVAILABLE, null, null, null, null, null, null, null, null), false, con, false);
						 System.out.println("Error in process, all files should be deleted");
						 for (File tmpFile : filesTemp.keySet())
							tmpFile.delete();	
					}
					ExecutionEntity exe = new ExecutionEntity(execution, 0, 0, null, null, null, ExecutionProcessEnum.SUCCESS, null, message);
					ExecutionManager.updateExecution(exe, con);
					ExecutionManager.breakFreeInterfaces(execution, con, IPEnum.AVAILABLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}