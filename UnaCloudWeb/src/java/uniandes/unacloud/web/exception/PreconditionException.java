package uniandes.unacloud.web.exception;

public class PreconditionException extends HttpException {

	private static final long serialVersionUID = 4726884493036714593L;
	
	public PreconditionException(String message) {
		super(412, message);
	}
}
