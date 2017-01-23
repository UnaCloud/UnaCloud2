package uniandes.unacloud.common.com.messages.agent;

import uniandes.unacloud.common.com.messages.AgentMessage;

/**
 * Represents message to clear folder cache in agents
 * @author Clouder
 *
 */
public class ClearVMCacheMessage extends AgentMessage{

	private static final long serialVersionUID = 524061116935661249L;

	public ClearVMCacheMessage() {
		super(CLEAR_CACHE);
	}
}
