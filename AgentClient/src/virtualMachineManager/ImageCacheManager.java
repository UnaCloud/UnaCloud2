package virtualMachineManager;

import hypervisorManager.HypervisorFactory;
import hypervisorManager.Image;
import hypervisorManager.ImageCopy;
import hypervisorManager.VirtualBox;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import utils.SystemUtils;
import Exceptions.VirtualMachineExecutionException;

import com.losandes.utils.Constants;
import com.losandes.utils.RandomUtils;
import com.losandes.utils.VariableManager;

public class ImageCacheManager {
	
	
	static String machineRepository=VariableManager.local.getsetStringValue("VM_REPO_PATH","E:\\GRID\\");
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
			System.out.println("Tiene "+vmi.getImageCopies().size()+" copias");
			if(vmi.getImageCopies().isEmpty()){
				ImageCopy copy=new ImageCopy();
				try{
					dowloadImageCopy(vmi,copy);
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
	 * Creates a new image copy
	 * @param image base image
	 * @param copy empty copy
	 */
	private static void dowloadImageCopy(Image image,ImageCopy copy)throws VirtualMachineExecutionException{
		File root=new File(machineRepository+"\\"+image.getId()+"\\base");
		cleanDir(root);
		root.mkdirs();
		final int puerto = VariableManager.global.getIntValue("DATA_SOCKET");
		final String ip=VariableManager.global.getStringValue("CLOUDER_SERVER_IP");
		System.out.println("Conectando a "+ip+":"+puerto);
		try(Socket s=new Socket(ip,puerto);DataOutputStream ds=new DataOutputStream(s.getOutputStream())){
			System.out.println("Conexion exitosa");
			System.out.println("Envio Operacion 1");
			ds.write(1);
			ds.flush();
			System.out.println("Envio ID");
			ds.writeLong(image.getId());
			ds.flush();
			try(ZipInputStream zis=new ZipInputStream(s.getInputStream())){
				System.out.println("Zip abierto");
				byte[] buffer=new byte[1024*100];
				for(ZipEntry entry;(entry=zis.getNextEntry())!=null;){
					if(entry.getName().equals("unacloudinfo")){
						BufferedReader br=new BufferedReader(new InputStreamReader(zis));
						image.setHypervisorId(br.readLine());
						String mainFile=br.readLine();
						if(mainFile==null){
							throw new VirtualMachineExecutionException("Error: image mainFile is null");
						}
						copy.setMainFile(new File(root,mainFile));
						image.setPassword(br.readLine());
						image.setUsername(br.readLine());
						copy.setStatus(VirtualMachineImageStatus.LOCK);
						/*copy.setVirtualMachineName();*/br.readLine();
						image.setConfiguratorClass(br.readLine());
					}else{
						try(FileOutputStream fos=new FileOutputStream(new File(root,entry.getName()))){
							for(int n;(n=zis.read(buffer))!=-1;){
								fos.write(buffer,0,n);
							}						
						}
					}
					zis.closeEntry();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			copy.setImage(image);
			image.getImageCopies().add(copy);
			copy.init();
			saveImages();
		} catch (VirtualMachineExecutionException e1) {
			throw e1;
		}catch (Exception e) {
			throw new VirtualMachineExecutionException("Error opening connection",e);
		}
	}
	
	/**
	 * Removes a directory from physical machine disk
	 * @param f file or directory to be deleted
	 */
	private static void cleanDir(File f){
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
			for(File f:new File(machineRepository).listFiles())cleanDir(f);
			((VirtualBox)HypervisorFactory.getHypervisor(Constants.VIRTUAL_BOX)).unregisterAllVms();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		saveImages();
		return "Successful";
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

	public static void deleteImage(Long imageId){
		loadImages();
		Image vmi=imageList.get(imageId);
		if(vmi!=null){
			imageList.remove(imageId);
			saveImages();
		}
	}
	
}
