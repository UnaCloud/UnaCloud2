package unacloud

import com.losandes.utils.UnaCloudConstants;

import unacloud.share.enums.UserRestrictionEnum;

/**
 * Entity to represent a group of users. This entity allows to assign restrictions to several users
 * @author CesarF
 *
 */
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
	 * Validates if group is admin group
	 * @return true if group is for administrators, false in case not
	 */
	def boolean isAdmin(){
		return name.equals(UnaCloudConstants.ADMIN_GROUP)
	}
	
	/**
	 * Validates if group is user default group
	 * @return true if group is default group, false in case not
	 */
	def boolean isDefault(){
		return name.equals(UnaCloudConstants.USERS_GROUP)
	}
		
	/**
	 * Searches and returns a restriction that is requested
	 * @param restriction to be search in group restriction list
	 * @return a restriction of user, null if does not exist
	 */
	def getRestriction(UserRestrictionEnum restriction){
		this.restrictions.find{it.name==restriction.toString()}
	}
}
