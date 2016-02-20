package uniandes.communication.processor;

import communication.UnaCloudAbstractResponse;

/**
 * Abstract class to be used that process responses from agents
 * @author Cesar
 *
 */
public abstract class AbstractResponseProcessor {
	
	/**
	 * Attends response
	 * @param response
	 */
	public abstract void attendResponse(UnaCloudAbstractResponse response, Long id);
	
	/**
	 * Manage error in process
	 * @param message
	 */
	public abstract void attendError(String message, Long id);

}
