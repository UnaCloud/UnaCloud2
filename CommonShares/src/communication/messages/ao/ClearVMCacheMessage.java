package communication.messages.ao;

import communication.messages.AgentMessage;

public class ClearVMCacheMessage extends AgentMessage{

	private static final long serialVersionUID = 524061116935661249L;

	public ClearVMCacheMessage() {
		super(CLEAR_CACHE);
	}
}
