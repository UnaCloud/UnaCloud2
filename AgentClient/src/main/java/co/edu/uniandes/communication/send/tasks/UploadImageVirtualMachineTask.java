package co.edu.uniandes.communication.send.tasks;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.losandes.enums.VirtualMachineExecutionStateEnum;
import com.losandes.utils.UnaCloudConstants;

import co.edu.uniandes.communication.send.report.ServerMessageSender;
import co.edu.uniandes.exceptions.VirtualMachineExecutionException;
import co.edu.uniandes.utils.VariableManager;
import co.edu.uniandes.virtualMachineManager.ImageCacheManager;
import co.edu.uniandes.virtualMachineManager.PersistentExecutionManager;
import co.edu.uniandes.virtualMachineManager.entities.VirtualMachineExecution;


/**
 * Task to send a virtual machine execution files to server
 * @author CesarF
 *
 */
public class UploadImageVirtualMachineTask implements Runnable{

	VirtualMachineExecution machineExecution;
	String secureToken;
	
	public UploadImageVirtualMachineTask(VirtualMachineExecution machineExecution, String secureToken) {
		this.machineExecution = machineExecution;
		this.secureToken = secureToken;
	}
	@Override
	public void run() {
		try {
			System.out.println("Stop execution: "+machineExecution.getId()+", of Image: "+machineExecution.getImageId() );
			PersistentExecutionManager.stopExecution(machineExecution.getId());
			System.out.println("Delete snapshot: "+machineExecution.getId());
			try {
				machineExecution.getImage().deleteSnapshot();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Unregister execution: "+machineExecution.getId());
			PersistentExecutionManager.unregisterExecution(machineExecution.getId());
			//Send files
			final int puerto = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.FILE_SERVER_PORT);
			final String ip=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.FILE_SERVER_IP);
			System.out.println("Connecting to "+ip+":"+puerto);
			try(Socket s=new Socket(ip,puerto);OutputStream os=s.getOutputStream()){
				DataOutputStream ds=new DataOutputStream(os);
				System.out.println("Connection succesful");
				ds.writeInt(UnaCloudConstants.SEND_IMAGE);
				ds.flush();
				System.out.println("Execution "+machineExecution.getId());
				ds.writeLong(machineExecution.getId());
				ds.flush();
				System.out.println("Token "+secureToken);
				ds.writeUTF(secureToken);
				ds.flush();
				ZipOutputStream zos=new ZipOutputStream(ds);
				
				System.out.println("\tSending "+machineExecution.getId());
				final byte[] buffer=new byte[1024*100];
				System.out.println("\tSending files"+machineExecution.getImage().getMainFile());
				
				try {
					for(java.io.File f:machineExecution.getImage().getMainFile().getParentFile().listFiles())if(f.isFile()){
						System.out.println("\tSending: "+f.getName());
						zos.putNextEntry(new ZipEntry(f.getName()));
							
						try(FileInputStream fis=new FileInputStream(f)){
							for(int n;(n=fis.read(buffer))!=-1;)zos.write(buffer,0,n);
						}
						zos.closeEntry();
					}
					System.out.println("Files sent");					
					zos.flush();
					ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), VirtualMachineExecutionStateEnum.FINISHED,"Image has been copied to server");
					
				} catch (Exception e) {
					PersistentExecutionManager.removeExecution(machineExecution.getId(), false);
					ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), VirtualMachineExecutionStateEnum.FAILED,"Error copying images to server");
					throw new VirtualMachineExecutionException("Error deleting images",e);
				}					
				
			}catch (Exception e) {	
				ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), VirtualMachineExecutionStateEnum.FAILED,"Error copying images to server");
				throw new VirtualMachineExecutionException("Error opening connection",e);
			}
			
			System.out.println("Delete Image "+machineExecution.getImage().getMainFile().getParentFile().getAbsolutePath());
			PersistentExecutionManager.removeExecution(machineExecution.getId(), false);
			if(machineExecution.getImage().getMainFile().getParentFile().getName().equals("base")){
				ImageCacheManager.deleteImage(machineExecution.getImageId());
			}
			PersistentExecutionManager.cleanDir(machineExecution.getImage().getMainFile().getParentFile());
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
