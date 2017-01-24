package uniandes.unacloud.common.com.messages.exeo;

import uniandes.unacloud.common.com.messages.ImageOperationMessage;
import uniandes.unacloud.common.utils.Time;


/**
 * Represents message to add time to execution
 * @author Clouder
 *
 */
public class ExecutionAddTimeMessage extends ImageOperationMessage{
	private static final long serialVersionUID = 7525891768407688888L;
	Time executionTime;
    String id;
    public ExecutionAddTimeMessage() {
		super(VM_TIME);
	}
	public Time getExecutionTime() {
		return executionTime;
	}
	public String getId() {
		return id;
	}
}