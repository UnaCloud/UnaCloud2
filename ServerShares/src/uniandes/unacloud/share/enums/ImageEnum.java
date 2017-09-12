package uniandes.unacloud.share.enums;

/**
 * Represents states of Image
 * @author CesarF
 *
 */

public enum ImageEnum {

	/**
	 * Image is no unavailable because doesn't have files
	 */
	UNAVAILABLE("UNAVAILABLE"),
	
	/**
	 * Image was disable by admin
	 */
	DISABLE("DISABLE"),
	
	/**
	 * Image is been processing 
	 */
	PROCESSING("PROCESSING"),
	
	/**
	 * Image is ready to be deployed
	 */
	AVAILABLE("AVAILABLE"),
	
	/**
	 * Image is being removing from machines cache folder
	 */
	REMOVING_CACHE("REMOVING FROM CACHE"),
	
	/**
	 * Image is being copying from some physical machine
	 */
	COPYING("COPYING TO SERVER"),
	
	/**
	 * Image  has a task in queue
	 */
	IN_QUEUE("QUEUED");
	
	/**
	 * Name of enum
	 */
	private String name;
	
	/**
	 * Construct a new Enum
	 * @param name
	 */
	private ImageEnum(String name) {
		this.name = name;
	}	
	
	/**
	 * Returns an Image state searched by name
	 * @param name of state
	 * @return Image state
	 */
	public static ImageEnum getEnum(String name) {
		if (UNAVAILABLE.name().equals(name)) return UNAVAILABLE;
		if (PROCESSING.name().equals(name)) return PROCESSING;
		if (DISABLE.name().equals(name)) return DISABLE;
		if (AVAILABLE.name().equals(name)) return AVAILABLE;
		if (REMOVING_CACHE.name().equals(name)) return REMOVING_CACHE;
		if (COPYING.name().equals(name)) return COPYING;
		if (IN_QUEUE.name().equals(name)) return IN_QUEUE;
		return null;
	}
	
	/**
	 * Returns enum value name
	 * @return enum name
	 */
	public String getName() {
		return this.name;
	}
}
