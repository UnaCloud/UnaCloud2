package unacloud.enums;

public enum UserRestrictionEnum {
	MAX_RAM_PER_VM("Max Ram per Virtual Machine"),
	MAX_CORES_PER_VM("Max Cores per Virtual Machine"),
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
		if(title.equals(MAX_CORES_PER_VM.toString()))return MAX_CORES_PER_VM;
		else if(title.equals(MAX_RAM_PER_VM.toString()))return MAX_RAM_PER_VM;
		else if(title.equals(ALLOWED_LABS.toString()))return ALLOWED_LABS;
		else if(title.equals(ALLOCATOR.toString()))return ALLOCATOR;
		return null;
	}
}
