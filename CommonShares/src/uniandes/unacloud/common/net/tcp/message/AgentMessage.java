package uniandes.unacloud.common.net.tcp.message;


/**
 * Represents type of tasks for agent
 * @author CesarF
 *
 */
public class AgentMessage extends ClientMessage {
	
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
	 * Request free space in data path
	 */
	public static final int GET_FILE = 12;
		
	/**
	 * Creates a new Agent message
	 * @param subOperation
	 */
	public AgentMessage(String ip, int port, String host, int task, long pmId) {
		super(ip, port, host, TCPMessageEnum.AGENT_OPERATION.name(), task, pmId);			
	}	
	
}
