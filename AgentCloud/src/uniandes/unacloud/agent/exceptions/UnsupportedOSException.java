package uniandes.unacloud.agent.exceptions;

/**
 * Exception class to manage unsupported operating system 
 * @author CesarF
 *
 */
public class UnsupportedOSException extends Exception {

	/**
	 * Serial version for this class
	 */
	private static final long serialVersionUID = 8681152293025474957L;
	
	/**
	 * Constructor method, create a new exception with a custom message
	 * @param version
	 */
	public UnsupportedOSException(String version){
		super("UnaCloud doesn't support " + version + " in this moment");
	}

}
