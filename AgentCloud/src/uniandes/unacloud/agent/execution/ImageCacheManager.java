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

import uniandes.unacloud.agent.communication.download.DownloadImageTask;
import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.entities.Image;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.ImageStatus;
import uniandes.unacloud.agent.platform.Platform;
import uniandes.unacloud.agent.platform.PlatformFactory;
import uniandes.unacloud.agent.system.OperatingSystem;
import uniandes.unacloud.agent.utils.SystemUtils;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.common.utils.RandomUtils;
import static uniandes.unacloud.common.utils.UnaCloudConstants.*;

/**
 * Responsible to manage list of images in cache
 * @author Clouder

 */
public class ImageCacheManager {
	
	
	private static String machineRepository=VariableManager.getInstance().getLocal().getStringVariable(VM_REPO_PATH);
	private static File imageListFile=new File("imageList");
	private static Map<Long,Image> imageList=null;
	
	/**
	 * Returns a free copy of the image
	 * @param imageId image Id 
	 * @return image available copy
	 */
	public static ImageCopy getFreeImageCopy(long imageId, int tipo, long executionId) throws ExecutionException {
		System.out.println("getFreeImageCopy "+imageId);
		Image vmi=getImage(imageId);
		ImageCopy source,dest;
		synchronized (vmi){
			System.out.println("has "+vmi.getImageCopies().size()+" copies");
			if(vmi.getImageCopies().isEmpty()){
				ImageCopy copy=new ImageCopy();
				try{
					DownloadImageTask.dowloadImageCopy(vmi,copy,machineRepository, tipo, executionId);					
					saveImages();
				}catch(ExecutionException ex){
					ex.printStackTrace();
					throw ex;
				}catch(Exception ex){
					ex.printStackTrace();
					throw new ExecutionException("Error downloading image -> "+ex.getMessage(),ex);
				}
				System.out.println(" downloaded");
				return copy;
			}else{
				for(ImageCopy copy:vmi.getImageCopies()){
					if(copy.getStatus()==ImageStatus.FREE){
						copy.setStatus(ImageStatus.LOCK);
						System.out.println(" Using free");
						return copy;
					}
				}
				source=vmi.getImageCopies().get(0);
				final String vmName="v"+RandomUtils.generateRandomString(9);
				dest=new ImageCopy();
				dest.setImage(vmi);
				vmi.getImageCopies().add(dest);
				File root=new File(machineRepository+OperatingSystem.PATH_SEPARATOR+imageId+OperatingSystem.PATH_SEPARATOR+vmName);
				if(source.getMainFile().getName().contains(".")){
					String[] fileParts = source.getMainFile().getName().split("\\.");
					dest.setMainFile(new File(root,vmName+"."+fileParts[fileParts.length-1]));
				}
				else
					dest.setMainFile(new File(root,vmName));
				dest.setStatus(ImageStatus.LOCK);
				saveImages();
				SystemUtils.sleep(2000);
			}
		}
		System.out.println(" clonning");
		return source.cloneCopy(dest);
	}
	/**
	 * returns or creates an image
	 * @param imageId image Id
	 * @return desired image
	 */
	private synchronized static Image getImage(long imageId){
		loadImages();
		Image vmi=imageList.get(imageId);
		if(vmi==null){
			vmi=new Image();
			vmi.setId(imageId);
			imageList.put(imageId,vmi);
			saveImages();
		}
		return vmi;
	}
	
	/**
	 * Unlocks a freed image
	 * @param vmiCopy image to be freed
	 */
	public synchronized static void freeLockedImageCopy(ImageCopy vmiCopy){
		vmiCopy.setStatus(ImageStatus.FREE);
	}
	
	
	
	/**
	 * Removes a directory from physical machine disk
	 * @param f file or directory to be deleted
	 */
	public static void cleanDir(File f){
		if(f.isDirectory())for(File r:f.listFiles())cleanDir(r);
		System.out.println("\t\t"+f+": "+f.delete());
	}
	
	/**
	 * Removes all images for physical machine disk
	 * @return operation confirmation
	 */
	public static synchronized String clearCache(){
		System.out.println("clearCache");
		loadImages();
		imageList.clear();
		try{	
			try {				
				for(Image image: imageList.values())
					for(ImageCopy copy: image.getImageCopies())
						PlatformFactory.getPlatform(image.getPlatformId()).unregisterImage(copy);
			} catch (Exception e) {
			}					
			for(File f:new File(machineRepository).listFiles())cleanDir(f);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		saveImages();
		return SUCCESSFUL_OPERATION;
	}
	
	
	/**
	 * Removes an image from cache in repository
	 * @return response
	 */
	public static synchronized String clearImageFromCache(Long imageId){
		System.out.println("clearCache for machine "+imageId);
		loadImages();
		Image vmi=imageList.get(imageId);		
		if(vmi!=null){
			try {
				for(ImageCopy copy: vmi.getImageCopies()){
					Platform platform=PlatformFactory.getPlatform(vmi.getPlatformId());
					platform.unregisterImage(copy);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}			
			imageList.remove(imageId);
			saveImages();
		}
		File folder = new File(machineRepository+OperatingSystem.PATH_SEPARATOR+imageId);
		System.out.println("\tDelete: "+folder);
		if(folder.exists())
			for(File root:new File(machineRepository+OperatingSystem.PATH_SEPARATOR+imageId).listFiles())cleanDir(root);
		return SUCCESSFUL_OPERATION;
	}
	
	/**
	 * Saves the images data in a file
	 */
	private static synchronized void saveImages(){
		try(ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(imageListFile))){
			oos.writeObject(imageList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads image info from file
	 */
	@SuppressWarnings("unchecked")
	private static void loadImages(){
		if(imageList==null){
			imageList=new TreeMap<>();
			try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(imageListFile))){
				imageList=(Map<Long,Image>)ois.readObject();
				for(Image im:imageList.values())for(ImageCopy copy:im.getImageCopies()){
					copy.setStatus(ImageStatus.FREE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	

	/**
	 * Deletes an image
	 * @param imageId
	 */
	public static void deleteImage(Long imageId){
		loadImages();
		Image vmi=imageList.get(imageId);
		if(vmi!=null){
			imageList.remove(imageId);
			saveImages();
		}
	}
	
	/**
	 * Returns the list of current images 
	 * @return list of images
	 */
	public static List<Long> getCurrentImages(){
		loadImages();
		List<Long> ids = new ArrayList<Long>();
		ids.addAll(imageList.keySet());
		return ids;
	}
}
