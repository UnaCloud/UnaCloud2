package uniandes.unacloud.common.net.messages.agent;


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
