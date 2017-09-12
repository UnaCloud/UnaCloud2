package uniandes.unacloud.common.net.tcp.message.exe;

import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;
import uniandes.unacloud.common.utils.Time;


/**
 * Represents message to add time to execution
 * @author Clouder, CesarF
 *
 */
public class ExecutionAddTimeMessage extends ImageOperationMessage {
	
	private static final long serialVersionUID = 7525891768407688888L;
	
	private Time extension; 
	
	public ExecutionAddTimeMessage(String ip, int port, String host, long executionId, long pmId, Time extension) {
		super(ip, port, host, ImageOperationMessage.VM_TIME, pmId, executionId);
		this.extension = extension;
	}
	
  
	public Time getExecutionTime() {
		return extension;
	}
}