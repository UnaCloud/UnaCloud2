package uniandes.unacloud.common.net.udp.message;

/**
 * Enum to represent types of UDP message
 * @author CesarF
 *
 */
public enum UDPMessageEnum {
	
	/**
	 * Message to report Physical Machines
	 */
	STATE_PM,
	
	/**
	 * Message to reports changes in Executions
	 */
	STATE_EXE,
	
	/**
	 * Message to add value to log database
	 * unused
	 */
	LOG_PM;
	
	/**
	 * Returns type of message requested by name
	 * @param name of type
	 * @return type of message
	 */
	public static UDPMessageEnum getType(String name) {
		if (STATE_PM.name().equals(name)) return STATE_PM;
		if (STATE_EXE.name().equals(name)) return STATE_EXE;
		if (LOG_PM.name().equals(name)) return LOG_PM;
		return null;
	}

}
