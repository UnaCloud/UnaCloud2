package uniandes.unacloud.common.enums;

/**
 * Represents task result process in agent
 * @author CesarF
 *
 */
public enum ExecutionProcessEnum {
	
	/**
	 * If task fail
	 */
	FAIL,
	
	/**
	 * Is task was successful
	 */
	SUCCESS,
	
	/**
	 * If task change status to requested
	 */
	REQUEST;
	
	/**
	 * Return enum using string name
	 * @param name
	 * @return
	 */
	public static ExecutionProcessEnum getEnum(String name) {
		if (name.equals(FAIL.name())) return FAIL;
		if (name.equals(SUCCESS.name())) return SUCCESS;
		if (name.equals(REQUEST.name())) return REQUEST;
		return null;
	}
}
