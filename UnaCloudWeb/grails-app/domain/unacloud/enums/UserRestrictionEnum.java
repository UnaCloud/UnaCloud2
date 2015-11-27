package unacloud.enums;

public enum UserRestrictionEnum {
	HARDWARE_PROFILE_AVAILABLES("Hardware Profiles availables"),
	ALLOWED_LABS("Allowed Labs"),
	ALLOCATOR("Allocator Algorithm");
	
	private String name;
	
	private UserRestrictionEnum(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public static UserRestrictionEnum getRestriction(String title){
		if(title.equals(HARDWARE_PROFILE_AVAILABLES.toString()))return HARDWARE_PROFILE_AVAILABLES;
		else if(title.equals(ALLOWED_LABS.toString()))return ALLOWED_LABS;
		else if(title.equals(ALLOCATOR.toString()))return ALLOCATOR;
		return null;
	}
}
