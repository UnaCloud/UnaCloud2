package uniandes.unacloud.common.net.tcp;

/**
 * Abstract class to be implements by response processor
 * @author CesarF
 */
public abstract class TCPResponseProcessor {
	
	/**
	 * Attends response
	 * @param response
	 */
	public abstract void attendResponse(Object response, Object message);
	
	/**
	 * Manage error in process
	 * @param message
	 */
	public abstract void attendError(Object error, String message);

}
