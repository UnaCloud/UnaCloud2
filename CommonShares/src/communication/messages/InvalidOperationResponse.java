package communication.messages;

import communication.UnaCloudAbstractResponse;

public class InvalidOperationResponse extends UnaCloudAbstractResponse{
	private static final long serialVersionUID = -4408716514292114861L;
	String message;
	public InvalidOperationResponse() {
	}
	public InvalidOperationResponse(String message) {
		super();
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	@Override
	public String toString() {
		return "InvalidOperationResponse [message=" + message + "]";
	}
}
