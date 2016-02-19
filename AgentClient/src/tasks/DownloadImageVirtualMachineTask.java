package tasks;

import hypervisorManager.ImageCopy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import virtualMachineManager.ImageCacheManager;
import virtualMachineManager.entities.Image;
import virtualMachineManager.entities.VirtualMachineImageStatus;

import com.losandes.utils.ClientConstants;
import com.losandes.utils.Constants;

import domain.VariableManager;
import exceptions.VirtualMachineExecutionException;

public class DownloadImageVirtualMachineTask {
	
	/**
	 * Creates a new image copy
	 * @param image base image
	 * @param copy empty copy
	 * @throws Exception 
	 */
	public static void dowloadImageCopy(Image image,ImageCopy copy,String repository)throws Exception{
		File root=new File(repository+"\\"+image.getId()+"\\base");
		ImageCacheManager.cleanDir(root);
		root.mkdirs();
		final int puerto = VariableManager.getInstance().getGlobal().getIntegerVariable(ClientConstants.FILE_SERVER_PORT);
		final String ip=VariableManager.getInstance().getGlobal().getStringVariable(ClientConstants.FILE_SERVER_IP);
		System.out.println("Connecting to "+ip+":"+puerto);
		try(Socket s=new Socket(ip,puerto);DataOutputStream ds=new DataOutputStream(s.getOutputStream())){
			System.out.println("Successful connection");
			System.out.println("Operation type 1");
			ds.write(Constants.REQUEST_IMAGE);
			ds.flush();
			System.out.println("send ID");
			ds.writeLong(image.getId());
			ds.flush();
			try(ZipInputStream zis=new ZipInputStream(s.getInputStream())){
				System.out.println("Zip open");
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
		} catch (VirtualMachineExecutionException e1) {
			throw e1;
		}catch (Exception e) {
			throw new VirtualMachineExecutionException("Error opening connection",e);
		}
	}
}
