package fileManager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import unacloud2.VirtualMachineImageEnum;
import unacloud2.VirtualMachineImageService;

public class FileReceiverTask implements Runnable{
	Socket s;	
	VirtualMachineImageService vs;
	public FileReceiverTask(Socket s) {
		System.out.println("Atending "+s.getRemoteSocketAddress());
		this.s = s;
		vs = new VirtualMachineImageService();
	}
	@Override
	public void run() {
		String mainFile=null;
		String newMainFile = null;
		try(Socket ss=s; DataInputStream is = new DataInputStream(s.getInputStream())) {
			String token= is.readUTF();
			System.out.println("\tAtendiendo " + token);
			Long vmId = (Long) vs.getByToken(token);
			//VirtualMachineImage vm = vs.getImage(vmId);
			boolean exist =  vs.imageExist(vmId);
			System.out.println("\tAtendiendo " + vmId);			
		
			if (exist) {
				String path = vs.getPathImage(vmId);
				mainFile=path.substring(0,path.lastIndexOf(java.io.File.separatorChar)+1);
				System.out.println("Voy a guardar en la ruta: "+mainFile);
				try(ZipInputStream zis = new ZipInputStream(is)) {
					System.out.println("\tZip abierto ");
					final byte[] buffer = new byte[1024 * 100];
					// for(ZipEntry entry;(entry=zis.getNextEntry())!=null;){
					TreeMap<File, String>filesTemp = new TreeMap<File, String>();
					for (ZipEntry entry; (entry = zis.getNextEntry()) != null;) {
						System.out.println("\t\tLlego: " + entry.getName());
						if(entry.getName().endsWith(".vmx") || entry.getName().endsWith(".vbox")|| entry.getName().endsWith(".vdi")){
							String ext = entry.getName().endsWith(".vmx")?".vmx":entry.getName().endsWith(".vbox")?".vbox":".vdi";
							File tempFile = File.createTempFile(entry.getName(), ext);
							try (FileOutputStream fos = new FileOutputStream(tempFile)) {
								for (int n; (n = zis.read(buffer)) != -1;) {
									fos.write(buffer, 0, n);
								}
								if (entry.getName().endsWith(".vmx") || entry.getName().endsWith(".vbox")){
									newMainFile=mainFile + entry.getName();								
								}								
							}	
							System.out.println("Guarde temp "+tempFile);
							filesTemp.put(tempFile,entry.getName());
						}
						zis.closeEntry();
					}
					System.out.println("Recibi "+filesTemp.size()+", Delete old files");
					if(filesTemp.size()>0){
						
						File dir = new File(mainFile);
						System.out.println(dir);
						for(java.io.File f:dir.listFiles())if(f.isFile())f.delete();
						System.out.println("Save new files");
						for(File temp:filesTemp.descendingKeySet()){
							File newFile = new File(mainFile, filesTemp.get(temp));
							System.out.println("Guardo: "+newFile);
							try (FileInputStream streamTemp = new FileInputStream(temp);FileOutputStream ouFile = new FileOutputStream(newFile)) {
								for (int n; (n = streamTemp.read(buffer)) != -1;) {
									ouFile.write(buffer, 0, n);
								}															
							}	
							temp.delete();
						}
					}				
					
					System.out.println("Termina recepcion de imagen en ruta: "+newMainFile);
					try {
						vs.setPath(token,newMainFile);
						vs.changeImageState(vmId, VirtualMachineImageEnum.AVAILABLE);
						System.out.println("Cambio estado y cierro proceso");
					} catch (Exception e) {
						System.out.println("error escritura");
						e.printStackTrace();
					}
					
				} catch (Exception e) {		
					System.out.println("error general");
				     e.printStackTrace();
					if(path.endsWith("temp")){
						vs.deleteImage(vmId);
						new java.io.File(path).getParentFile().delete();
					}else{
						vs.setPath(token,path);
						vs.changeImageState(vmId, VirtualMachineImageEnum.AVAILABLE);
					}
					
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}