package uniandes.unacloud.common.net.messages.exeo;

import uniandes.unacloud.common.net.messages.ImageOperationMessage;

/**
 * Represents message to stop an execution
 * @author CesarF
 *
 */
public class ExecutionStopMessage extends ImageOperationMessage{
	
	private static final long serialVersionUID = -8728929759121643688L;
	
	public ExecutionStopMessage() {
		super(VM_STOP);
	}
}
