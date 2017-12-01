package uniandes.unacloud.web.exception;

public abstract class HttpException extends Exception{
	
	private static final long serialVersionUID = -8257598661292832146L;
	
	private int code;
	
	public HttpException(int code, String message) {
		
		super(message);
		this.code = code;
		
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}

}
