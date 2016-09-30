package uniandes.unacloud.web.services

import org.apache.commons.io.FileUtils

import uniandes.unacloud.web.queue.QueueTaskerControl;
import uniandes.unacloud.web.queue.QueueTaskerFile;
import uniandes.unacloud.web.utils.java.Hasher;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.web.domain.Cluster;
import uniandes.unacloud.web.domain.DeployedImage;
import uniandes.unacloud.web.domain.OperatingSystem;
import uniandes.unacloud.web.domain.Platform
import uniandes.unacloud.web.domain.Repository;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.domain.Image;

import grails.transaction.Transactional

/**
 * This service contains all methods to manage User Virtual Machine Image: Crud methods, some methods send tasks in queue
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
@Transactional
class ImageService {

	// Properties
	//-----------------------------------------------------------------
		
	/**
	 * repository service
	 */
	RepositoryService repositoryService
	
	/**
	 * User restriction service representation
	 */
	UserRestrictionService userRestrictionService
	
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
    /**
	 * creates a new image with an unique token 
	 * @param diskSize image disk size
	 * @param name image name
	 * @param isPublic indicates if the image will be uploaded as a public image
	 * @param accessProtocol indicates the access protocol configured in the image
	 * @param operatingSystemId image OS
	 * @param username image access user
	 * @param password image access password
	 * @param user owner user
	 * @return token to validate image 
	 */
	def uploadImage(name, isPublic, accessProtocol, operatingSystemId, username, password,User user) {
		if(user.existImage(name))throw new Exception('Currently you have an image with the same name.')
		Repository repo = userRestrictionService.getRepository(user)
		String token = Hasher.hashSha256(name+new Date().getTime())
		def image= new Image(owner: user, repository:repo, name: name, lastUpdate:new Date(),
			isPublic: isPublic, imageVersion: 0,accessProtocol: accessProtocol , operatingSystem: OperatingSystem.get(operatingSystemId),
			user: username, password: password, token:token,fixedDiskSize:0, state: ImageEnum.UNAVAILABLE)	
		image.save(failOnError: true)
		return token;
    }
	
	/**
	 * Creates a new image from on a public one
	 * @param name image name
	 * @param publicImage public image used as template
	 * @param user owner user
	 * @return true in case task has been send, false in case not
	 */
	def newPublic(name, imageId, User user){
		if(user.existImage(name))throw new Exception('Currently you have an image with the same name.')
		def publicImage = Image.get(imageId)
		if(publicImage){
			def repo= userRestrictionService.getRepository(user)
			def image= new Image(state: ImageEnum.IN_QUEUE, fixedDiskSize: publicImage.fixedDiskSize, 
				owner: user, repository:repo, name: name , avaliable: true, lastUpdate:new Date(),isPublic: false, imageVersion: 0,
				accessProtocol: publicImage.accessProtocol , operatingSystem: publicImage.operatingSystem,user: publicImage.user, 
				password:  publicImage.password)
			image.save(failOnError:true)
			QueueTaskerFile.createCopyFromPublic(publicImage, image, user)			
			return true
		}else return false	
	}
	
	/**
	 * Returns all available public images
	 * @return list of available public images
	 */
	def getAvailablePublicImages(){
		return Image.where{isPublic==true&&state==ImageEnum.AVAILABLE}.findAll()
	}
	
	/**
	 * Deletes the virtual machine image, virtual machine files and directory
	 * @param user owner user
	 * @param repository image repository
	 * @param image image to be removed
	 * @return true in case image has been deleted, false in case not
	 */
	
	def deleteImage(User user,Image image){		
		def clusteres = Cluster.where{images{id==image.id;}}.findAll();
		if(clusteres&&clusteres.size()>0){
			return false;
		}
		DeployedImage.executeUpdate("update DeployedImage di set di.image=null where di.image.id= :id",[id:image.id]);
		image.freeze()
		QueueTaskerFile.deleteImage(image, user)		
		return true;
	}
	
	/**
	 * Sends a task to remove image from cache in all physical machines
	 * @param image
	 */
	def clearCache(Image image){
		image.freeze()
		QueueTaskerControl.clearCache(image, image.owner);		
	}
	
	/**
	 * Sets new values for the image
	 * @param image image to be edited
	 * @param name new image name
	 * @param user new belonging user
	 * @param password new image password
	 */
	
	def setValues(Image image, name, user, password){
		if(image.name!=name&&image.owner.existImage(name))throw new Exception('Currently you have an image with the same name.')
		image.putAt("name", name)
		image.putAt("user", user)
		image.putAt("password", password)
	}
	
	/**
	 * Alters image privacy from public to private (delete public file in imageTemplates folder)
	 * or private to public (create public file in imageTemplates folder).
	 * @param toPublic
	 * @param image
	 * @param user
	 */
	def alterImagePrivacy(toPublic, Image image){
		image.freeze()
		if(!toPublic && image.isPublic){
			QueueTaskerFile.deletePublicImage(image, image.owner);
		}else if(toPublic && !image.isPublic){
			QueueTaskerFile.createPublicCopy(image, image.owner);
		}			
	}
	
	/**
	 * creates a token to be used to upload image in file manager project
	 * @param i image to be edited
	 * @param files new set of files
	 * @param user owner user
	 * @return token to validates image
	 */	
	def updateFiles(Image image){		
		String token = Hasher.hashSha256(image.getName()+new Date().getTime())
		image.putAt("token",token)
		image.putAt("state",ImageEnum.UNAVAILABLE)
		return token
	}
	
	/**
	 * Returns list of images which have linked 
	 * @param plat
	 * @return
	 */
	def getListMachinesByPlatform(Platform plat){
		return Image.where{platform==plat}.findAll()
	}
}
