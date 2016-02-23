package unacloud

import com.losandes.utils.UnaCloudConstants;

import unacloud.share.enums.UserRestrictionEnum;

class UserGroup {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
		
	/**
	 * group name
	 */
	String name
	
	/**
	 * Name to be modified
	 */
	String visualName
	
	/**
	 * list of users that belongs to this group
	 * List of restrictions that apply for all users
	 */
	static hasMany = [users: User, restrictions: UserRestriction]
	
	static constraints = {
		name unique: true, nullable: false
	}
	
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Validate if group is admin group
	 * @return
	 */
	def boolean isAdmin(){
		return name.equals(UnaCloudConstants.ADMIN_GROUP)
	}
	
	/**
	 * Validate if group is user default group
	 * @return
	 */
	def boolean isDefault(){
		return name.equals(UnaCloudConstants.USERS_GROUP)
	}
		
	/**
	 * Return a restriction of user, null if does not exist
	 */
	def getRestriction(UserRestrictionEnum restriction){
		this.restrictions.find{it.name==restriction.toString()}
	}
}
