package unacloud.enums;

/**
 * 
 * @author Cesar
 * 
 * Class to represent the states of a VirtualMachineImage
 *
 */

public enum VirtualMachineImageEnum {

	UNAVAILABLE("UNAVAILABLE"),DISABLE("DISABLE"),AVAILABLE("AVAILABLE"),REMOVING_CACHE("REMOVING FROM CACHE"),COPYING("COPYING TO SERVER"),IN_QUEUE("IN QUEUE");
	
	String name;
	
	private VirtualMachineImageEnum(String name){
		this.name = name;
	}
}
