package uniandes.unacloud.agent.exceptions;

/**
 * Exception class to manage unsupported commands in operating system
 * @author CesarF
 *
 */
public class UnsupportedCommandException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8924386847427153073L;
	
	public UnsupportedCommandException(String command, String osVersion){
		super("Command "+command+" is not supported by agent in os "+osVersion);
	}

}
