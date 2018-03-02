package uniandes.unacloud.agent.net.upload;

import java.io.File;

import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.net.torrent.TorrentClient;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.enums.FileEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.utils.file.FileProcessor;

public class UploadImageTask extends AbsUploadFileTask {
	
	private Execution machineExecution;

	public UploadImageTask(String fileId, Execution exe) {
		super(exe.getImage().getMainFile().getExecutableFile(), exe.getId() + "_" + fileId, FileEnum.IMAGE);		
		this.machineExecution = exe;
	}

	@Override
	public void beforeUpload() {
		
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
		
		try {				
			//TODO: If virtual machine requires some external folder it will be deleted. Take care when a new platform will be added
			for (File f: machineExecution.getImage().getMainFile().getExecutableFile().getParentFile().listFiles())
				if (f.isDirectory() || f.getName().equals(machineExecution.getImage().getMainFile().getZipFile().getName()))
					FileProcessor.deleteFileSync(f.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void successUpload() {
		try {
			ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.SUCCESS, "Image has been copied to server");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failedUpload(Exception e) {		
		try {
			PersistentExecutionManager.removeExecution(machineExecution.getId(), false);
			ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.FAIL, UnaCloudConstants.ERROR_MESSAGE + " copying images to server" + e.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
		}		
	}

	@Override
	public void afterUpload(File zip) {
		try {
			System.out.println("Delete Image " + machineExecution.getImage().getMainFile().getExecutableFile().getParentFile().getAbsolutePath());
			PersistentExecutionManager.removeExecution(machineExecution.getId(), false);
			//Change base
			if (machineExecution.getImage().getMainFile().getExecutableFile().getParentFile().getName().equals("base")) {
				ImageCacheManager.deleteImage(machineExecution.getImageId());
				TorrentClient.getInstance().removeTorrent(machineExecution.getImage().getMainFile().getTorrentFile());				
			}
			FileProcessor.deleteFileSync(machineExecution.getImage().getMainFile().getExecutableFile().getParentFile().getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
