package uniandes.communication;

import communication.UnaCloudAbstractResponse;

/**
 * Interface to be implemented by classes that process responses from agents
 * @author Cesar
 *
 */
public interface ResponseProcessor {
	
	/**
	 * Attends response
	 * @param response
	 */
	public void attendResponse(UnaCloudAbstractResponse response);
	
	/**
	 * Manage error in process
	 * @param message
	 */
	public void attendError(String message);

}
