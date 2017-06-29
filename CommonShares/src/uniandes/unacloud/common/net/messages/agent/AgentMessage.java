package uniandes.unacloud.common.net.messages.agent;

import uniandes.unacloud.common.net.messages.UnaCloudAbstractMessage;


/**
 * Represents type of tasks for agent
 * @author Clouder
 *
 */
public abstract class AgentMessage extends UnaCloudAbstractMessage {
	
	private static final long serialVersionUID = 2295996510707565731L;
	
	/**
	 * Request to update agent files in physical machine
	 */
	public static final int UPDATE_OPERATION = 4;
	/**
	 * Request to stop agent 
	 */
	public static final int STOP_CLIENT = 6;
	/**
	 * Request to return version of agent
	 */
	public static final int GET_VERSION = 7;
	/**
	 * Request to clear data path 
	 */
	public static final int CLEAR_CACHE = 8;
	/**
	 * Request to delete one image form data path
	 */
	public static final int CLEAR_IMAGE_FROM_CACHE = 9;
	/**
	 * Request free space in data path
	 */
	public static final int GET_DATA_SPACE = 11;
	
	/**
	 * Creates a new Agent message
	 * @param subOperation
	 */
    public AgentMessage(int subOperation) {
		super(AGENT_OPERATION, subOperation);
	}
}
