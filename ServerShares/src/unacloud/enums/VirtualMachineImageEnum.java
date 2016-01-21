package unacloud.enums;

/**
 * 
 * @author Cesar
 * 
 * Class to represent the states of a VirtualMachineImage
 *
 */

public enum VirtualMachineImageEnum {

	UNAVAILABLE("UNAVAILABLE"),DISABLE("DISABLE"),AVAILABLE("AVAILABLE"),REMOVING_CACHE("REMOVING FROM CACHE"),COPYING("COPYING TO SERVER"),IN_QUEUE("QUEUED");
	
	String name;
	
	private VirtualMachineImageEnum(String name){
		this.name = name;
	}	
	
	public static VirtualMachineImageEnum getEnum(String name){
		if(UNAVAILABLE.name().equals(name))return UNAVAILABLE;
		if(DISABLE.name().equals(name))return DISABLE;
		if(AVAILABLE.name().equals(name))return AVAILABLE;
		if(REMOVING_CACHE.name().equals(name))return REMOVING_CACHE;
		if(COPYING.name().equals(name))return COPYING;
		if(IN_QUEUE.name().equals(name))return IN_QUEUE;
		return null;
	}
}
