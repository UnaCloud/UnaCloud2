package uniandes.unacloud.share.enums;

/**
 * Represents states of a VirtualMachineImage
 * @author CesarF
 *
 */

public enum VirtualMachineImageEnum {

	/**
	 * Image is no unavailable because doesn't have files
	 */
	UNAVAILABLE("UNAVAILABLE"),
	/**
	 * Image was disable by admin
	 */
	DISABLE("DISABLE"),
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
	
	String name;
	
	private VirtualMachineImageEnum(String name){
		this.name = name;
	}	
	
	/**
	 * Returns a Virtual Machine Image state searched by name
	 * @param name of state
	 * @return Virtual Machine image state
	 */
	public static VirtualMachineImageEnum getEnum(String name){
		if(UNAVAILABLE.name().equals(name))return UNAVAILABLE;
		if(DISABLE.name().equals(name))return DISABLE;
		if(AVAILABLE.name().equals(name))return AVAILABLE;
		if(REMOVING_CACHE.name().equals(name))return REMOVING_CACHE;
		if(COPYING.name().equals(name))return COPYING;
		if(IN_QUEUE.name().equals(name))return IN_QUEUE;
		return null;
	}
	
	public String getName(){
		return this.name;
	}
}
