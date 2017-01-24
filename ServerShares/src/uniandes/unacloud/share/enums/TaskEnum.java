package uniandes.unacloud.share.enums;

/**
 * Enum to represent kind of task that could be request to agents
 * @author CesarF
 *
 */
public enum TaskEnum {
	
	/**
	 * Message to stop executions
	 */
	STOP("stop"),
	/**
	 * Message to update agents
	 */
	UPDATE("update"),
	/**
	 * Message to clear cache from agents
	 */
	CACHE("cache");
	
	private String name;
	
	private TaskEnum(String name) {
		this.name = name;
	}
	
	/**
	 * Returns a task enum requested by name
	 * @param name of enum
	 * @return task enum
	 */
	public static TaskEnum getEnum(String name){
		if(name.equals(STOP.name))return STOP;
		if(name.equals(UPDATE.name))return UPDATE;
		if(name.equals(CACHE.name))return CACHE;
		return null;
	}
	
	/**
	 * Returns name of enum
	 * @return name
	 */
	public String getName(){
		return name;
	}

}
