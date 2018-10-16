package uniandes.unacloud.common.enums;

import uniandes.unacloud.common.net.tcp.message.AgentMessage;

/**
 * Enum to represent kind of task that could be request to agents
 * @author CesarF
 *
 */
public enum TaskEnum {
	
	/**
	 * Message to stop executions
	 */
	STOP("stop", AgentMessage.STOP_CLIENT),
	
	/**
	 * Message to update agents
	 */
	UPDATE("update", AgentMessage.UPDATE_OPERATION),
	
	/**
	 * Message to clear cache from agents
	 */
	CACHE("cache", AgentMessage.CLEAR_CACHE),
	
	/**
	 * Message to clear cache from agents
	 */	
	VERSION("version", AgentMessage.GET_VERSION),
	
	/**
	 * Message to clear cache from agents
	 */	
	DATA_SPACE("size", AgentMessage.GET_DATA_SPACE),	
	
	
	/**
	 * Message to request log from agents
	 */
	GET_LOGS("logs", AgentMessage.GET_LOGS),
	/**
	 * Message to request monitoring files from agent
	 */
	GET_MONITORING("monitoring",AgentMessage.GET_MONITORING),
	/**
	 * Message for clearing copies from memory
	 */
	CLEAR_COPY("copy",AgentMessage.CLEAR_COPY);


	
	/**
	 * Name of task
	 */
	private String name;
	
	/**
	 * Unique identifier for task
	 */
	private int id;
	
	/**
	 * Creates a new Enum
	 * @param name
	 */
	private TaskEnum(String name, int numberTask) {
		this.name = name;
		this.id = numberTask;
	}
	
	/**
	 * Returns a task enum requested by name
	 * @param name of enum
	 * @return task enum
	 */
	public static TaskEnum getEnum(String name) {
		if (name.equals(STOP.name)) return STOP;
		if (name.equals(UPDATE.name)) return UPDATE;
		if (name.equals(CACHE.name)) return CACHE;
		if (name.equals(DATA_SPACE.name)) return DATA_SPACE;
		if (name.equals(VERSION.name)) return VERSION;
		if (name.equals(GET_LOGS.name)) return GET_LOGS;
		if (name.equals(GET_MONITORING.name)) return GET_MONITORING;
		if (name.equals(CLEAR_COPY.name)) return CLEAR_COPY;
		return null;
	}
	
	/**
	 * Returns name of enum
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns number of task
	 * @return
	 */
	public int getId() {
		return id;
	}

}