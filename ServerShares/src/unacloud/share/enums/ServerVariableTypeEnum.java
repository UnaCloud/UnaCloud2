package unacloud.share.enums;

public enum ServerVariableTypeEnum {
	INT("Integer"),STRING("String"),BOOLEAN("Boolean");
	String type; 
	private ServerVariableTypeEnum(String typeString){
		type=typeString;
	}
	
	public static ServerVariableTypeEnum getEnum(String type){
		if(type.equals(INT.name()))return INT;
		if(type.equals(STRING.name()))return STRING;
		if(type.equals(BOOLEAN.name()))return BOOLEAN;
		return null;
	}
}
