package unacloud

import org.apache.commons.io.FileUtils

import com.losandes.utils.Constants;

import unacloud.task.queue.QueueTaskerControl;
import unacloud.task.queue.QueueTaskerFile;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.enums.VirtualMachineImageEnum;
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
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
    /**
	 * Uploads a new image
	 * @param files image files
	 * @param diskSize image disk size
	 * @param name image name
	 * @param isPublic indicates if the image will be uploaded as a public image
	 * @param accessProtocol indicates the access protocol configured in the image
	 * @param operatingSystemId image OS
	 * @param username image access user
	 * @param password image access password
	 * @param user owner user
	 */
	def uploadImage(files, name, isPublic, accessProtocol, operatingSystemId, username, password,User user) {
		def copy = null;
		def repo= repositoryService.getMainRepository()
		if(isPublic){	
			File file = new File(repo.root+Constants.TEMPLATE_PATH+separator+name);
			if (file.exists()){
				isPublic=false;
				copy = false;
			}else copy = true
		}		
		//TODO define repository assignment schema		
		def image= new VirtualMachineImage(owner: user, repository:repo, name: name , avaliable: true, lastUpdate:new Date(),
			isPublic: isPublic, imageVersion: 0,accessProtocol: accessProtocol , operatingSystem: OperatingSystem.get(operatingSystemId),
			user: username, password: password)
		def sizeImage = 0;
		files.each {
			def fileName=it.getOriginalFilename()
			java.io.File newFile= new java.io.File(repo.root+image.name+"_"+user.username+separator+fileName+separator)
			newFile.mkdirs()
			it.transferTo(newFile)			
			if(image.isPublic){
				QueueTaskerFile.createPublicCopy(image, user)
				image.freeze()
			}			
			if (fileName.endsWith(".vmx")||fileName.endsWith(".vbox")){
				image.putAt("mainFile", repo.root+image.name+"_"+user.username+separator+fileName)		
			}
			sizeImage += it.getSize()
		}		
		image.setFixedDiskSize(sizeImage)
		image.save(failOnError: true)
		return copy;
    }
	
	/**
	 * Creates a new image from on a public one
	 * @param name image name
	 * @param publicImage public image used as template
	 * @param user owner user
	 */
	def newPublic(name, imageId, User user){
		def publicImage = VirtualMachineImage.get(imageId)
		if(publicImage){
			def repo= repositoryService.getMainRepository()
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
		QueueTaskerFile.deleteImage(image, user)
		image.freeze()
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
		if(!toPublic && image.isPublic){
			QueueTaskerFile.deletePublicImage(image, image.owner);
		}else if(toPublic && !image.isPublic){
			QueueTaskerFile.createPublicCopy(image, image.owner);
		}	
		image.freeze()
	}
	
	/**
	 * Changes image files for the files uploaded by user
	 * @param i image to be edited
	 * @param files new set of files
	 * @param user owner user
	 */
	
	def updateFiles(VirtualMachineImage image, files, User user){
		new java.io.File(image.mainFile).getParentFile().deleteDir()
		def repo= repositoryService.getMainRepository()
		def sizeImage = 0;
		files.each {
			def file=it.getOriginalFilename()
			java.io.File newFile= new java.io.File(repo.root+image.name+"_"+user.username+separator+file+separator)
			newFile.mkdirs()
			it.transferTo(newFile)
			if(image.isPublic){
				 QueueTaskerFile.createPublicCopy(image, user)
				 image.freeze()
			}
			if (file.endsWith(".vmx")||file.endsWith(".vbox"))
			image.putAt("mainFile", repo.root+image.name+"_"+user.username+separator+file)
			image.putAt("imageVersion", image.imageVersion++)		
			sizeImage += it.getSize()
		}		
		image.setFixedDiskSize(sizeImage)
		image.save(failOnError: true)		
	}
}
