package uniandes.unacloud.common.com.messages.agent;

import uniandes.unacloud.common.com.messages.AgentMessage;

/**
 * Represents message to stop agents in physical machine
 * @author Clouder
 *
 */
public class StopAgentMessage extends AgentMessage{

	private static final long serialVersionUID = -7283171731214883842L;
	public StopAgentMessage() {
		super(STOP_CLIENT);
	}
}
