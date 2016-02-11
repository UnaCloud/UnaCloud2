package unacloud

import org.apache.commons.io.FileUtils

import com.losandes.utils.Constants;

import unacloud.task.queue.QueueTaskerControl;
import unacloud.task.queue.QueueTaskerFile;
import unacloud.utils.Hasher;
import unacloud.share.enums.VirtualMachineExecutionStateEnum;
import unacloud.share.enums.VirtualMachineImageEnum;
import grails.transaction.Transactional

@Transactional
class VirtualMachineImageService {

	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * System separator
	 */
	def separator =  java.io.File.separatorChar
	
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
	 */
	def uploadImage(name, isPublic, accessProtocol, operatingSystemId, username, password,User user) {
		if(user.existImage(name))throw new Exception('Currently you have an image with the same name.')
		Repository repo = userRestrictionService.getRepository(user)
		String token = Hasher.hashSha256(name+new Date().getTime())
		def image= new VirtualMachineImage(owner: user, repository:repo, name: name, lastUpdate:new Date(),
			isPublic: isPublic, imageVersion: 0,accessProtocol: accessProtocol , operatingSystem: OperatingSystem.get(operatingSystemId),
			user: username, password: password, token:token,fixedDiskSize:0, state: VirtualMachineImageEnum.UNAVAILABLE)	
		image.save(failOnError: true)
		return token;
    }
	
	/**
	 * Creates a new image from on a public one
	 * @param name image name
	 * @param publicImage public image used as template
	 * @param user owner user
	 */
	def newPublic(name, imageId, User user){
		if(user.existImage(name))throw new Exception('Currently you have an image with the same name.')
		def publicImage = VirtualMachineImage.get(imageId)
		if(publicImage){
			def repo= userRestrictionService.getRepository(user)
			def image= new VirtualMachineImage(state: VirtualMachineImageEnum.IN_QUEUE, fixedDiskSize: publicImage.fixedDiskSize, 
				owner: user, repository:repo, name: name , avaliable: true, lastUpdate:new Date(),isPublic: false, imageVersion: 0,
				accessProtocol: publicImage.accessProtocol , operatingSystem: publicImage.operatingSystem,user: publicImage.user, 
				password:  publicImage.password)
			image.save(failOnError:true)
			QueueTaskerFile.createCopyFromPublic(publicImage, image, user)			
			return true
		}else return false	
	}
	
	/**
	 * Return all available public images
	 * @return
	 */
	def getAvailablePublicImages(){
		return VirtualMachineImage.where{isPublic==true&&state==VirtualMachineImageEnum.AVAILABLE}.findAll()
	}
	
	/**
	 * Deletes the virtual machine image, virtual machine files and directory
	 * @param user owner user
	 * @param repository image repository
	 * @param image image to be removed
	 */
	
	def deleteImage(User user,VirtualMachineImage image){		
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
	 * Send a task to remove image from cache in all physical machines
	 * @param image
	 * @return
	 */
	def clearCache(VirtualMachineImage image){
		QueueTaskerControl.clearCache(image, image.owner);		
	}
	
	/**
	 * Sets new values for the image
	 * @param image image to be edited
	 * @param name new image name
	 * @param user new belonging user
	 * @param password new image password
	 */
	
	def setValues(VirtualMachineImage image, name, user, password){
		if(image.name!=name&&image.owner.existImage(name))throw new Exception('Currently you have an image with the same name.')
		image.putAt("name", name)
		image.putAt("user", user)
		image.putAt("password", password)
	}
	
	/**
	 * Alter image privacy from public to private (delete public file in imageTemplates folder)
	 * or private to public (create public file in imageTemplates folder).
	 * @param toPublic
	 * @param image
	 * @param user
	 */
	def alterImagePrivacy(toPublic, VirtualMachineImage image){
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
	 */	
	def updateFiles(VirtualMachineImage image){		
		String token = Hasher.hashSha256(image.getName()+new Date().getTime())
		image.putAt("token",token)
		image.putAt("state",VirtualMachineImageEnum.UNAVAILABLE)
		return token
	}
}
