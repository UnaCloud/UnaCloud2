package unacloud.share.enums;

/**
 * Enum to represent states of Physical machine
 * @author Cesar
 *
 */
public enum PhysicalMachineStateEnum {
	
	ON,OFF,DISABLED,PROCESSING;
	
	public static PhysicalMachineStateEnum getEnum(String name){
		if(ON.name().equals(name))return ON;
		if(OFF.name().equals(name))return OFF;
		if(DISABLED.name().equals(name))return DISABLED;
		if(PROCESSING.name().equals(name))return PROCESSING;
		return null;
	}
}
