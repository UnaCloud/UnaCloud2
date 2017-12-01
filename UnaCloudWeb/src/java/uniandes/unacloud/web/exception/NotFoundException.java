package uniandes.unacloud.web.exception;

public class NotFoundException extends HttpException {

	private static final long serialVersionUID = 4726884493036714593L;
	
	public NotFoundException(String message) {
		super(404, message);
	}
}
