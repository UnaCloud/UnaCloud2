package unacloud.enums;

/**
 * Enum to represent kind of task that could be request to agents
 * @author Cesar
 *
 */
public enum TaskEnum {
	
	STOP("stop"),UPDATE("update"),CACHE("cache");
	
	private String name;
	
	private TaskEnum(String name) {
		this.name = name;
	}
	
	public static TaskEnum getEnum(String name){
		if(name.equals(STOP.name))return STOP;
		if(name.equals(UPDATE.name))return UPDATE;
		if(name.equals(CACHE.name))return CACHE;
		return null;
	}
	
	public String getName(){
		return name;
	}

}
