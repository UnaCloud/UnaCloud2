package uniandes.unacloud.share.enums;

/**
 * Represents types of variable
 * 
 * @author CesarF
 *
 */
public enum ServerVariableTypeEnum {
	/**
	 * To manage integer type
	 */
	INT("Integer"),
	
	/**
	 * To manage String type
	 */
	STRING("String"),
	
	/**
	 * To manage Boolean type
	 */
	BOOLEAN("Boolean");
	
	String type; 
	
	private ServerVariableTypeEnum(String typeString) {
		type = typeString;
	}
	
	/**
	 * Returns a Server variable type requested by name
	 * @param type of server variable
	 * @return Server Variable type
	 */
	public static ServerVariableTypeEnum getEnum(String type) {
		if (type.equals(INT.name())) return INT;
		if (type.equals(STRING.name())) return STRING;
		if (type.equals(BOOLEAN.name())) return BOOLEAN;
		return null;
	}
}
