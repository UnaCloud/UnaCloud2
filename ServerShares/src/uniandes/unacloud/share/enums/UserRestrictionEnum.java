package uniandes.unacloud.share.enums;

/**
 * Represents types of User Restriction 
 * @author CesarF
 *
 */
public enum UserRestrictionEnum {
	
	/**
	 * Restriction of Hardware Profile available
	 */
	HARDWARE_PROFILE_AVAILABLES("Hardware Profiles availables"),
	
	/**
	 * Restriction of allowed lab to deploy images
	 * 
	*/
	ALLOWED_LABS("Allowed Labs"),
	
	/**
	 * Restriction of allowed allocator by user
	 */
	ALLOCATOR("Allocator Algorithm"),	
	
	/**
	 * Restriction of allowed repository to save images
	 */
	REPOSITORY("Repository");
	
	private String name;
	
	private UserRestrictionEnum(String name) {
		this.name = name;
	}
	
	/**
	 * name of user restriction type
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns type of user restriction searched  by name
	 * @param title name of user restriction
	 * @return type of user restriction, could return null
	 */
	public static UserRestrictionEnum getRestriction(String title){
		if (title.equals(HARDWARE_PROFILE_AVAILABLES.toString())) return HARDWARE_PROFILE_AVAILABLES;
		else if (title.equals(ALLOWED_LABS.toString())) return ALLOWED_LABS;
		else if (title.equals(ALLOCATOR.toString())) return ALLOCATOR;
		else if (title.equals(REPOSITORY.toString())) return REPOSITORY;
		return null;
	}
}
