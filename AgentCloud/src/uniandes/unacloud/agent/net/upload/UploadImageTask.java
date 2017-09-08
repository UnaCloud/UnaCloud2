package uniandes.unacloud.agent.net.upload;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.net.torrent.TorrentClient;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.utils.file.FileProcessor;


/**
 * Task to send execution files to server
 * @author CesarF
 *
 */
public class UploadImageTask implements Runnable {

	/**
	 * Execution entity to be uploaded to server
	 */
	private Execution machineExecution;
	
	/**
	 * Secure token for server
	 */
	private String secureToken;
	
	/**
	 * Constructs a new upload task
	 * @param machineExecution
	 * @param secureToken
	 */
	public UploadImageTask(Execution machineExecution, String secureToken) {
		this.machineExecution = machineExecution;
		this.secureToken = secureToken;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Stop execution: " + machineExecution.getId() + ", of Image: " + machineExecution.getImageId() );
			PersistentExecutionManager.stopExecution(machineExecution.getId());
			
			System.out.println("Delete snapshot: " + machineExecution.getId());
			try {
				machineExecution.getImage().deleteSnapshot();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("Unregister execution: " + machineExecution.getId());
			PersistentExecutionManager.unregisterExecution(machineExecution.getId());
						
			final int puerto = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.FILE_SERVER_PORT);
			final String ip = VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.FILE_SERVER_IP);
			
			//Preparing files
			System.out.println("Preparing file");
			File zip = null;
			long fileSize = 0;
			try {				
				//TODO: If virtual machine requires some external folder it will be deleted. Take care when a new platform will be added
				for (File f: machineExecution.getImage().getMainFile().getExecutableFile().getParentFile().listFiles())
					if (f.isDirectory() || f.getName().equals(machineExecution.getImage().getMainFile().getZipFile().getName()))
						FileProcessor.deleteFileSync(f.getAbsolutePath());
					else
						fileSize += f.length();
				zip = FileProcessor.zipFileSync(machineExecution.getImage().getMainFile().getExecutableFile().getAbsolutePath());
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
				
				System.out.println("Execution " + machineExecution.getId());
				ds.writeLong(machineExecution.getId());
				ds.flush();
				
				System.out.println("Token " + secureToken);
				ds.writeUTF(secureToken);
				ds.flush();
								
				System.out.println("Filesize " + fileSize);
				ds.writeLong(fileSize);
				ds.flush();
				
				ZipOutputStream zos = new ZipOutputStream(ds);
				
				System.out.println("\tSending " + machineExecution.getId());
				final byte[] buffer = new byte[1024 * 100];
				System.out.println("\tSending files" + machineExecution.getImage().getMainFile().getExecutableFile().getAbsolutePath());
				
				try {										
					System.out.println("\tSending: " + zip.getName());
					zos.putNextEntry(new ZipEntry(zip.getName()));
						
					try (FileInputStream fis = new FileInputStream(zip)) {
						for (int n; (n = fis.read(buffer)) != -1;)
							zos.write(buffer,0,n);
					}
					zos.closeEntry();
					System.out.println("Zip sent");					
					zos.flush();
					ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.SUCCESS, "Image has been copied to server");
					
				} catch (Exception e) {
					e.printStackTrace();
					PersistentExecutionManager.removeExecution(machineExecution.getId(), false);
					ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.FAIL, UnaCloudConstants.ERROR_MESSAGE + " copying images to server" + e.getMessage());
				}					
				
			} catch (Exception e) {	
				e.printStackTrace();
				ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.FAIL, UnaCloudConstants.ERROR_MESSAGE + " copying images to server " + e.getMessage());
			}
			
			System.out.println("Delete Image " + machineExecution.getImage().getMainFile().getExecutableFile().getParentFile().getAbsolutePath());
			PersistentExecutionManager.removeExecution(machineExecution.getId(), false);
			//Change base
			if (machineExecution.getImage().getMainFile().getExecutableFile().getParentFile().getName().equals("base")) {
				ImageCacheManager.deleteImage(machineExecution.getImageId());
				TorrentClient.getInstance().removeTorrent(machineExecution.getImage().getMainFile().getTorrentFile());
			}
			PersistentExecutionManager.cleanDir(machineExecution.getImage().getMainFile().getExecutableFile().getParentFile());
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
