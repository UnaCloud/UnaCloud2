package uniandes.unacloud.common.enums;

public enum ExecutionProcessEnum {
	
	FAIL,
	
	SUCCESS,
	
	REQUEST;
	
	
	public static ExecutionProcessEnum getEnum(String name) {
		if (name.equals(FAIL.name())) return FAIL;
		if (name.equals(SUCCESS.name())) return SUCCESS;
		if (name.equals(REQUEST.name())) return REQUEST;
		return null;
	}
}
