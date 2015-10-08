package unacloud.enums;

public enum ServerVariableTypeEnum {
	INT("Integer"),STRING("String");
	String type; 
	private ServerVariableTypeEnum(String typeString){
		type=typeString;
	}
}
