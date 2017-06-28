package uniandes.unacloud.share.enums;

/**
 * Represents states of User Entity
 * @author CesarF
 *
 */
public enum UserStateEnum {
	
	/**
	 * User is available
	 */
	AVAILABLE("AVAILABLE"), 
	/**
	 * User is disabled while is deleting
	 */
	DISABLE("DISABLE"), 
	/**
	 * User is blocked by admin
	 */
	BLOCKED("BLOCKED");
	
	private String name;
	
	private UserStateEnum(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Returns user state searched by name
	 * @param name of user state
	 * @return User state enum
	 */
	public static UserStateEnum getEnum(String name) {
		if (name.equals(AVAILABLE.name) || name.equals(AVAILABLE.name())) return AVAILABLE;
		if (name.equals(DISABLE.name) || name.equals(DISABLE.name())) return DISABLE;
		if (name.equals(BLOCKED.name) || name.equals(BLOCKED.name())) return BLOCKED;
		return null;
	}
}
