package uniandes.unacloud.common.com.messages;

import uniandes.unacloud.common.com.UnaCloudAbstractMessage;

/**
 * Represents task message for agent
 * @author Clouder
 *
 */
public abstract class AgentMessage extends UnaCloudAbstractMessage{
	private static final long serialVersionUID = 2295996510707565731L;
	public static final int UPDATE_OPERATION = 4;
	public static final int STOP_CLIENT=6;
	public static final int GET_VERSION=7;
	public static final int CLEAR_CACHE=8;
	public static final int CLEAR_IMAGE_FROM_CACHE=9;
    public AgentMessage(int subOperation){
		super(AGENT_OPERATION, subOperation);
	}
}
