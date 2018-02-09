package uniandes.unacloud.common.net.tcp.message.agent;

import uniandes.unacloud.common.enums.FileEnum;
import uniandes.unacloud.common.net.tcp.message.AgentMessage;

public class GetFilesMessage extends AgentMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7115365883481935109L;
	
	
	private FileEnum fileType;
	
	private String fileName;

	public GetFilesMessage(String ip, int port, String host, long pmId, FileEnum fileType, String fileName) {
		super(ip, port, host, AgentMessage.GET_FILE, pmId);
		this.fileType = fileType;
		this.fileName = fileName;
	}
	
	public FileEnum getFileType() {
		return fileType;
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
