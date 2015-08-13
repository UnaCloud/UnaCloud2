package communication.security.utils;

/**
 * Exception used when there is a connection error with UnaCloud server
 * @author Clouder
 */
public class ConnectionException extends Exception{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4029595910497232247L;

	public ConnectionException(String msg) {
        super(msg);
    }



}
