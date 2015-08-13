package unacloud2

import org.junit.After;
import back.services.AgentService
import grails.converters.JSON

class VirtualMachineImageController {

	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of image services
	 */
	
	VirtualMachineImageService virtualMachineImageService
	
	/**
	 * Representation of image servicesagent services
	 */
	
	AgentService agentService
	
	//-----------------------------------------------------------------
	// Actions
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
	 * Virtual machine image index action
	 * @return list of all images related to user
	 */
    def index() {
		 
		[images: session.user.getOrderedImages()]
	}
	
	/**
	 * Change image files form action. 
	 * @return id of the image to be edited.
	 */
	
	def changeVersion(){
		[id:params.id]
	}
	
	/**
	 * Deletes the image files from every physical machine. Redirects to index when
	 * finished
	 */
	
	def clearImageFromCache(){
		//TODO improve this function
		def resp;
		try{
			agentService.clearImageFromCache(VirtualMachineImage.get(params.id))
			resp = [success:true]
		}catch(Exception e){
			e.printStackTrace();
			resp = [success:false,'message':e.message]
		}
		render resp as JSON		
	}
	
	/**
	 * Image creation form action
	 */
	
	def newImage(){
		
	}
	
	/**
	 * New uploaded image form action 
	 * @return list of OS for user selection
	 */
	
	def newUploadImage(){
		[oss: OperatingSystem.list()]
	}
	
	/**
	 * Generates the form in order to create a new image based on a public image
	 * @return
	 */
	
	def newPublicImage(){
		[ pImages:VirtualMachineImage.findAllWhere(isPublic: true) ]
	}
	
	/**
	 * Renders image info when a public image is selected in creation form
	 */
	
	def refreshInfo(){
		def pImage= VirtualMachineImage.get(params.selectedValue)
		if(pImage!=null){
			render "<div class=\"control-group\"><label class=\"control-label\">Operating System:</label><div class=\"controls\"><p><small>"+pImage.operatingSystem.name+"</small></p></div></div><div class=\"control-group\"><label class=\"control-label\">User:</label><div class=\"controls\"><p><small>"+pImage.user+"</small></p></div></div><div class=\"control-group\"><label class=\"control-label\">Password:</label><div class=\"controls\"><p><small>"+pImage.password+"</small></p></div></div><div class=\"control-group\"><label class=\"control-label\">Access Protocol:</label><div class=\"controls\"><p><small>"+pImage.accessProtocol+"</small></p></div></div>"
		}
		else
			render ""
	}
	
	/**
	 * Save new public image. Redirects to index when finished
	 */
	
	def newPublic(){
		def resp
		def publicImage = VirtualMachineImage.get(params.pImage)
		if(publicImage){
			def user = User.get(session.user.id)			
			try {
				virtualMachineImageService.newPublic(params.name, publicImage, user)
				resp = [success:true,'redirect':'index'];
			} catch (Exception e) {
				e.printStackTrace()
				resp = [success:false];
			}
		}
		else resp = [success:false];
		render resp as JSON
	}
	
	/**
	 * Validates file parameters are correct and save new uploaded image. Redirects 
	 * to index when finished or renders an error message if uploaded 
	 * files are not valid. 
	 * 
	 * REST
	 */
	
	def upload(){
		println 'inside upload'
		def resp
		if( params.name&&!params.name.empty&&params.accessProtocol&&!params.accessProtocol.empty&&
			params.user&&!params.user.empty&&params.password&&!params.password.empty){			
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
						def e=it.getOriginalFilename()
						print e;
						if(!(e.endsWith("vmx")|| e.endsWith("vmdk")||e.endsWith("vbox")|| e.endsWith("vdi"))){
							resp = [success:false,'message':'Invalid file type.']
							validate= false;
						}
					}
				}
				if(validate){
					try{
						def createPublic = virtualMachineImageService.uploadImage(files, 0, params.name, (params.isPublic!=null), params.accessProtocol, params.osId, params.user, params.password,user)
						if(createPublic!=null){
							resp = [success:true,'redirect':'index','cPublic':createPublic];
						}else resp = [success:true,'redirect':'index'];						
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
		if(request.multiFileMap&&request.multiFileMap.files&&request.multiFileMap.files.size()>0){
			VirtualMachineImage i= VirtualMachineImage.get(params.id)
			def files = request.multiFileMap.files					
			if (i!= null){
				boolean validate=true
				def user= User.get(session.user.id)
				files.each {
				if(it.isEmpty()){
					resp =[success:false,'message':'File cannot be empty'];
					validate = false;
				}
				else{
					def e=it.getOriginalFilename()
						if(!(e.endsWith("vmx")|| e.endsWith("vmdk")||e.endsWith("vbox")|| e.endsWith("vdi"))){
							resp = [success:false,'message':'Invalid file type.']
							validate= false;
						}
					}
				}
				if(validate){
					try{
						virtualMachineImageService.updateFiles(i,files,user)
						resp = [success:true,'redirect':'../index']
					}
					catch(Exception e) {
						resp = [success:false,'message':e.message]
					}
				}				
			}else resp = [success:false,'message':'Error! image does not exist.'];	
		}else resp = [success:false,'message':'File(s) to upload is/are missing.'];		
		print resp	
		render resp as JSON
	}
	
	/**
	 * Edit image information form action. 
	 * @return image id to be edited
	 */
	
	def edit(){
		def i= VirtualMachineImage.get(params.id)		
		if (!i) {
			redirect(action:"index")
		}
		else{
			[image: i]
		}
	}
	
	/**
	 * Save image information changes. Redirect6s to index when finished
	 */
	
	def setValues(){
		def resp;
		def image = VirtualMachineImage.get(params.id)
		if(image){
			boolean toPublic = params.isPublic!=null;
			def res = null;
			if((image.isPublic&&!toPublic)||(!image.isPublic&&toPublic)){
				def user= User.get(session.user.id)
				res = virtualMachineImageService.alterImagePrivacy(toPublic,image,user)
			}
			virtualMachineImageService.setValues(image,params.name,params.user,params.password,(params.isPublic!=null))
			if(res!=null)resp = [success:true,'redirect':'../index','toPublic':res];			
			else resp = [success:true,'redirect':'../index'];
			
		}else resp = [success:false,'message':'Image is not available'];
		render resp as JSON;
	}
	
	def addExternalId(){
		def i= VirtualMachineImage.get(params.id)		
		if (!i) {
			redirect(action:"index")
		}
		else{
			[image: i]
		}
	}
	
	def setExternalId(){
		def resp;
		def image = VirtualMachineImage.get(params.id)
		if(image){
			virtualMachineImageService.setExternalId(image,params.externalId)
			resp = [success:true,'redirect':'../index'];
		}else resp = [success:false,'message':'Image is not available'];	
		render resp as JSON;
	}
	
	/**
	 * Verifies if the image is being used then acquires the repository when it is
	 * deposited and send deletion request. Redirects to index when finished or to 
	 * and error message if the validation processes failed
	 */
	
	def delete(){
		def resp;
		def image = VirtualMachineImage.get(params.id)
		print image
		if (!image) {
			resp = [success:false];
			//redirect(action:"index")
		}
		else {
			def isUsed=false
			for (cluster in Cluster.list()){
				if (cluster.images.contains(image))
					isUsed=true
				
			}
			if (!isUsed){	
				def user= User.get(session.user.id)
				Repository repository
				for(repo in Repository.all) {
					for (repoImage in repo.images){
						if (repoImage.equals(image)){
							repository= repo
							break
						}
					}
					if (repository.equals(repo))
						break
				}
				virtualMachineImageService.deleteImage(user,repository, image);
				resp = [success:true,'redirect':'index'];
			}
			else{
				resp = [success:false,'message':'The image is being used'];
			}
			
		}
		render resp as JSON;
	}
}
