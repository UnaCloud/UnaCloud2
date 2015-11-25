package unacloud.enums;

public enum ServerVariableTypeEnum {
	INT("Integer"),STRING("String"),BOOLEAN("Boolean");
	String type; 
	private ServerVariableTypeEnum(String typeString){
		type=typeString;
	}
}
