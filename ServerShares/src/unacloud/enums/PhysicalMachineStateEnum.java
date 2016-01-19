package unacloud.enums;

public enum PhysicalMachineStateEnum {
	/**
	 * PROCESSING: 
	**/
	ON,OFF,DISABLED,PROCESSING;
	
	public static PhysicalMachineStateEnum getEnum(String name){
		if(ON.name().equals(name))return ON;
		if(OFF.name().equals(name))return OFF;
		if(DISABLED.name().equals(name))return DISABLED;
		if(PROCESSING.name().equals(name))return PROCESSING;
		return null;
	}
}
