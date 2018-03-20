package uniandes.unacloud.agent.execution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.domain.Image;
import uniandes.unacloud.agent.execution.domain.ImageCopy;
import uniandes.unacloud.agent.execution.domain.ImageStatus;
import uniandes.unacloud.agent.host.system.OperatingSystem;
import uniandes.unacloud.agent.net.download.DownloadImageTask;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.net.torrent.TorrentClient;
import uniandes.unacloud.agent.platform.PlatformFactory;
import uniandes.unacloud.agent.utils.SystemUtils;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.utils.file.FileProcessor;
import uniandes.unacloud.utils.security.HashGenerator;
import static uniandes.unacloud.common.utils.UnaCloudConstants.*;

/**
 * Responsible to manage list of images in cache
 * @author Clouder
 */
public class ImageCacheManager {
	
	/**
	 * Path of current image repository
	 */
	private static final String machineRepository = VariableManager.getInstance().getLocal().getStringVariable(VM_REPO_PATH);
	
	/**
	 * Represents file where image list is stored
	 */
	private static final File imageListFile = new File("imageList");
	
	/**
	 * Represents list of images currently stored in repository
	 */
	private static Map<Long, Image> imageList = null;
	
	/**
	 * Returns a free copy of the image
	 * @param imageId image Id 
	 * @return image available copy
	 * @throws Exception 
	 */
	public static ImageCopy getFreeImageCopy(Execution execution, TransmissionProtocolEnum type) throws Exception {
		System.out.println("\tgetFreeImageCopy " + execution.getImageId());
		Image vmi = getImage(execution.getImageId());
		ImageCopy source;
		ImageCopy dest;
		synchronized (vmi) {
			System.out.println("\thas " + vmi.getImageCopies().size() + " copies");
			if (vmi.getImageCopies().isEmpty()) {
				ImageCopy copy = new ImageCopy();
				try {
					ServerMessageSender.reportExecutionState(execution.getId(), ExecutionProcessEnum.REQUEST, "Start Transmission");
					DownloadImageTask.downloadImageCopy(vmi, copy, machineRepository, type);
					saveImages();
				} catch (ExecutionException ex) {
					ex.printStackTrace();
					throw ex;
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new ExecutionException("Error downloading image " + ex.getMessage(), ex);
				}
				System.out.println("\t\t downloaded");
				ServerMessageSender.reportExecutionState(execution.getId(), ExecutionProcessEnum.SUCCESS, "Start configuring");
				return copy;
			} 
			else {
				for (ImageCopy copy : vmi.getImageCopies()) {
					if (copy.getStatus() == ImageStatus.FREE) {
						copy.setStatus(ImageStatus.LOCK);
						System.out.println("\t Using free");
						ServerMessageSender.reportExecutionState(execution.getId(), ExecutionProcessEnum.SUCCESS, "Start configuring");
						return copy;
					}
				}
				System.out.println("\t No copy is free");
				source = vmi.getImageCopies().get(0);
				final String vmName = "v" + HashGenerator.randomString(9);
				dest = new ImageCopy();
				dest.setImage(vmi);
				vmi.getImageCopies().add(dest);
				File root = new File(machineRepository + OperatingSystem.PATH_SEPARATOR + vmi.getId() + OperatingSystem.PATH_SEPARATOR + vmName);
				if (source.getMainFile().getExecutableFile().getName().contains(".")) {
					String[] fileParts = source.getMainFile().getExecutableFile().getName().split("\\.");
					dest.setMainFile(new File(root, vmName + "." + fileParts[fileParts.length-1]));
				}
				else
					dest.setMainFile(new File(root, vmName));
				dest.setStatus(ImageStatus.LOCK);
				saveImages();
				ServerMessageSender.reportExecutionState(execution.getId(), ExecutionProcessEnum.SUCCESS, "Start configuring");
				SystemUtils.sleep(2000);
			}
		}
		System.out.println("\tclonning");
		return source.cloneCopy(dest);
	}
	/**
	 * returns or creates an image
	 * @param imageId image Id
	 * @return desired image
	 */
	private synchronized static Image getImage(long imageId) {
		loadImages();
		Image vmi = imageList.get(imageId);
		if (vmi == null) {
			vmi = new Image();
			vmi.setId(imageId);
			imageList.put(imageId,vmi);
			saveImages();
		}
		return vmi;
	}
	
	/**
	 * Unlocks an image copy
	 * @param vmiCopy image copy to be free
	 */
	public synchronized static void freeLockedImageCopy(ImageCopy vmiCopy) {
		System.out.println("\t break free " + vmiCopy.getMainFile().getFilePath());
				
		Image image = ImageCacheManager.getImage(vmiCopy.getImage().getId());
		for (ImageCopy imC: image.getImageCopies())
			if (imC.getImageName().equals(vmiCopy.getImageName()))
				imC.setStatus(ImageStatus.FREE);
		saveImages();
	}
		
	/**
	 * Removes all images from physical machine disk
	 * @return operation confirmation
	 */
	public static synchronized UnaCloudResponse clearCache() {
		System.out.println("clearCache");
		loadImages();		
		try {	
			try {				
				for (Image image: imageList.values())
					for (ImageCopy copy: image.getImageCopies()) {
						System.out.println("\tRemove execution: " + copy.getMainFile().getFilePath());
						PlatformFactory.getPlatform(image.getPlatformId()).stopAndUnregister(copy);
						TorrentClient.getInstance().removeTorrent(copy.getMainFile().getTorrentFile());
					}
			} catch (Exception e) {
				e.printStackTrace();
			}					
			for (File f : new File(machineRepository).listFiles()) {
				System.out.println("\tDelete File: " + f.getAbsolutePath());
				FileProcessor.deleteFileSync(f.getAbsolutePath());
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		imageList.clear();
		saveImages();
		return new UnaCloudResponse(SUCCESSFUL_OPERATION, ExecutionProcessEnum.SUCCESS)  ;
	}
	
	
	/**
	 * Removes an image from cache in repository
	 * @return response
	 * @throws Exception 
	 */
	public static synchronized UnaCloudResponse clearImageFromCache(Long imageId) throws Exception {
		System.out.println("clearCache for machine " + imageId);
		loadImages();
		Image vmi = imageList.get(imageId);		
		if (vmi != null) {
			try {
				for (ImageCopy copy : vmi.getImageCopies()) {
					PlatformFactory.getPlatform(vmi.getPlatformId()).stopAndUnregister(copy);
					TorrentClient.getInstance().removeTorrent(copy.getMainFile().getTorrentFile());
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}			
			imageList.remove(imageId);
			saveImages();
		}
		File folder = new File(machineRepository + OperatingSystem.PATH_SEPARATOR + imageId);
		System.out.println("\tDelete: " + folder);
		if (folder.exists())
			for (File root : new File(machineRepository + OperatingSystem.PATH_SEPARATOR + imageId).listFiles())
				FileProcessor.deleteFileSync(root.getAbsolutePath());
		return new UnaCloudResponse(SUCCESSFUL_OPERATION, ExecutionProcessEnum.SUCCESS)  ;
	}
	
	/**
	 * Saves the images data in a file
	 */
	private static synchronized void saveImages() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(imageListFile))) {
			oos.writeObject(imageList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads image info from file
	 */
	@SuppressWarnings("unchecked")
	private static void loadImages() {
		if (imageList == null) {
			imageList = new TreeMap<>();
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(imageListFile))) {
				imageList = (Map<Long,Image>) ois.readObject();
				for (Image im : imageList.values())
					for(ImageCopy copy : im.getImageCopies())
						copy.setStatus(ImageStatus.FREE);				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	

	/**
	 * Deletes an image
	 * @param imageId
	 */
	public static void deleteImage(Long imageId) {
		loadImages();
		Image vmi = imageList.get(imageId);
		if (vmi != null) {
			imageList.remove(imageId);
			saveImages();
		}
	}
	
	/**
	 * Returns the list of current images 
	 * @return list of images
	 */
	public static List<Long> getCurrentImages() {
		loadImages();
		List<Long> ids = new ArrayList<Long>();
		ids.addAll(imageList.keySet());
		return ids;
	}
}
