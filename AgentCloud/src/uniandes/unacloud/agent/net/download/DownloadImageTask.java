package uniandes.unacloud.agent.net.download;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.domain.Image;
import uniandes.unacloud.agent.execution.domain.ImageCopy;
import uniandes.unacloud.agent.execution.domain.ImageStatus;
import uniandes.unacloud.agent.host.system.OperatingSystem;
import uniandes.unacloud.agent.net.torrent.TorrentClient;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.utils.file.FileProcessor;
import uniandes.unacloud.utils.file.Zipper;

/**
 * Class responsible for manage download files process from server
 * @author CesarF
 *
 */
public class DownloadImageTask {
	
	/**
	 * Creates a new image copy requesting images from server
	 * @param image base image
	 * @param copy empty copy
	 * @param repository
	 * @param type
	 * @throws Exception
	 */
	public static void downloadImageCopy(Image image, ImageCopy copy, String repository, TransmissionProtocolEnum type) throws Exception {
		
		File root = new File(repository + OperatingSystem.PATH_SEPARATOR + image.getId() + OperatingSystem.PATH_SEPARATOR + "base");
		FileProcessor.deleteFileSync(root.getAbsolutePath());
		root.mkdirs();
		final int puerto = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.FILE_SERVER_PORT);
		final String ip = VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.FILE_SERVER_IP);
		System.out.println("Connecting to " + ip + ":" + puerto + " to download: " + image.getId());
		
		try (Socket s = new Socket(ip, puerto); DataOutputStream ds = new DataOutputStream(s.getOutputStream())) {
			
			//Sends operation type ID
			System.out.println("\tSuccessful connection");
			System.out.println("\tOperation type " + UnaCloudConstants.REQUEST_IMAGE);
			ds.writeInt(UnaCloudConstants.REQUEST_IMAGE);
			ds.flush();
			
			//sends image id
			System.out.println("\tsend ID " + image.getId());
			ds.writeLong(image.getId());
			ds.flush();
			
			System.out.println("\tTransmission protocol:" + type);
			ds.writeUTF(type.name());
			ds.flush();
			
			//Receives zip elements
			try (ZipInputStream zis = new ZipInputStream(s.getInputStream())) {
				System.out.println("\tZip open");
				byte[] buffer = new byte[1024 * 100];
				for (ZipEntry entry; (entry = zis.getNextEntry()) != null;) {
					if (entry.getName().equals("unacloudinfo")) {						
						BufferedReader br = new BufferedReader(new InputStreamReader(zis));
						image.setPlatformId(br.readLine());
						System.out.println("\tPlatform: " + image.getPlatformId());
						String mainFile = br.readLine();
						if (mainFile == null)
							throw new ExecutionException(UnaCloudConstants.ERROR_MESSAGE + " image mainFile is null");		
						copy.setMainFile(new File(root, mainFile));
						System.out.println("\tMain: " + mainFile);
						image.setPassword(br.readLine());
						image.setUsername(br.readLine());
						copy.setStatus(ImageStatus.LOCK);
						/*copy.setVirtualMachineName();*/br.readLine();
						image.setConfiguratorClass(br.readLine());
						System.out.println("\tconfig: " + image.getConfiguratorClass());						
					} 
					else {
						try (FileOutputStream fos = new FileOutputStream(new File(root, entry.getName()))) {
							for(int n; (n = zis.read(buffer)) != -1;)
								fos.write(buffer, 0, n);	
						}
					}
					zis.closeEntry();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if (type == TransmissionProtocolEnum.P2P) 
				TorrentClient.getInstance().downloadAndAnnounceTorrent(copy.getMainFile().getTorrentFile());
					
			Zipper.unzipIt(copy.getMainFile().getZipFile(), root.getAbsolutePath());
			copy.setImage(image);
			image.getImageCopies().add(copy);
			copy.init();
			
		} catch (ExecutionException e1) {
			throw e1;
		} catch (Exception e) {
			throw new ExecutionException("Error opening connection " + e.getMessage(), e);
		}
	}
}
