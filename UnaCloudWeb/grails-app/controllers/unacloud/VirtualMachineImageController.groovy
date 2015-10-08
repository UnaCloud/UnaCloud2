package unacloud

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
		session.user.refresh()
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
		[images: session.user.getOrderedImages()]
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
	 * Create a copy from a public image. Redirects to index when finished
	 */
	def copyPublic(){
		def resp
		def user = User.get(session.user.id)
		if(params.name&&!params.name.isEmpty()&&params.pImage){
			try {
				print params.pImage
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
		println 'inside upload'
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
						print fileName;
						if(!(fileName.endsWith("vmx")|| fileName.endsWith("vmdk")||fileName.endsWith("vbox")|| fileName.endsWith("vdi"))){
							resp = [success:false,'message':'Invalid file type.']
							validate= false;
						}
					}
				}
				if(validate){
					try{
						def createPublic = virtualMachineImageService.uploadImage(files, params.name, (params.isPublic!=null), params.protocol, params.osId, params.user, params.passwd,user)
						print 'Image upload'
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
	
	
}
