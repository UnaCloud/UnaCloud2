package uniandes.unacloud.common.net.tcp.message.exe;

import org.json.JSONObject;

import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;
import uniandes.unacloud.common.utils.Time;


/**
 * Represents message to add time to execution
 * @author Clouder, CesarF
 *
 */
public class ExecutionAddTimeMessage extends ImageOperationMessage {
	
	private static final long serialVersionUID = 7525891768407688888L;
	
	private static final String TIME_EXTENSION = "time_ext";
	
	private Time extension; 
	
	public ExecutionAddTimeMessage(String ip, int port, String host, long executionId, long pmId, Time extension) {
		super(ip, port, host, ImageOperationMessage.VM_TIME, pmId, executionId);
		this.extension = extension;
	}
	
	@Override
	public void setMessageByStringJson(String format) {
		super.setMessageByStringJson(format);
		JSONObject json;
		json = new JSONObject(format);		
		this.extension = (Time) json.get(TIME_EXTENSION);
	}
	
	@Override
	protected JSONObject getJsonMessage() {
		JSONObject obj = super.getJsonMessage();
		obj.put(TIME_EXTENSION, extension);
		return obj;
	}
	
  
	public Time getExecutionTime() {
		return extension;
	}
}