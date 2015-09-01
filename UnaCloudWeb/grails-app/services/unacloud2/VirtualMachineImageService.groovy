package unacloud2

import java.nio.file.Path
import java.nio.file.Files

import org.apache.commons.io.FileUtils
import org.junit.internal.runners.statements.FailOnTimeout;
import org.springframework.transaction.annotation.Transactional;

import unacloud2.enums.VirtualMachineImageEnum;

class VirtualMachineImageService {	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * System separator 
	 */
	def separator =  java.io.File.separatorChar
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Gets the image given its id
	 * @param id id of the image
	 * @return image requested if exists
	 */
	public VirtualMachineImage getImage(long id){
		return VirtualMachineImage.get(id)
	}
	
    
	/**
	 * Sets new values for the image
	 * @param image image to be edited
	 * @param name new image name
	 * @param user new belonging user
	 * @param password new image password
	 */
	
	def setValues(VirtualMachineImage image, name, user, password, isPublic){
		image.putAt("name", name)
		image.putAt("user", user)
		image.putAt("password", password)
		image.putAt("isPublic", isPublic)
	}
	
	
	def setExternalId(VirtualMachineImage image, externalId){
		image.putAt("externalId", externalId)
	}
	/**
	 * Changes image files for the files uploaded by user
	 * @param i image to be edited
	 * @param files new set of files
	 * @param user owner user 
	 */
	
	def updateFiles(VirtualMachineImage i, files, User user){
		new java.io.File(i.mainFile).getParentFile().deleteDir()
		def repository= Repository.findByName("Main Repository")
		files.each {
			def e=it.getOriginalFilename()
			java.io.File newFile= new java.io.File(repository.root+i.name+"_"+user.username+separator+it.getOriginalFilename()+separator)
			newFile.mkdirs()
			it.transferTo(newFile)
			if(i.isPublic){
				def templateFile= new java.io.File(repository.root+"imageTemplates"+separator+i.name+separator+it.getOriginalFilename())
				FileUtils.copyFile(newFile, templateFile)
			}
			if (e.endsWith(".vmx")||e.endsWith(".vbox"))
			i.putAt("mainFile", repository.root+i.name+"_"+user.username+separator+it.getOriginalFilename())
			i.putAt("imageVersion", i.imageVersion++)
		}
		
	}
	
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
	
	def uploadImage(files, diskSize, name, isPublic, accessProtocol, operatingSystemId, username, password,User user) {
		def copy = null;
		def repository= Repository.findByName("Main Repository")
		if(isPublic){	
			File f = new File(repository.root+"imageTemplates"+separator+name);
			if (f.exists()){
				isPublic=false;
				copy = false;
			}else copy = true
		}		
		//TODO define repository assignment schema		
		def i= new VirtualMachineImage( fixedDiskSize: diskSize, name: name , avaliable: true,
			isPublic: isPublic, imageVersion: 0,accessProtocol: accessProtocol , operatingSystem: OperatingSystem.get(operatingSystemId),
			user: username, password: password)
		i.save(failOnError: true)
		files.each {
			def e=it.getOriginalFilename()
			java.io.File newFile= new java.io.File(repository.root+i.name+"_"+user.username+separator+it.getOriginalFilename()+separator)
			newFile.mkdirs()
			it.transferTo(newFile)
			if(i.isPublic){
				def templateFile= new java.io.File(repository.root+"imageTemplates"+separator+i.name+separator+it.getOriginalFilename())
				FileUtils.copyFile(newFile, templateFile)
				
			}
			if (e.endsWith(".vmx")||e.endsWith(".vbox"))
				i.putAt("mainFile", repository.root+i.name+"_"+user.username+separator+it.getOriginalFilename())
		
		}		
		if(user.images==null)
			user.images
		user.images.add(i)
		user.save()
		if(repository.images==null)
			repository.images
		repository.images.add(i)
		repository.save()
		
		return copy;
    }
	
	/**
	 * Deletes the virtual machine image and virtaul machine files and directory
	 * @param user owner user
	 * @param repository image repository 
	 * @param image image to be removed
	 */
	
	def deleteImage(User user, Repository repository, VirtualMachineImage image){
		for(depImage in DeployedImage.getAll()){
			if(depImage.image.equals(image)){
				println "borrando depImage"
				depImage.putAt("image", null)
			}
		}		
		try{//delete files
			File f = new java.io.File(image.mainFile);
			f.getParentFile().deleteDir();
		}catch(Exception e){
		    e.printStackTrace();
		}	
		if(image.isPublic){//Delete public copy
			try{
				deletePublicImage(image);
			}catch(Exception e){
				e.printStackTrace();
			}
		}				
		repository.images.remove(image)
		repository.save()
		user.images.remove(image)
		user.save()
		image.delete()		
	}
	
	/**
	 * Creates a new image based on a public one
	 * @param name image name
	 * @param publicImage public image used as template
	 * @param user owner user
	 */
	
	def newPublic(name, VirtualMachineImage publicImage, User user){
		
		def i= new VirtualMachineImage(fixedDiskSize: 0, imageVersion: 0,name: name, isPublic: false, accessProtocol: publicImage.accessProtocol ,operatingSystem: publicImage.operatingSystem, user: publicImage.user, password: publicImage.password)
		i.save(onFailError:true)
		java.io.File folder= new java.io.File(publicImage.mainFile.substring(0, publicImage.mainFile.lastIndexOf(separator.toString())))
		println folder.toString()
		//TODO define repository assignment schema
		def repository= Repository.findByName("Main Repository")
		folder.listFiles().each
		{
			def file= new java.io.File(repository.root+"imageTemplates"+separator+publicImage.name+separator+it.getName())
			def newFile= new java.io.File(repository.root+i.name+"_"+user.username+separator+it.getName())
			FileUtils.copyFile(file, newFile)
			if (it.getName().endsWith(".vmx")||it.getName().endsWith(".vbox"))
				i.putAt("mainFile", repository.root+i.name+"_"+user.username+separator+newFile.getName())
		}
		if(user.images==null)
			user.images
		user.images.add(i)
		user.save()
		if(repository.images==null)
			repository.images
		repository.images.add(i)
		repository.save()
	}	
	
	def deletePublicImage(VirtualMachineImage publicImage){
		def repository= Repository.findByName("Main Repository")
		File f = new java.io.File(repository.root+"imageTemplates"+separator+publicImage.name+separator);
		f.deleteDir();
	}
	/**
	 * Alter image privacy, from public to private (delete public file in imageTemplates folder) 
	 * or private to public (create public file in imageTemplates folder). 
	 * @param toPublic
	 * @param image
	 * @param user
	 * @return success (true) if process finish successfully, 
	 * failed (false) if currently exists a public image with the same name
	 */
	def alterImagePrivacy(toPublic, VirtualMachineImage image, User user){		
		boolean success = false;
		if(!toPublic && image.isPublic){//Delete public image
			deletePublicImage(image);
			success = true;
		}else if(toPublic && !image.isPublic){//Create public image
			def repository= Repository.findByName("Main Repository")
			File f = new File(repository.root+"imageTemplates"+separator+image.name);
			if (!f.exists()){			
				java.io.File folder= new java.io.File(image.mainFile.substring(0, image.mainFile.lastIndexOf(separator.toString())))
				println folder.toString()				
				//TODO define repository assignment schema				
				folder.listFiles().each
				{
					def file= new java.io.File(repository.root+image.name+"_"+user.username+separator+it.getName())
					def newFile= new java.io.File(repository.root+"imageTemplates"+separator+image.name+separator+it.getName())					
					FileUtils.copyFile(file, newFile)
				}
				success = true;
			}			
		}		
		return success;
	}
	/**
	 * Return image owner 
	 * @param image
	 * @return
	 */
	def User getUserByImage(VirtualMachineImage image){
		if(image==null)return null
		User user = User.where{
			images{image}
		}.find([max:1])
		return user
	}
	
	/**
	 * TODO: Documentation
	 */
	def Repository getRepositoryByImage(VirtualMachineImage image){
		if(image==null)return null
		Repository repo = Repository.where{
			images{image}
		}.find([max:1])
		return repo
	}
	
	def changeImageState(Long id, VirtualMachineImageEnum state){
		VirtualMachineImage.withTransaction{
			VirtualMachineImage vm2 = getImage(id);
			if(vm2!=null){
				vm2.setState(state);
				vm2.save();
			}
		}
	}
	
	def deleteImage(Long id){
		try {
			VirtualMachineImage.withTransaction{
				VirtualMachineImage vm = getImage(id);
				User user = (User) getUserByImage(vm);
				Repository repo = (Repository) getRepositoryByImage(vm);
				deleteImage(user, repo, vm);
			}			
		} catch (Exception e) {
			e.printStackTrace()
		}
	}
	
	def Long getByToken(token){
		if(token==null||token.equals(''))return null
		def vm= VirtualMachineImage.findByToken(token)
		if(vm!=null)return vm.id
		else null
	}

	def setPath(String token,String newPath){
		
		def id = getByToken(token);
		if(id!=null){
			VirtualMachineImage.withTransaction{
				def image = getImage(id)
				image.setMainFile(newPath);
				image.setToken(null);
				image.save(failOnError: true);
			}
		}
	}
	def boolean imageExist(Long vmId) {
		return getImage(vmId)!=null;
	}
		
	def String getPathImage(Long vmId) {
		VirtualMachineImage vm = getImage(vmId);
		if(vm)return vm.getMainFile();
		return null;
	}
}
