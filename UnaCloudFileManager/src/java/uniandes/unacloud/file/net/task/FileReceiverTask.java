package uniandes.unacloud.file.net.task;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.share.db.DeploymentManager;
import uniandes.unacloud.share.db.PlatformManager;
import uniandes.unacloud.share.entities.PlatformEntity;
import uniandes.unacloud.share.entities.ExecutionEntity;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.db.UserManager;
import uniandes.unacloud.file.db.ImageFileManager;
import uniandes.unacloud.file.db.entities.UserEntity;
import uniandes.unacloud.file.db.entities.ImageFileEntity;

/**
 * This class receives files from agent when user requests to save image in server.
 * @author CesarF
 *
 */
public class FileReceiverTask implements Runnable {
	
	private Socket s;	
	
	public FileReceiverTask(Socket s) {
		System.out.println("Attending "+s.getRemoteSocketAddress());
		this.s = s;
	}
	
	@Override
	public void run() {
		String mainFile = null;
		String newMainFile = null;
		String message;
		try (Socket ss = s; DataInputStream is = new DataInputStream(s.getInputStream()); Connection con = FileManager.getInstance().getDBConnection();) {
			
			Long execution = is.readLong();
			String token= is.readUTF();
			System.out.println("\tRequest " +execution+" - "+ token);
			ImageFileEntity image = ImageFileManager.getImageWithFile(token, con);
			System.out.println("\tImage requested " + image);	
			
			if (image != null) {
				
				UserEntity user = UserManager.getUser(image.getOwner().getId(), con);
				mainFile = image.getRepository().getRoot() + image.getName() + "_" + user.getUsername() + File.separator;//getMainFile().substring(0,image.getMainFile().lastIndexOf(java.io.File.separatorChar)+1);
				System.out.println("save in path: " + mainFile);
				TreeMap<File, String>filesTemp = new TreeMap<File, String>();
				
				try (ZipInputStream zis = new ZipInputStream(is)) {
					
					System.out.println("\tZip open");
					final byte[] buffer = new byte[1024 * 100];
					// for(ZipEntry entry;(entry=zis.getNextEntry())!=null;){					
					List<PlatformEntity>platforms = PlatformManager.getAll(con);				
					
					for (ZipEntry entry; (entry = zis.getNextEntry()) != null;) {
						boolean goodExtension = false;
						String mainExtension = null;
						System.out.println("\t\tFile: " + entry.getName());
						for (PlatformEntity hyperv : platforms)
							if (hyperv.validatesExtension(entry.getName())){		
								goodExtension = true;
								mainExtension = hyperv.getExtension();
								break;
							}	
						if (goodExtension) {
							File tempFile = File.createTempFile(entry.getName(), null);
							try (FileOutputStream fos = new FileOutputStream(tempFile)) {
								for (int n; (n = zis.read(buffer)) != -1;) {
									fos.write(buffer, 0, n);
								}
								if (entry.getName().endsWith(mainExtension)){
									newMainFile = mainFile + entry.getName();								
								}								
							}	
							System.out.println("Save temp "+tempFile);
							filesTemp.put(tempFile,entry.getName());
							tempFile.deleteOnExit();
						}
						zis.closeEntry();
					}
					System.out.println("There are "+filesTemp.size() + ", Delete old files");
					Long sizeImage = 0l;
					
					if (filesTemp.size() > 0) {			
						
						File dir = new File(mainFile);
						System.out.println(dir + " " + dir.exists());
						System.out.println("Creates: " + dir.mkdirs());
						for (java.io.File f:dir.listFiles())
							if (f.isFile())
								f.delete();						
						System.out.println("Save new files");
						
						for (File temp : filesTemp.descendingKeySet()) {
							File newFile = new File(mainFile, filesTemp.get(temp));
							//newFile.createNewFile();
							System.out.println("save: "+newFile);
							try (FileInputStream streamTemp = new FileInputStream(temp); FileOutputStream ouFile = new FileOutputStream(newFile)) {
								for (int n; (n = streamTemp.read(buffer)) != -1;) {
									ouFile.write(buffer, 0, n);
								}															
							}	
							
							sizeImage += temp.length();
							temp.delete();
						}
					}				
					
					System.out.println("reception finished: " + newMainFile);
					try {
						ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null, null, null, null, sizeImage, newMainFile, null, null), false, con, false);
						message = "Image has been saved in server";
						System.out.println("Status changed, process closed");
					} catch (Exception e) {
						e.printStackTrace();
						 message = e.getMessage();
					}
					
				} catch (Exception e) {		
				    e.printStackTrace();
				    ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.UNAVAILABLE, null, null, null, null, null, null, null, null), false, con, false);
				    message = e.getMessage();
				    for (File tmpFile : filesTemp.keySet()) {
						tmpFile.delete();
					}
				}	
				DeploymentManager.setExecution(new ExecutionEntity(execution, 0, 0, null, new Date(), null, ExecutionStateEnum.FINISHED, null, message), con);
				DeploymentManager.breakFreeInterfaces(execution, con, IPEnum.AVAILABLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}