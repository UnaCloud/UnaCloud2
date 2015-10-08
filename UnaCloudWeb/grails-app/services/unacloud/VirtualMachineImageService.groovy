package unacloud

import org.apache.commons.io.FileUtils

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
	//TODO this service must works in batch when is public to create copy
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
			java.io.File newFile= new java.io.File(repo.root+image.name+"_"+user.username+separator+it.getOriginalFilename()+separator)
			newFile.mkdirs()
			it.transferTo(newFile)
			//TODO Create task
//			if(image.isPublic){
//				def templateFile= new java.io.File(repo.root+"imageTemplates"+separator+image.name+separator+it.getOriginalFilename())
//				FileUtils.copyFile(newFile, templateFile)				
//			}
			if (fileName.endsWith(".vmx")||fileName.endsWith(".vbox")){
				image.putAt("mainFile", repo.root+image.name+"_"+user.username+separator+it.getOriginalFilename())		
			}
			sizeImage += it.getSize()
		}		
		image.setFixedDiskSize(sizeImage)
		image.save(failOnError: true)
		return copy;
    }
	
	/**
	 * Creates a new image based on a public one
	 * @param name image name
	 * @param publicImage public image used as template
	 * @param user owner user
	 */
	//TODO this service must works in batch and alter password
	def newPublic(name, imageId, User user){
		def publicImage = VirtualMachineImage.get(imageId)
		if(publicImage){
			def repo= repositoryService.getMainRepository()
			def image= new VirtualMachineImage(state: VirtualMachineImageEnum.IN_QUEUE, fixedDiskSize: publicImage.fixedDiskSize, 
				owner: user, repository:repo, name: name , avaliable: true, lastUpdate:new Date(),isPublic: false, imageVersion: 0,
				accessProtocol: publicImage.accessProtocol , operatingSystem: publicImage.operatingSystem,user: publicImage.user, 
				password:  publicImage.password)
			print image
			//TODO Create Task
//			java.io.File folder= new java.io.File(publicImage.mainFile.substring(0, publicImage.mainFile.lastIndexOf(separator.toString())))
//			println folder.toString()
//			//TODO define repository assignment schema
//			folder.listFiles().each
//			{
//				def file= new java.io.File(repo.root+"imageTemplates"+separator+publicImage.name+separator+it.getName())
//				def newFile= new java.io.File(repo.root+image.name+"_"+user.username+separator+it.getName())
//				FileUtils.copyFile(file, newFile)
//				if (it.getName().endsWith(".vmx")||it.getName().endsWith(".vbox"))
//					image.putAt("mainFile", repo.root+image.name+"_"+user.username+separator+newFile.getName())
//			}
			image.save(failOnError:true)
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
}
