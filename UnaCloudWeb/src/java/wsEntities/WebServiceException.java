package wsEntities;

public class WebServiceException {
	String message;
	public WebServiceException(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}
