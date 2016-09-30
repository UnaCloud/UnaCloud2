package uniandes.unacloud.agent.communication.download;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import uniandes.unacloud.agent.exceptions.VirtualMachineExecutionException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.entities.Image;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.VirtualMachineImageStatus;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Class responsible for manage download files process from server
 * @author CesarF
 *
 */
public class DownloadImageVirtualMachineTask {
	
	/**
	 * Creates a new image copy requesting images from server
	 * @param image base image
	 * @param copy empty copy
	 * @throws Exception 
	 */
	public static void dowloadImageCopy(Image image,ImageCopy copy,String repository)throws Exception{
		File root=new File(repository+"\\"+image.getId()+"\\base");
		ImageCacheManager.cleanDir(root);
		root.mkdirs();
		final int puerto = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.FILE_SERVER_PORT);
		final String ip=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.FILE_SERVER_IP);
		System.out.println("Connecting to "+ip+":"+puerto);
		
		try(Socket s=new Socket(ip,puerto);DataOutputStream ds=new DataOutputStream(s.getOutputStream())){
			//Sends operation type ID
			System.out.println("Successful connection");
			System.out.println("Operation type 1");
			ds.writeInt(UnaCloudConstants.REQUEST_IMAGE);
			ds.flush();
			//sends image id
			System.out.println("send ID "+image.getId());
			ds.writeLong(image.getId());
			ds.flush();
			//Receives zip elements
			try(ZipInputStream zis=new ZipInputStream(s.getInputStream())){
				System.out.println("Zip open");
				byte[] buffer=new byte[1024*100];
				for(ZipEntry entry;(entry=zis.getNextEntry())!=null;){
					if(entry.getName().equals("unacloudinfo")){
						BufferedReader br=new BufferedReader(new InputStreamReader(zis));
						image.setHypervisorId(br.readLine());
						System.out.println("Hypervisor: "+image.getHypervisorId());
						String mainFile=br.readLine();
						if(mainFile==null){
							throw new VirtualMachineExecutionException(UnaCloudConstants.ERROR_MESSAGE+" image mainFile is null");
						}
						copy.setMainFile(new File(root,mainFile));
						System.out.println("Main: "+mainFile);
						image.setPassword(br.readLine());
						image.setUsername(br.readLine());
						copy.setStatus(VirtualMachineImageStatus.LOCK);
						/*copy.setVirtualMachineName();*/br.readLine();
						image.setConfiguratorClass(br.readLine());
						System.out.println("config: "+image.getConfiguratorClass());
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
