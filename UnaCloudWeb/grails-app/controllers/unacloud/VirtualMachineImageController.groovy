package unacloud

import unacloud.enums.VirtualMachineImageEnum;
import unacloud.task.queue.QueueTaskerControl;
import grails.converters.JSON

class VirtualMachineImageController {

    //-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of image services
	 */
	
	VirtualMachineImageService virtualMachineImageService
	
	
	//-----------------------------------------------------------------
	// Actions MVC
	//-----------------------------------------------------------------
	
	/**
	 * Makes session verifications before executing any action
	 */
	
	def beforeInterceptor = {
		if(!session.user){			
			flash.message="You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}
		def user = User.get(session.user.id)
		session.user.refresh(user)
	}
	/**
	 * Action by default
	 * 
	 */
	def index(){
		redirect(uri:"/services/image/list", absolute:true)
	}
	/**
	 * Virtual machine image index action
	 * @return list of all images related to user
	 */
	def list() {	
		def user = User.get(session.user.id)
		[images: user.getOrderedImages()]
	}
	/**
	 * New uploaded image form action
	 * @return list of OS for user selection
	 */
	def newUploadImage(){
		[oss: OperatingSystem.list()]
	}
	/**
	 * New Image from public one action
	 */
	def newFromPublic(){
		[ publicImages: virtualMachineImageService.getAvailablePublicImages()]
	}
	
	/**
	 * Creates a copy from a public image. Redirects to index when finished
	 */
	def copyPublic(){
		def resp
		def user = User.get(session.user.id)
		if(params.name&&!params.name.isEmpty()&&params.pImage){
			try {
				if(virtualMachineImageService.newPublic(params.name, params.pImage, user)){
					redirect(uri:"/services/image/list", absolute:true)
				}
				else{
					flash.message="Values are not correct"
					redirect(uri:"/services/image/public", absolute:true)
				}
			} catch (Exception e) {
				e.printStackTrace()
				flash.message="Error: "+e.message
				redirect(uri:"/services/image/public", absolute:true)
			}
		}else{
			flash.message="All fields are mandatory"
			redirect(uri:"/services/image/public", absolute:true)
		}
	}
	
	/**
	 * Verifies if the image is being used, then acquires the repository when it is
	 * deposited and send deletion request. Redirects to index when finished or to
	 * and error message if the validation processes failed
	 */	
	def delete(){
		def image = VirtualMachineImage.get(params.id)
		if (!image||!image.state==VirtualMachineImageEnum.AVAILABLE)
			redirect(uri:"/services/image/list", absolute:true)
		else {	
			def user= User.get(session.user.id)
			if(virtualMachineImageService.deleteImage(user,image)){
				flash.message="Your image has been disabled, it will be deleted in a few time";
				flash.type="info";
			}else{
				flash.message="The image can not be deleted in this moment";
			}	
			redirect(uri:"/services/image/list", absolute:true)		
		}
	}
	
	/**
	 * Requests delete image files from every ACTIVE physical machine
	 * Verifies if image is AVAILABLE
	 */	
	def clearFromCache(){
		VirtualMachineImage image = VirtualMachineImage.get(params.id);
		if (image&&image.state==VirtualMachineImageEnum.AVAILABLE){	
			if(image.owner.id==session.user.id){
				virtualMachineImageService.clearCache(image);				
				image.freeze()
				flash.message="Your request has been sent, image will be deleted from physical machines in a few time";
				flash.type="info";
			}
		}
		redirect(uri:"/services/image/list", absolute:true)
	}
	/**
	 * Validates if image can be edited by user and render view
	 * @return
	 */
	def edit(){
		VirtualMachineImage image = VirtualMachineImage.get(params.id);
		if (image&&image.state==VirtualMachineImageEnum.AVAILABLE){
			if(image.owner.id==session.user.id){	
				[image: image]
			}else{
				flash.message="You are not authorized to edit that image";
				redirect(uri:"/services/image/list", absolute:true)
			}
		}else
			redirect(uri:"/services/image/list", absolute:true)
	}
	/**
	 * Save image information changes. Redirect6s to index when finished
	 */
	
	def saveEdit(){
		def image = VirtualMachineImage.get(params.id)
		if (image&&image.state==VirtualMachineImageEnum.AVAILABLE){
			if(image.owner.id==session.user.id){
				boolean toPublic = params.isPublic!=null;
				def res = null;
				virtualMachineImageService.setValues(image,params.name,params.user,(params.password?params.password:image.password))
				if(image.isPublic!=toPublic){				
					virtualMachineImageService.alterImagePrivacy(toPublic,image)
					flash.message="Image files will be change its privacy, this will be take a few minutes";
				}else flash.message="Your changes has been saved";				
			    flash.type="success";
				redirect(uri:"/services/image/list/", absolute:true)
			}else{
				flash.message="You are not authorized to edit this image";
				redirect(uri:"/services/image/list", absolute:true)
			}		
		}else{ 		
			flash.message="There was an error, check logs server";
			redirect(uri:"/services/image/list", absolute:true)
		}
	}
	
	
	/**
	 * Change image files form action.
	 * @return id of the image to be edited.
	 */	
	def update(){
		[id:params.id]
	}
	
	/**
	 * This action renders view to change the externalId reference in external cloud provider
	 * @return
	 */
	def external(){
		//TODO to be implemented
	}
	
	//-----------------------------------------------------------------
	// Actions JSON
	//-----------------------------------------------------------------
	
	/**
	 * Validates file parameters are correct and save new uploaded image. Redirects
	 * to index when finished or renders an error message if uploaded
	 * files are not valid.
	 *
	 */
	def upload(){
		def resp
		if( params.name&&!params.name.empty&&params.protocol&&!params.protocol.empty&&
			params.user&&!params.user.empty&&params.passwd&&!params.passwd.empty&&params.osId){
			if(request.multiFileMap&&request.multiFileMap.files&&request.multiFileMap.files.size()>0){
				
				def files = request.multiFileMap.files
				def user= User.get(session.user.id)
				boolean validate=true
				files.each {
					if(it.isEmpty()){
						resp = [success:false,'message':'File cannot be empty.'];
						validate= false;
					}
					else{
						def fileName=it.getOriginalFilename()
						if(!(fileName.endsWith("vmx")|| fileName.endsWith("vmdk")||fileName.endsWith("vbox")|| fileName.endsWith("vdi"))){
							resp = [success:false,'message':'Invalid file type.']
							validate= false;
						}
					}
				}
				if(validate){
					try{
						def createPublic = virtualMachineImageService.
							uploadImage(files, params.name, (params.isPublic!=null), params.protocol, params.osId, params.user, params.passwd,user)
						if(createPublic!=null){
							resp = [success:true,'redirect':'list','cPublic':createPublic];
						}else resp = [success:true,'redirect':'list'];
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
	 * Validates file parameters are correct and save new files for the image.
	 * Redirects to index when finished or renders an error message if uploaded
	 * files are not valid.
	 */
	
	def updateFiles(){
		def resp
		VirtualMachineImage image= VirtualMachineImage.get(params.id)
		if (image!= null&&image.owner.id==session.user.id){
			if(request.multiFileMap&&request.multiFileMap.files&&request.multiFileMap.files.size()>0){
				def files = request.multiFileMap.files
				boolean validate=true
				files.each {
					if(it.isEmpty()){
						resp =[success:false,'message':'File cannot be empty'];
						validate = false;
					}
					else{
						def fileName=it.getOriginalFilename()
						if(!(fileName.endsWith("vmx")|| fileName.endsWith("vmdk")
							||fileName.endsWith("vbox")|| fileName.endsWith("vdi"))){
							resp = [success:false,'message':'Invalid file type.']
							validate= false;
						}
					}
				}
				if(validate){
					virtualMachineImageService.updateFiles(image,files,image.owner)
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
