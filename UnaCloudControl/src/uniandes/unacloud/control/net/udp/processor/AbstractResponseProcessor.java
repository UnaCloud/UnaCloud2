package uniandes.unacloud.control.net.udp.processor;

import uniandes.unacloud.common.net.messages.UnaCloudAbstractResponse;

/**
 * Abstract class that process responses from agents
 * @author CesarF
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
