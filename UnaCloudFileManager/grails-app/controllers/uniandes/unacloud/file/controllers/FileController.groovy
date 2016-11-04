package uniandes.unacloud.file.controllers

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import uniandes.unacloud.share.entities.PlatformEntity;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.db.UserManager;
import uniandes.unacloud.file.db.entities.UserEntity;
import uniandes.unacloud.file.services.FileService;
import uniandes.unacloud.share.db.PlatformManager;
import grails.converters.JSON

/**
 * This Controller contains actions to manage upload file process
 * This class process request to upload new images in UnaCloud or update files
 * @author CesarF
 *
 */
class FileController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of file services
	 */
	
	FileService fileService
	
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------

	/**
	 * Service to test if application is running
	 * @return
	 */
	def test(){
		render 'The Cloud is not the limit ;)'
	}
	
   /**
	 * Validates file parameters are correct and save new uploaded image. Redirects
	 * to index when finished or renders an error message if uploaded
	 * files are not valid.
	 *
	 */
	def upload(){
		def resp		
		if(params.token&&!params.token.empty){
			if(request.multiFileMap&&request.multiFileMap.files&&request.multiFileMap.files.size()>0){				
				def files = request.multiFileMap.files
				boolean validate=true
				try{
					files.each {
						if(validate){
							if(it.isEmpty()){
								resp = [success:false,'message':'File cannot be empty.'];
								validate= false;
							}													
						}					
					}
				}catch(Exception e) {
					validate=false;
					resp = [success:false,'message':e.message]
				}
				if(validate){
					try{
						def createPublic = fileService.upload(files, params.token)
						if(createPublic == null){
							resp = [success:false,'message':'Invalid file type.']
						}
						else if(createPublic == true){
							resp = [success:true,'redirect':'list','cPublic':createPublic];
						}else 
							resp = [success:true,'redirect':'list'];
					}
					catch(Exception e) {
						resp = [success:false,'message':e.message]
					}
				}
			}else resp = [success:false,'message':'File(s) to upload is/are missing.'];
		}else resp = [success:false,'message':'All fields are required'];
		render resp as JSON
	}
	
	
	/**
	 * Validates file parameters are correct and save new files for image.
	 * Redirects to index when finished or renders an error message if uploaded
	 * files are not valid.
	 */
	
	def updateFiles(){
		def resp
		if(params.token&&!params.token.empty){
			if(request.multiFileMap&&request.multiFileMap.files&&request.multiFileMap.files.size()>0){
				def files = request.multiFileMap.files
				println 'valid files'
				boolean validate=true
				try{
					files.each {
						if(validate){
							if(it.isEmpty()){
								resp = [success:false,'message':'File cannot be empty.'];
								validate= false;
							}							
						}					
					}
				}catch(Exception e) {
					e.printStackTrace()
					validate=false;
					resp = [success:false,'message':e.message]
				}
				if(validate){
					def update = fileService.updateFiles(files,params.token)
					if(update == null){
						resp = [success:false,'message':'Invalid file type.']
					}else 
						resp = [success:true,'redirect':'../list']
				}
			}else{
				resp = [success:false,'message':'File(s) to upload is/are missing.'];		
			}
		}else{
			resp = [success:false,'message':'Error! image does not exist.'];	
		}
		render resp as JSON
	}
}
