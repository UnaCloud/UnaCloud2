package unacloud

import org.apache.commons.lang.RandomStringUtils;

import unacloud.utils.Hasher;

class UserService {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	
	/**
	 * Representation of group services
	 */
	
	UserGroupService userGroupService
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	def User getUser(String username, String password){
		return User.findWhere(username:username,password:Hasher.hashSha256(password));
	}
	/**
	 * Adds a new user
	 * @param username new user's username
	 * @param name fullname
	 * @param description of user
	 * @param password user password
	 */
	
    def addUser(String username, String name, String description, String password) {
	   String charset = (('A'..'Z') + ('0'..'9')).join()
	   Integer length = 32
	   String randomString = RandomStringUtils.random(length, charset.toCharArray())
	   def user= new User(username: username, name: name, description: description, password:Hasher.hashSha256(password), apiKey: randomString, registerDate: new Date())
	   user.images=[]
	   user.restrictions=[]
	   user.userClusters=[]
	   user.deployments=[]
	   user.save()
	   userGroupService.addToGroup(userGroupService.getDefaultGroup(), user)	   
    }
	
	/**
	 * Creates a new API key for the given user
	 * @param u user which API key will be generated and replaced
	 */
	
	def refreshAPIKey(User u){
		String randomString = designAPIKey()
		u.apiKey=randomString
		u.save()
		return u.apiKey
	}
	
	/**
	 * Desing an apikey for user
	 * @return
	 */
	def designAPIKey(){
		String charset = (('A'..'Z') + ('0'..'9')).join()
		Integer length = 32
		return RandomStringUtils.random(length, charset.toCharArray())
	}
}
