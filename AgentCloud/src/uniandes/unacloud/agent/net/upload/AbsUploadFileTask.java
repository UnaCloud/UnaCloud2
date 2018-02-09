package uniandes.unacloud.agent.net.upload;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.FileEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.utils.file.FileProcessor;

public abstract class AbsUploadFileTask implements Runnable {
	
	protected File fileOrDirectory;
	
	protected String fileId;
	
	protected FileEnum type;
	
	public AbsUploadFileTask(File fileOrDirectory, String fileId, FileEnum fileType) {
		this.fileOrDirectory = fileOrDirectory;
		this.fileId = fileId;
		type = fileType;
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
				zip = FileProcessor.zipFileSync(fileOrDirectory.getAbsolutePath());
				fileSize += fileOrDirectory.length();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Send files
			System.out.println("Connecting to " + ip + ":" + puerto);
			try (Socket s = new Socket(ip, puerto); OutputStream os = s.getOutputStream()) {
				
				DataOutputStream ds = new DataOutputStream(os);
				System.out.println("Connection succesful");
				ds.writeInt(UnaCloudConstants.SEND_IMAGE);
				ds.flush();
				
				ds.writeUTF(type.name());
				ds.flush();
				
				System.out.println("Token " + fileId);
				ds.writeUTF(fileId);
				ds.flush();
								
				System.out.println("Filesize " + fileSize);
				ds.writeLong(fileSize);
				ds.flush();
				
				ZipOutputStream zos = new ZipOutputStream(ds);
				
				System.out.println("\tSending " + fileId);
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
			
			afterUpload();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public abstract void beforeUpload();
	
	public abstract void successUpload();
	
	public abstract void failedUpload(Exception e);
	
	public abstract void afterUpload();

}
