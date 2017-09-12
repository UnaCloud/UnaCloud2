package uniandes.unacloud.share.enums;

/**
 * Enum to represent states of Physical machine
 * @author CesarF
 *
 */
public enum PhysicalMachineStateEnum {
	
	/**
	 * Physical Machine has been reported for last minutes to server
	 */
	ON,
	/**
	 * Physical Machines has not been reported to server for last minutes
	 */
	OFF,
	/**
	 * Admin user has disabled machine
	 */
	DISABLED,
	/**
	 * Physical Machine has a task in queue
	 */
	PROCESSING;
	
	/**
	 * Return a physical machine states search by name
	 * @param name to be searched
	 * @return enum or null
	 */
	public static PhysicalMachineStateEnum getEnum(String name) {
		if (ON.name().equals(name)) return ON;
		if (OFF.name().equals(name)) return OFF;
		if (DISABLED.name().equals(name)) return DISABLED;
		if (PROCESSING.name().equals(name)) return PROCESSING;
		return null;
	}
}
