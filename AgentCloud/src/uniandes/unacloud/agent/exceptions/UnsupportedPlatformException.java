package uniandes.unacloud.agent.exceptions;

/**
 * Exception class to manage unsupported platforms
 * @author CesarF
 *
 */
public class UnsupportedPlatformException extends Exception {

	/**
	 * Serial version for this class
	 */
	private static final long serialVersionUID = -2172011906698962586L;
	
	/**
	 * Constructor method, create a new exception with a custom message
	 * @param platform
	 */
	public UnsupportedPlatformException(String platform) {
		super("UnaCloud doesn't support " + platform + " in this moment");
	}

}
