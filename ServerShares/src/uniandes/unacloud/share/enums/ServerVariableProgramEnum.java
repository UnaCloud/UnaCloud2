package uniandes.unacloud.share.enums;

/**
 * Represents types of Server Variable by environment
 * @author CesarF
 *
 */
public enum ServerVariableProgramEnum {
	
	/**
	 * Variable that should be used for agent
	 */
	AGENT,
	/**
	 * Variable used by Control project
	 */
	CONTROL,
	/**
	 * Variable used by File Manager Project
	 */
	FILE_MANAGER,
	/**
	 * Variable used by Web Project
	 */
	WEB,
	/**
	 * Variable used for all web projects (web, file and control managers)
	 */
	SERVER;
	
	/**
	 * Returns a server variable base in type
	 * @param type
	 * @return server variable program
	 */
	public static ServerVariableProgramEnum getEnum(String type) {
		if (type.equals(AGENT.name())) return AGENT;
		if (type.equals(CONTROL.name())) return CONTROL;
		if (type.equals(FILE_MANAGER.name())) return FILE_MANAGER;
		if (type.equals(WEB.name())) return WEB;
		if (type.equals(SERVER.name())) return SERVER;
		return null;
	}

}
