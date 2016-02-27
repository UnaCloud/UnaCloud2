package communication.messages.ao;

import communication.UnaCloudAbstractResponse;

/**
 * Class used to respond with information to server after one operation
 * @author Cesar
 *
 */
public class InformationResponse extends UnaCloudAbstractResponse{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 923545605946862973L;
	private String message;
	
	public InformationResponse(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}
