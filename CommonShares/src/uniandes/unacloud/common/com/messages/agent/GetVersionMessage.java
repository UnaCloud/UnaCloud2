package uniandes.unacloud.common.com.messages.agent;

import uniandes.unacloud.common.com.messages.AgentMessage;

/**
 *  Represents message to request agent version
 * @author CesarF
 *
 */
public class GetVersionMessage extends AgentMessage{

	private static final long serialVersionUID = -8917249505055572647L;

	public GetVersionMessage() {
		super(GET_VERSION);
	}

}
