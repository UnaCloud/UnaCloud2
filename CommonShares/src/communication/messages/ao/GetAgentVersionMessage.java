package communication.messages.ao;

import communication.messages.AgentMessage;

/**
 * Represents message to return agent version in physical machine
 * @author Clouder
 *
 */
public class GetAgentVersionMessage extends AgentMessage{

	private static final long serialVersionUID = -4156099004206342347L;
	public GetAgentVersionMessage() {
		super(GET_VERSION);
	}
}
