package uniandes.unacloud.web.exception;

public class UnathorizedException extends HttpException{
	
	private static final long serialVersionUID = 2059405801984123040L;

	public UnathorizedException(String message) {
		super(401, message);
	}

}
