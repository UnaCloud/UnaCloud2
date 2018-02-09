package uniandes.unacloud.agent.host.resources;

import java.io.File;

import uniandes.unacloud.agent.execution.ExecutorService;
import uniandes.unacloud.agent.net.upload.UploadFileTask;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.enums.FileEnum;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;

public class FileManager {	
	
	public static UnaCloudResponse copyFile(FileEnum type, String fileName) {
		ExecutorService.executeBackgroundTask(new UploadFileTask(new File(fileName), type));
		return new UnaCloudResponse("Starts copy file process", ExecutionProcessEnum.SUCCESS);
	}

}
