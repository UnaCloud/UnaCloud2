package uniandes.unacloud.agent.net.upload;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;


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
			
			//Send files
			final int puerto = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.FILE_SERVER_PORT);
			final String ip = VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.FILE_SERVER_IP);
			System.out.println("Connecting to " + ip + ":" + puerto);
			try (Socket s = new Socket(ip,puerto); OutputStream os = s.getOutputStream()) {
				
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
				
				ZipOutputStream zos = new ZipOutputStream(ds);
				
				System.out.println("\tSending " + machineExecution.getId());
				final byte[] buffer = new byte[1024*100];
				System.out.println("\tSending files" + machineExecution.getImage().getMainFile());
				
				try {
					for (java.io.File f : machineExecution.getImage().getMainFile().getParentFile().listFiles())
						if (f.isFile()) {
							System.out.println("\tSending: " + f.getName());
							zos.putNextEntry(new ZipEntry(f.getName()));
								
							try (FileInputStream fis = new FileInputStream(f)) {
								for (int n; (n = fis.read(buffer)) != -1;)
									zos.write(buffer,0,n);
							}
							zos.closeEntry();
						}
					System.out.println("Files sent");					
					zos.flush();
					ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.SUCCESS, "Image has been copied to server");
					
				} catch (Exception e) {
					PersistentExecutionManager.removeExecution(machineExecution.getId(), false);
					ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.FAIL, UnaCloudConstants.ERROR_MESSAGE + " copying images to server");
					throw new ExecutionException(UnaCloudConstants.ERROR_MESSAGE + " deleting images", e);
				}					
				
			} catch (Exception e) {	
				ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.FAIL, UnaCloudConstants.ERROR_MESSAGE + " copying images to server");
				throw new ExecutionException(UnaCloudConstants.ERROR_MESSAGE + " opening connection", e);
			}
			
			System.out.println("Delete Image " + machineExecution.getImage().getMainFile().getParentFile().getAbsolutePath());
			PersistentExecutionManager.removeExecution(machineExecution.getId(), false);
			if (machineExecution.getImage().getMainFile().getParentFile().getName().equals("base"))
				ImageCacheManager.deleteImage(machineExecution.getImageId());
			PersistentExecutionManager.cleanDir(machineExecution.getImage().getMainFile().getParentFile());
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
