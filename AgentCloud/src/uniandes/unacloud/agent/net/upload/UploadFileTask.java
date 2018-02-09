package uniandes.unacloud.agent.net.upload;

import java.io.File;
import java.io.PrintWriter;

import uniandes.unacloud.common.enums.FileEnum;
import uniandes.unacloud.utils.file.FileProcessor;

public class UploadFileTask extends AbsUploadFileTask {
		
	private boolean success;

	public UploadFileTask(File fileOrDirectory, FileEnum type) {
		super(fileOrDirectory, fileOrDirectory.getName(), type);
	}

	@Override
	public void beforeUpload() {
		
	}

	@Override
	public void successUpload() {
		success = true;
	}

	@Override
	public void failedUpload(Exception e) {		
		success = false;
	}

	@Override
	public void afterUpload() {
		if(success) {
			try {
				if(type == FileEnum.LOG) {
					PrintWriter writer;				
					writer = new PrintWriter(fileOrDirectory);
					writer.print("");
					writer.close();					
				}
				else if(type == FileEnum.MONITORING)
					FileProcessor.deleteFileSync(fileOrDirectory.getAbsolutePath());
				
			} catch (Exception e) {			
				e.printStackTrace();
			}	
		}		
	}

}
