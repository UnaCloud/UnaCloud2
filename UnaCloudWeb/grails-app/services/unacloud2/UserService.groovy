package unacloud2

import org.apache.commons.lang.RandomStringUtils;

class UserService {
	
	/**
	 * Adds a new user to teh system
	 * @param username new user's username
	 * @param name new user's name
	 * @param userType type of user (Administrator or simply User)
	 * @param password user password
	 */
	
    def addUser(String username, String name, String userType, String password) {
	   String charset = (('A'..'Z') + ('0'..'9')).join()
	   Integer length = 32
	   String randomString = RandomStringUtils.random(length, charset.toCharArray())
	   def u= new User(username: username, name: name, userType: userType, password:password , apiKey: randomString )
	   u.save()
	   
    }
	
	/**
	 * Deletes the selected user
	 * @param user user to be removed
	 */
	
	def deleteUser(User user){
		user.delete()
	}
	
	/**
	 * Edits the user info
	 * @param user user to be edited
	 * @param username new username
	 * @param name new name
	 * @param userType new user type
	 * @param password new password
	 */
	
	def setValues(User user, String username, String name, String userType, String password){
		user.putAt("username", username)
		user.putAt("password", password)
		user.putAt("name", name)
		user.putAt("userType", userType)		
	}
	
	/**
	 * Sets a new user restriction to the given user
	 * @param u user with the new restriction
	 * @param name restriction name
	 * @param value restriciton value
	 */
	
	def setPolicy(User u, String name, String value){
		UserRestriction oldAlloc
		for(allocPolicy in u.restrictions){
			if(allocPolicy.name.equals(name)){
				oldAlloc= allocPolicy
			}
		}
		println "alloc found:"+oldAlloc
		if(oldAlloc==null){
			
			def alloc= new UserRestriction(name: name, value: value)
			alloc.save(failOnError: true)
			println "alloc created:"+alloc
			u.addToRestrictions(alloc)
			u.save(failOnError: true)
		}
		else{
			println "setting value on oldAlloc:"+oldAlloc
			if(value.equals("")){
				u.restrictions.remove(oldAlloc)
				oldAlloc.delete()	
			}
			else{
			oldAlloc.setValue(value)
			oldAlloc.save(failOnError: true)
			}
		}
	}
	
	/**
	 * Changes user's password
	 * @param u user which password will be changed
	 * @param newPass new password
	 */
	
	def changePass(User u, String newPass){
		u.password=newPass
		u.save()	
	}
	
	/**
	 * Creates a new API key for the given user
	 * @param u user which API key will be generated and replaced
	 */
	
	def refreshAPIKey(User u){
		String charset = (('A'..'Z') + ('0'..'9')).join()
		Integer length = 32
		String randomString = RandomStringUtils.random(length, charset.toCharArray())
		u.apiKey=randomString
		u.save()
		return u.apiKey
	}
}
