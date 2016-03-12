package unacloud

import java.io.File;
import java.sql.Connection

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import com.losandes.utils.Constants;
import com.losandes.utils.UnaCloudConstants;

import unacloud.share.db.RepositoryManager;
import unacloud.share.db.VirtualImageManager;
import unacloud.share.entities.RepositoryEntity;
import unacloud.share.entities.VirtualMachineImageEntity;
import unacloud.share.enums.VirtualMachineImageEnum;
import uniandes.unacloud.FileManager;
import uniandes.unacloud.db.UserManager;
import uniandes.unacloud.db.VirtualMachineImageManager;
import uniandes.unacloud.db.entities.UserEntity
import uniandes.unacloud.db.entities.VirtualImageFileEntity;
import grails.transaction.Transactional

/**
 * This service contains all methods to manage files: saves files for a new image or update files for a current image.
 * This class not use hibernate connection to database is using UnaCloud pool database connection library
 * @author CesarF
 *
 */
@Transactional
class FileService implements ApplicationContextAware { 
     ApplicationContext applicationContext 

	/**
	 * Saves files in repository
	 * All files must be valid (extension file)
	 * @param files
	 * @param token
	 * @return boolean, true if image was copy to file repository or not.
	 */
    def upload(files, String token, String mainExtension){
		def copy = null;
		try{
			Connection con = FileManager.getInstance().getDBConnection();
			def image = VirtualMachineImageManager.getVirtualImageWithFile(token,con)
			if(image){				
				RepositoryEntity main = RepositoryManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con);
				if(image.isPublic()){
					File file = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName());
					if (file.exists()){
						image.setPublic(false)
						copy = false;
					}else copy = true
				}
				Long sizeImage = 0;
				UserEntity user = UserManager.getUser(image.getOwner().getId(), con)
				files.each {
					def fileName=it.getOriginalFilename()
					File file= new File(image.getRepository().getRoot()+image.getName()+"_"+user.getUsername()+File.separator+fileName+File.separator)
					file.mkdirs()
					it.transferTo(file)
					if(image.isPublic()){
						File newFile = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator+fileName);
						FileUtils.copyFile(file, newFile);
					}
					if (fileName.matches(".*"+mainExtension)){
						image.setMainFile(image.getRepository().getRoot()+image.getName()+"_"+user.getUsername()+File.separator+fileName)
					}
					sizeImage += it.getSize()
				}
				
				VirtualMachineImageManager.setVirtualMachineFile(new VirtualImageFileEntity(image.getId(), VirtualMachineImageEnum.AVAILABLE, null, null, image.isPublic(), sizeImage, image.getMainFile(), null, null),false, con)
	
			}
			con.close();
		}catch(Exception e){
			e.printStackTrace()
		}		
		return copy;
	}
	
	/**
	 * Updates image files in repository
	 * @param files list of files
	 * @param token to query image
	 */
	
	def updateFiles(files, String token, String mainExtension){
		try{
			Connection con = FileManager.getInstance().getDBConnection();
			def image = VirtualMachineImageManager.getVirtualImageWithFile(token, con)
			if(image){
				println 'Main file: '+image.getMainFile()
				if(image.getMainFile()!=null)new java.io.File(image.getMainFile()).getParentFile().deleteDir()
				RepositoryEntity main = RepositoryManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con);
				def sizeImage = 0;
				UserEntity user = UserManager.getUser(image.getOwner().getId(), con)
				files.each {
					def filename=it.getOriginalFilename()
					File file= new File(image.getRepository().getRoot()+image.getName()+"_"+user.getUsername()+File.separator+filename+File.separator)
					file.mkdirs()
					it.transferTo(file)
					if(image.isPublic()){
						File newFile = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator+filename);
						FileUtils.copyFile(file, newFile);
					}
					if (filename.matches(".*"+mainExtension)){
						image.setMainFile(image.getRepository().getRoot()+image.getName()+"_"+user.getUsername()+File.separator+filename)
					}
					sizeImage += it.getSize()
				}
				println 'Update: '+VirtualMachineImageManager.setVirtualMachineFile(new VirtualImageFileEntity(image.getId(), VirtualMachineImageEnum.AVAILABLE, null, null, image.isPublic(), sizeImage, image.getMainFile(), null, null),true, con)
				
			}
			con.close();
		}catch(Exception e){
			e.printStackTrace()
		}			
	}
	
	/**
	 * This method is used to set system property with the current path for project.
	 * The purpose of this method is to set variable base in groovy environment to be used in java classes	
	 */
	def updateProperty(){
		System.setProperty(UnaCloudConstants.ROOT_PATH, applicationContext.getResource("/").getFile().getAbsolutePath())
	}
}
