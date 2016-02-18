package communication;

/**
 *Enum to represent types of UDP message
 * @author Cesar
 *
 */
public enum UDPMessageEnum {
	
	STATE_PM,
	STATE_VM,
	LOG_PM;
	
	public static UDPMessageEnum getType(String name){
		if(STATE_PM.name().equals(name))return STATE_PM;
		if(STATE_VM.name().equals(name))return STATE_VM;
		if(LOG_PM.name().equals(name))return LOG_PM;
		return null;
	}

}
