package uniandes.unacloud.web.services

import org.apache.commons.lang3.RandomStringUtils;

import uniandes.unacloud.share.enums.UserRestrictionEnum;
import uniandes.unacloud.share.enums.UserStateEnum;
import uniandes.unacloud.web.queue.QueueTaskerControl;
import uniandes.unacloud.web.queue.QueueTaskerFile;
import uniandes.unacloud.web.utils.java.Hasher;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.domain.UserRestriction;

/**
 * This service contains all methods to manage User: User crud methods, change password, validate login, and changes api key.
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
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
	
	/**
	 * Returns a user searched by password and username
	 * @param username
	 * @param password
	 * @return
	 */
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
	
    def addUser(String username, String name, String description, String password,String email) {
	   String charset = (('A'..'Z') + ('0'..'9')).join()
	   Integer length = 32
	   String randomString = RandomStringUtils.random(length, charset.toCharArray())
	   def user= new User(username: username, name: name, description: description, password:Hasher.hashSha256(password), apiKey: randomString, registerDate: new Date(),email:email)
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
	 * Creates an apikey for user
	 * @return
	 */
	def designAPIKey(){
		String charset = (('A'..'Z') + ('0'..'9')).join()
		Integer length = 32
		return RandomStringUtils.random(length, charset.toCharArray())
	}
	
	/**
	 * Puts the task to delete user, deployments, images, clusters
	 * @param user user to be removed
	 */	
	def deleteUser(User user, User admin) throws Exception{
		if(user.getActiveDeployments().size()>0)throw new Exception('User has currently active deployments')
		if(user.getNotAvailableImages().size()>0)throw new Exception('It is necessary that all images have AVAILABLE state')
		user.deprecate()
		userGroupService.removeUser(user);
		if(user.images.size()>0){						
			QueueTaskerFile.deleteUser(user, admin);			
		}else{
			user.delete()
		}		
	}
	
	/**
	 * Edits the user info
	 * @param user user to be edited
	 * @param username new username
	 * @param name new name
	 * @param description of user
	 * @param password new password
	 */
	
	def setValues(User user, String username, String name, String description, String password, String email){	
		user.setName(name)
		user.setDescription(description)
		user.setUsername(username)
		user.setEmail(email)
		if(password){			
			user.setPassword( Hasher.hashSha256(password))
		}
		user.save(failOnError:true)
		return user
	}
	
	/**
	 * Searches a restriction in user
	 * @param user
	 * @param restriction
	 * @return restriction, null if it does not exist
	 */
	def getRestrictionByUser(User user, String restriction){
		return user.restrictions.find{it.name==UserRestrictionEnum.ALLOCATOR.toString()}
	}
	
	/**
	 * Sets a new user restriction to the given user
	 * @param user user with the new restriction
	 * @param name restriction name
	 * @param value restriction value
	 */
	
	def setRestriction(User user, String name, String value){
		UserRestriction old = user.restrictions.find{it.name==name}
		if(!old&&value){			
			def newRestriction= new UserRestriction(name: name, value: value)
			newRestriction.save(failOnError: true)
			user.restrictions.add(newRestriction)
			user.save(failOnError: true)
		}
		else{
			if(!value||value.equals("")){
				user.restrictions.remove(old)
				old.delete()
			}
			else{
				old.setValue(value)
				old.save(failOnError: true)
			}
		}
	}
	
	/**
	 * Changes a current password in user
	 * @param user to be modified
	 * @param password current Password
	 * @param newPassword new Password
	 */
	def changePassword(User user, String password, String newPassword){
		if(!user.password.equals(Hasher.hashSha256(password)))throw new Exception('Current Password is invalid')
		user.setPassword(Hasher.hashSha256(newPassword))
		user.save(failOnError:true)
	}
	
	/**
	 * Returns a requested user based in userId
	 * @param userId
	 * @return user requested by id
	 */
	def User getUser(long userId){
		return User.get(userId)
	}
}
