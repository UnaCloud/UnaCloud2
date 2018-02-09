package uniandes.unacloud.file.net.task;

import java.io.File;
import java.net.Socket;
import java.sql.Connection;

import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.utils.FileConverter;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.db.ImageFileManager;
import uniandes.unacloud.file.db.UserManager;
import uniandes.unacloud.file.db.entities.ImageFileEntity;
import uniandes.unacloud.file.db.entities.UserEntity;
import uniandes.unacloud.file.net.torrent.TorrentTracker;
import uniandes.unacloud.share.db.ExecutionManager;
import uniandes.unacloud.share.db.entities.ExecutionEntity;
import uniandes.unacloud.share.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.utils.file.FileProcessor;

public class FileImageReceiverTask extends AbsFileReceiverTask {
	
	private ImageFileEntity image;
	
	private UserEntity user;
	
	private Long execution;
	
	/**
	 * Creates a new file receiver task
	 * @param s socket to process task
	 */
	public FileImageReceiverTask(Socket s) {
		super(s);
		System.out.println("Attending " + s.getRemoteSocketAddress());
	}	

	@Override
	public boolean validateToken(String token) {
		String[] data = token.split("_"); 
		execution = Long.parseLong(data[0]);
		token = data[1];
		try (Connection con = FileManager.getInstance().getDBConnection();) {
			image = ImageFileManager.getImageWithFile(token, con);
			System.out.println("\tImage requested " + image);	
			if (image != null) {
				user = UserManager.getUser(image.getOwner().getId(), con);
				System.out.println("\tRequest " + execution + " - " + token);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	@Override
	public void successReceive(File file, long originalSize) {
		FileConverter zip = new FileConverter(file);
		String message;
		try {					
			//Announce in torrent
			TorrentTracker.getInstance().publishFile(zip);
			message = "Copying process has been successful";
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}
		try (Connection con = FileManager.getInstance().getDBConnection();) {
			
			ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null, null, null, null, originalSize, zip.getExecutableFile().getAbsolutePath(), null, null), false, con, true);
			System.out.println("Status changed, process closed");
			ExecutionEntity exe = new ExecutionEntity(execution, 0, 0, null, null, ExecutionProcessEnum.SUCCESS, null, message);
			ExecutionManager.updateExecution(exe, ExecutionStateEnum.COPYING, con);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void failedReceive() {
		try {
			FileProcessor.deleteFileSync(getRepoPath());
		} catch (Exception e1) {
			e1.printStackTrace();
		}	
		String message = "Error receiving file from agent. Check logs";	
		try (Connection con = FileManager.getInstance().getDBConnection();) {							
			System.out.println("Error in process, all files must be deleted");
			ExecutionEntity exe = new ExecutionEntity(execution, 0, 0, null, null, ExecutionProcessEnum.FAIL, null, message);
			ExecutionManager.updateExecution(exe, ExecutionStateEnum.COPYING, con);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public String getRepoPath() {
		return image.getRepository().getRoot() + image.getName() + "_" + user.getUsername() + File.separator;
	}
}
