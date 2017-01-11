package uniandes.unacloud.common.com.messages.exeo;

import uniandes.unacloud.common.com.messages.ImageOperationMessage;


/**
 * Represents message to restart physical machine
 * @author Clouder
 *
 */
public class ExecutionRestartMessage extends ImageOperationMessage{
	private static final long serialVersionUID = 619421995819548819L;
	public ExecutionRestartMessage() {
		super(VM_RESTART);
	}
}