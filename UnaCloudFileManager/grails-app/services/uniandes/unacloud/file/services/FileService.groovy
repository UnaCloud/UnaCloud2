package uniandes.unacloud.file.services

import java.io.File;
import java.sql.Connection

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.share.db.ImageManager;
import uniandes.unacloud.share.db.StorageManager;
import uniandes.unacloud.share.entities.RepositoryEntity;
import uniandes.unacloud.share.entities.ImageEntity;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.db.UserManager;
import uniandes.unacloud.file.db.ImageFileManager;
import uniandes.unacloud.file.db.entities.UserEntity
import uniandes.unacloud.file.db.entities.ImageFileEntity;
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
    def upload(files, String token){
		boolean copy = false;
		try{
			Connection con = FileManager.getInstance().getDBConnection();
			def image = ImageFileManager.getImageWithFile(token,con)
			if(image){
				boolean isValid = true;
				files.each {
					def fileName=it.getOriginalFilename().trim()
					if(!image.getPlatform().validatesExtension(fileName)){
						isValid = false;
					}
				}
				if(!isValid)return null										
				RepositoryEntity main = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con);
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
					if (fileName.matches(".*"+image.getPlatform().getExtension())){
						image.setMainFile(image.getRepository().getRoot()+image.getName()+"_"+user.getUsername()+File.separator+fileName)
					}
					sizeImage += it.getSize()
				}				
				ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null, null, null, image.isPublic(), sizeImage, image.getMainFile(), null, null),false, con, true)
	
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
	
	def updateFiles(files, String token){
		try{
			Connection con = FileManager.getInstance().getDBConnection();
			def image = ImageFileManager.getImageWithFile(token, con)
			if(image){
				println 'Main file: '+image.getMainFile()
				files.each {
					def fileName=it.getOriginalFilename()
					if(!image.getPlatform().validatesExtension(fileName)){
						return null
					}
				}
				if(image.getMainFile()!=null)new java.io.File(image.getMainFile()).getParentFile().deleteDir()
				RepositoryEntity main = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con);
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
					if (filename.matches(".*"+image.getPlatform().getExtension())){
						image.setMainFile(image.getRepository().getRoot()+image.getName()+"_"+user.getUsername()+File.separator+filename)
					}
					sizeImage += it.getSize()
				}
				println 'Update: '+ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null, null, null, image.isPublic(), sizeImage, image.getMainFile(), null, null),true, con, true)
				
			}
			con.close();
			return true;
		}catch(Exception e){
			e.printStackTrace()
		}		
		return false;	
	}
	
	/**
	 * This method is used to set system property with the current path for project.
	 * The purpose of this method is to set variable base in groovy environment to be used in java classes	
	 */
	def updateProperty(){
		System.setProperty(UnaCloudConstants.ROOT_PATH, applicationContext.getResource("/").getFile().getAbsolutePath())
	}
}
