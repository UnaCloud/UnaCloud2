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

import uniandes.unacloud.agent.communication.download.DownloadImageVirtualMachineTask;
import uniandes.unacloud.agent.exceptions.VirtualMachineExecutionException;
import uniandes.unacloud.agent.execution.entities.Image;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.VirtualMachineImageStatus;
import uniandes.unacloud.agent.hypervisor.Hypervisor;
import uniandes.unacloud.agent.hypervisor.HypervisorFactory;
import uniandes.unacloud.agent.hypervisor.VMwareWorkstation;
import uniandes.unacloud.agent.hypervisor.VirtualBox;
import uniandes.unacloud.agent.utils.SystemUtils;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.utils.Constants;
import uniandes.unacloud.common.utils.RandomUtils;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Responsible to manage list of images in cache
 * @author Clouder

 */
public class ImageCacheManager {
	
	
	static String machineRepository=VariableManager.getInstance().getLocal().getSetStringValue(UnaCloudConstants.VM_REPO_PATH,"E:\\GRID\\");
	private static File imageListFile=new File("imageList");
	private static Map<Long,Image> imageList=null;
	
	/**
	 * Returns a free copy of the image
	 * @param imageId image Id 
	 * @return image available copy
	 */
	public static ImageCopy getFreeImageCopy(long imageId)throws VirtualMachineExecutionException{
		System.out.println("getFreeImageCopy "+imageId);
		Image vmi=getImage(imageId);
		ImageCopy source,dest;
		synchronized (vmi){
			System.out.println("has "+vmi.getImageCopies().size()+" copies");
			if(vmi.getImageCopies().isEmpty()){
				ImageCopy copy=new ImageCopy();
				try{
					DownloadImageVirtualMachineTask.dowloadImageCopy(vmi,copy,machineRepository);
					saveImages();
				}catch(VirtualMachineExecutionException ex){
					throw ex;
				}catch(Exception ex){
					ex.printStackTrace();
					throw new VirtualMachineExecutionException("Error downloading image",ex);
				}
				System.out.println(" downloaded");
				return copy;
			}else{
				for(ImageCopy copy:vmi.getImageCopies()){
					if(copy.getStatus()==VirtualMachineImageStatus.FREE){
						copy.setStatus(VirtualMachineImageStatus.LOCK);
						System.out.println(" Using free");
						return copy;
					}
				}
				source=vmi.getImageCopies().get(0);
				final String vmName="v"+RandomUtils.generateRandomString(9);
				dest=new ImageCopy();
				dest.setImage(vmi);
				vmi.getImageCopies().add(dest);
				File root=new File(machineRepository+"\\"+imageId+"\\"+vmName);
				dest.setMainFile(new File(root,vmName+"."+source.getMainFile().getName().split("\\.")[1]));
				dest.setStatus(VirtualMachineImageStatus.LOCK);
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
		vmiCopy.setStatus(VirtualMachineImageStatus.FREE);
	}
	
	
	
	/**
	 * Removes a directory from physical machine disk
	 * @param f file or directory to be deleted
	 */
	public static void cleanDir(File f){
		if(f.isDirectory())for(File r:f.listFiles())cleanDir(r);
		f.delete();
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
					if(image.getHypervisorId().equals(Constants.VM_WARE_WORKSTATION))
						for(ImageCopy copy: image.getImageCopies())
							((VMwareWorkstation)HypervisorFactory.getHypervisor(Constants.VM_WARE_WORKSTATION)).unregisterVirtualMachine(copy);
				((VirtualBox)HypervisorFactory.getHypervisor(Constants.VIRTUAL_BOX)).unregisterAllVms();
			} catch (Exception e) {
				// TODO: handle exception
			}					
			for(File f:new File(machineRepository).listFiles())cleanDir(f);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		saveImages();
		return "Success";
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
					Hypervisor hypervisor=HypervisorFactory.getHypervisor(vmi.getHypervisorId());
					hypervisor.unregisterVirtualMachine(copy);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(new File(machineRepository+"\\"+imageId).exists())
				for(File root:new File(machineRepository+"\\"+imageId).listFiles())cleanDir(root);
			imageList.remove(imageId);
			saveImages();
		}
		return "Success";
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
					copy.setStatus(VirtualMachineImageStatus.FREE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	

	/**
	 * Delete an image
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
	 * Return the list of current images 
	 * @return list of images
	 */
	public static List<Long> getCurrentImages(){
		loadImages();
		List<Long> ids = new ArrayList<Long>();
		ids.addAll(imageList.keySet());
		return ids;
	}
}
