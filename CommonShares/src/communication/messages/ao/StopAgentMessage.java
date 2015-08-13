package communication.messages.ao;

import communication.messages.AgentMessage;

public class StopAgentMessage extends AgentMessage{

	private static final long serialVersionUID = -7283171731214883842L;
	public StopAgentMessage() {
		super(STOP_CLIENT);
	}
}
