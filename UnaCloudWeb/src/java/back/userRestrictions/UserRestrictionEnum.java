package back.userRestrictions;

public enum UserRestrictionEnum {
	MAX_RAM_PER_VM(new MaxRamPerVMRestriction(),"Max RAM per VM"),MAX_CORES_PER_VM(new MaxCoresPerVMRestriction(),"Max cores per VM"),ALLOWED_LABS(new LaboratoryRestriction(),"Allowed Labs");
	UserRestrictionInterface userRestrictionInterface;
	String name;

	private UserRestrictionEnum(UserRestrictionInterface uRestriction,String name) {
		this.userRestrictionInterface=uRestriction;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public UserRestrictionInterface getUserRestriction(){
		return userRestrictionInterface;
	}
	
}
