package uniandes.unacloud.agent.net.upload;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import uniandes.unacloud.common.enums.FileEnum;

/**
 * Class for uploading a zip with the given file list
 */
public class UploadZipTask extends AbsUploadFileTask {
		
	private boolean success;

	public UploadZipTask(List<File> files, String hostname) {
		super(files, hostname, FileEnum.LOG);
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
	public void afterUpload(File zip) {
		if(zip != null && zip.exists())
			zip.delete();
		if(success) {
			try {
				if(type == FileEnum.LOG) {
					for(File file: files) {
						PrintWriter writer;				
						writer = new PrintWriter(file);
						writer.print("");
						writer.close();	
						System.out.println("Logs were uploaded to server");
					}		
				}				
			} catch (Exception e) {			
				e.printStackTrace();
			}	
		}		
	}

}
