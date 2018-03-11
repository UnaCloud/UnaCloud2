package uniandes.unacloud.file.net.task;

import java.io.File;
import java.net.Socket;
import java.sql.Connection;

import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.share.db.PhysicalMachineManager;
import uniandes.unacloud.share.db.StorageManager;
import uniandes.unacloud.share.db.entities.PhysicalMachineEntity;
import uniandes.unacloud.share.db.entities.RepositoryEntity;

public class FileLogReceiverTask extends AbsFileReceiverTask {

	private PhysicalMachineEntity machine;
	
	public FileLogReceiverTask(Socket s) {
		super(s);
	}

	@Override
	public boolean validateToken(String token) {
		if(token != null && !token.isEmpty()) {
			token = token.trim().toUpperCase();
			System.out.println(token);
			try (Connection con = FileManager.getInstance().getDBConnection();) {
				machine = PhysicalMachineManager.getPhysicalMachineByHostName(token, con);
				System.out.println("\tLogs requested " + machine);	
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void successReceive(File file, long originalSize) {
		try (Connection con = FileManager.getInstance().getDBConnection();) {
			PhysicalMachineEntity pm = new PhysicalMachineEntity(machine.getId(), machine.getIp(), null, null, null, null, null, file.getName());
			PhysicalMachineManager.setPhysicalMachine(pm, con);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failedReceive() {
		
	}

	@Override
	public String getRepoPath() {
		try (Connection con = FileManager.getInstance().getDBConnection();) {	
			RepositoryEntity entity = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con);
			return entity.getRoot() + File.separator + UnaCloudConstants.LOGS_PATH + File.separator + machine.getHost() + File.separator;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
