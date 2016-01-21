package unacloud.enums;

public enum PhysicalMachineStateEnum {
	
	ON,OFF,DISABLED;
	
	public static PhysicalMachineStateEnum getEnum(String name){
		if(ON.name().equals(name))return ON;
		if(OFF.name().equals(name))return OFF;
		if(DISABLED.name().equals(name))return DISABLED;
		return null;
	}
}
