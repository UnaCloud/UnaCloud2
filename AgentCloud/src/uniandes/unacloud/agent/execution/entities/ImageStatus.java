package uniandes.unacloud.agent.execution.entities;

/**
 * Status of image
 * @author clouder
 *
 */
public enum ImageStatus {
	/**
	 * represents an image used by one platform
	 */
	LOCK,
	/**
	 * Represents an image free to be deployed
	 */
	FREE,
	/**
	 * Represents an image when in being configuring and testing connection
	 */
	STARTING
}
