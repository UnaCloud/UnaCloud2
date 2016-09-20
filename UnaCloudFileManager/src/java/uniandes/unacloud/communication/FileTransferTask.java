package uniandes.unacloud.communication;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import unacloud.share.enums.VirtualMachineImageEnum;
import uniandes.unacloud.FileManager;
import uniandes.unacloud.db.VirtualMachineImageManager;
import uniandes.unacloud.db.entities.VirtualImageFileEntity;

import com.losandes.utils.Constants;

/**
 * This class sends files to agent when an agent doesn't have image in its cache folder.
 * @author CesarF
 *
 */
public class FileTransferTask implements Runnable{
	Socket s;	
	public FileTransferTask(Socket s) {
		System.out.println("Working "+s.getRemoteSocketAddress());
		this.s = s;
	}
	@Override
	public void run() {		
		try(Socket ss=s;DataInputStream ds=new DataInputStream(s.getInputStream());OutputStream os=s.getOutputStream();Connection con = FileManager.getInstance().getDBConnection();){			
			ZipOutputStream zos=new ZipOutputStream(os);
			long imageId=ds.readLong();
			System.out.println("\tWorking "+imageId);
			VirtualImageFileEntity image = VirtualMachineImageManager.getVirtualImageWithFile(imageId, VirtualMachineImageEnum.AVAILABLE, false,true, con);
			if(image!=null){
				System.out.println(image+" - "+imageId+" - "+image.getState());
				final byte[] buffer=new byte[1024*100];
				System.out.println("\t Sending files "+image.getMainFile());
				for(java.io.File f:new java.io.File(image.getMainFile()).getParentFile().listFiles())if(f.isFile()){
					System.out.println("\tprocessing: "+f.getName());
					zos.putNextEntry(new ZipEntry(f.getName()));
					
					try(FileInputStream fis=new FileInputStream(f)){
						for(int n;(n=fis.read(buffer))!=-1;)zos.write(buffer,0,n);
					}
					zos.closeEntry();
				}
				System.out.println("Files sent "+image.getMainFile());
				zos.putNextEntry(new ZipEntry("unacloudinfo"));
				//TODO improve this to manage hypervisors
				PrintWriter pw=new PrintWriter(zos);
				if(image.getMainFile().endsWith("vmx"))
					pw.println(Constants.VM_WARE_WORKSTATION);
				else if(image.getMainFile().endsWith("vbox"))
					pw.println(Constants.VIRTUAL_BOX);
				else if(image.getMainFile().endsWith("img"))
					pw.println(Constants.QEMU_KVM);
				pw.println(new File(image.getMainFile()).getName());
				pw.println(image.getPassword());
				pw.println(image.getUser());
				pw.println(image.getName());
				//System.out.println("en el servidor el configurator class es "++" "+image.getName());
				pw.println(image.getConfigurer());
				pw.flush();
				
				zos.closeEntry();
				zos.flush();
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
