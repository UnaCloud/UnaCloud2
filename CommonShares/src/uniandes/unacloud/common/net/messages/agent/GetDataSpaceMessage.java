package uniandes.unacloud.common.net.messages.agent;


/**
 * Represents message to request free space in data path
 * @author CesarF
 *
 */
public class GetDataSpaceMessage extends AgentMessage {
	
	private static final long serialVersionUID = 4304479802712467440L;

	public GetDataSpaceMessage() {
		super(GET_DATA_SPACE);
	}

}
