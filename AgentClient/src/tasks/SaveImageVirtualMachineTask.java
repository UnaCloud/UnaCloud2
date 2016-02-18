package tasks;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import reportManager.ServerMessageSender;

import com.andes.enums.VirtualMachineExecutionStateEnum;
import com.losandes.utils.ClientConstants;
import com.losandes.utils.Constants;
import com.losandes.utils.VariableManager;

import exceptions.VirtualMachineExecutionException;
import virtualMachineManager.ImageCacheManager;
import virtualMachineManager.PersistentExecutionManager;
import virtualMachineManager.entities.VirtualMachineExecution;

public class SaveImageVirtualMachineTask implements Runnable{

	VirtualMachineExecution machineExecution;
	String secureToken;
	
	public SaveImageVirtualMachineTask(VirtualMachineExecution machineExecution, String secureToken) {
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
			final int puerto = VariableManager.global.getIntValue(ClientConstants.FILE_SERVER_PORT);
			final String ip=VariableManager.global.getStringValue(ClientConstants.FILE_SERVER_IP);
			System.out.println("Connecting to "+ip+":"+puerto);
			try(Socket s=new Socket(ip,puerto);OutputStream os=s.getOutputStream()){
				DataOutputStream ds=new DataOutputStream(os);
				System.out.println("Connection succesfull");
				ds.write(Constants.SEND_IMAGE);
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
						if(f.getName().endsWith("vmx")||f.getName().endsWith("vbox")||f.getName().endsWith("vdi")){
							System.out.println("\tSending: "+f.getName());
							zos.putNextEntry(new ZipEntry(f.getName()));
								
							try(FileInputStream fis=new FileInputStream(f)){
								for(int n;(n=fis.read(buffer))!=-1;)zos.write(buffer,0,n);
							}
							zos.closeEntry();
						}					
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
			
			System.out.println("Delete Image");
			PersistentExecutionManager.removeExecution(machineExecution.getId(), false);
			if(machineExecution.getImage().getMainFile().getParentFile().equals("base")){
				ImageCacheManager.deleteImage(machineExecution.getId());
			}
			PersistentExecutionManager.cleanDir(machineExecution.getImage().getMainFile().getParentFile());
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
