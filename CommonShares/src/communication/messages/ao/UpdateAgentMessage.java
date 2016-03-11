package communication.messages.ao;

import communication.messages.AgentMessage;

/**
 * Represents message to update agents in physical machine
 * @author Clouder
 *
 */
public class UpdateAgentMessage extends AgentMessage{

	private static final long serialVersionUID = -3358107648144467395L;
	public UpdateAgentMessage() {
		super(UPDATE_OPERATION);
	}
}
