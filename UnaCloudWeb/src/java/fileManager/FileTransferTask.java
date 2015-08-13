package fileManager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.losandes.utils.Constants;

import unacloud2.VirtualMachineImage;
import unacloud2.VirtualMachineImageService;

public class FileTransferTask implements Runnable{
	Socket s;	
	public FileTransferTask(Socket s) {
		System.out.println("Atending "+s.getRemoteSocketAddress());
		this.s = s;
	}
	@Override
	public void run() {
		try(Socket ss=s;DataInputStream ds=new DataInputStream(s.getInputStream());OutputStream os=s.getOutputStream()){
			ZipOutputStream zos=new ZipOutputStream(os);
			long imageId=ds.readLong();
			System.out.println("\tAtendiendo "+imageId);
			VirtualMachineImage image=new VirtualMachineImageService().getImage(imageId);
			final byte[] buffer=new byte[1024*100];
			System.out.println("\tEnviando archivos "+image.getMainFile());
			for(java.io.File f:new java.io.File(image.getMainFile()).getParentFile().listFiles())if(f.isFile()){
				System.out.println("\tAtendiendo: "+f.getName());
				zos.putNextEntry(new ZipEntry(f.getName()));
				
				try(FileInputStream fis=new FileInputStream(f)){
					for(int n;(n=fis.read(buffer))!=-1;)zos.write(buffer,0,n);
				}
				zos.closeEntry();
			}
			System.out.println("Archivos enviados");
			zos.putNextEntry(new ZipEntry("unacloudinfo"));
			
			PrintWriter pw=new PrintWriter(zos);
			if(image.getMainFile().endsWith("vmx"))
				pw.println(Constants.VM_WARE_WORKSTATION);
			else if(image.getMainFile().endsWith("vbox"))
				pw.println(Constants.VIRTUAL_BOX);
			pw.println(new File(image.getMainFile()).getName());
			pw.println(image.getPassword());
			pw.println(image.getUser());
			pw.println(image.getName());
			//System.out.println("en el servidor el configurator class es "++" "+image.getName());
			pw.println(image.getOperatingSystem().getConfigurer());
			pw.flush();
			
			zos.closeEntry();
			zos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}