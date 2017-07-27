package uniandes.unacloud.common.net.tcp.message.exe;

import org.json.JSONObject;

import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;
import uniandes.unacloud.common.utils.Time;


/**
 * Represents message to add time to execution
 * @author Clouder
 *
 */
public class ExecutionAddTimeMessage extends ImageOperationMessage {
	
	private static final long serialVersionUID = 7525891768407688888L;
	
	public static final String TIME_EXTENSION = "time_ext";
	
	public ExecutionAddTimeMessage(String ip, int port, String host, long executionId, Time extension) {
		super(ip, port, host, ImageOperationMessage.VM_TIME, executionId);
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(TIME_EXTENSION, extension);
		this.setMessage(tempMessage);	
	}
  
	public Time getExecutionTime() {
		return (Time) this.getMessage().get(TIME_EXTENSION);
	}
}