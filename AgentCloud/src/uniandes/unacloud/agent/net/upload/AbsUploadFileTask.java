package uniandes.unacloud.agent.net.upload;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uniandes.unacloud.agent.utils.SystemUtils;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.FileEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.utils.file.FileProcessor;

public abstract class AbsUploadFileTask implements Runnable {
	
	protected List<File> files;
	
	protected String tokenUploadCom;
	
	protected FileEnum type;
	
	public AbsUploadFileTask(File fileOrDirectory, String token, FileEnum fileType) {
		this.files.add(fileOrDirectory);
		this.tokenUploadCom = token;
		type = fileType;
	}
	
	public AbsUploadFileTask(List<File> files, String token, FileEnum fileType) {
		this.files = files;
		this.tokenUploadCom = token;
		type = fileType;
	}
	
	public double getSize() {
		double size = 0;
		for (File file: files) 
			size += file.length();
		return size;
	}
	
	
	@Override
	public void run() {
		try {						
			beforeUpload();
			
			final int puerto = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.FILE_SERVER_PORT);
			final String ip = VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.FILE_SERVER_IP);
			
			//Preparing files
			System.out.println("Preparing file");
			File zip = null;
			long fileSize = 0;
			try {	
				String name = null;
				if (files.size() > 1) {
					name = files.get(0).getParentFile().getAbsolutePath() + files.get(0).getName() + "_" + SystemUtils.getStringDate();
					System.out.println("\tMultiple Files " + name);
					zip = FileProcessor.zipFilesSync(name, files);					
				}
				//If size is one
				else {
					name = files.get(0).getParentFile().getAbsolutePath();
					System.out.println("\tOne file or folder " + name);
					zip = FileProcessor.zipFileSync(name);
				}
				System.out.println("\t\t" + zip);
				fileSize += getSize();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Send files
			System.out.println("Connecting to " + ip + ":" + puerto);
			try (Socket s = new Socket(ip, puerto); OutputStream os = s.getOutputStream()) {
				
				DataOutputStream ds = new DataOutputStream(os);
				System.out.println("Connection successful");
				ds.writeInt(UnaCloudConstants.SEND_IMAGE);
				ds.flush();
				
				ds.writeUTF(type.name());
				ds.flush();
				
				System.out.println("Token " + tokenUploadCom);
				ds.writeUTF(tokenUploadCom);
				ds.flush();
								
				System.out.println("Filesize " + fileSize);
				ds.writeLong(fileSize);
				ds.flush();
				
				ZipOutputStream zos = new ZipOutputStream(ds);
				
				System.out.println("\tSending " + tokenUploadCom);
				final byte[] buffer = new byte[1024 * 100];
				System.out.println("\tSending files" + zip.getAbsolutePath());
				
											
				System.out.println("\tSending: " + zip.getName());
				zos.putNextEntry(new ZipEntry(zip.getName()));
					
				try (FileInputStream fis = new FileInputStream(zip)) {
					for (int n; (n = fis.read(buffer)) != -1;)
						zos.write(buffer,0,n);
				}
				zos.closeEntry();
				System.out.println("Zip sent");					
				zos.flush();
				
				successUpload();				
				
			} catch (Exception e) {	
				e.printStackTrace();
				failedUpload(e);
			}
			
			afterUpload(zip);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public abstract void beforeUpload();
	
	public abstract void successUpload();
	
	public abstract void failedUpload(Exception e);
	
	public abstract void afterUpload(File zip);

}
