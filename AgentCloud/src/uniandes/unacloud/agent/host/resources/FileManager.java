package uniandes.unacloud.agent.host.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.agent.execution.ExecutorService;
import uniandes.unacloud.agent.host.system.OSFactory;
import uniandes.unacloud.agent.net.upload.UploadZipTask;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Class for managing file exchange in UnaCloud
 * @author CesarF
 */
public class FileManager {	
	
	public static UnaCloudResponse copyLogs() {
		try {
			List<File> files = new ArrayList<File>();
			files.add(new File(VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH) + UnaCloudConstants.AGENT_OUT_LOG));
			files.add(new File(VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH) + UnaCloudConstants.AGENT_ERROR_LOG));
			ExecutorService.executeRequestTask(new UploadZipTask(files, OSFactory.getOS().getHostname()));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return new UnaCloudResponse("Starts copy file process", ExecutionProcessEnum.SUCCESS);
	}

	public static UnaCloudResponse copyMonitoringFiles() {
		try {
			List<File> files = new ArrayList<File>();
			//Know where are the monitoring files to zip it
			files.add(new File(VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH) + UnaCloudConstants.AGENT_OUT_LOG));
			files.add(new File(VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH) + UnaCloudConstants.AGENT_ERROR_LOG));
			ExecutorService.executeRequestTask(new UploadZipTask(files, OSFactory.getOS().getHostname()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new UnaCloudResponse("Starts copy file process", ExecutionProcessEnum.SUCCESS);
	}


}
