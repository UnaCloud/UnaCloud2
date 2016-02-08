package unacloud

import java.util.TreeMap;

import unacloud.pmallocators.AllocatorEnum;
import unacloud.UserService;
import unacloud.share.enums.UserRestrictionEnum;
import unacloud.share.enums.UserStateEnum;
import unacloud.share.enums.VirtualMachineImageEnum;
import webutils.UserSession;

class UserController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of user services
	 */
	
	UserService userService
	
	/**
	 * Representation of group services
	 */
	
	UserGroupService userGroupService
	
	/**
	 * Representation of labs services
	 */
	
	LaboratoryService laboratoryService
	
	/**
	 * Representation of Hardware profiles services
	 */
	
	HardwareProfileService hardwareProfileService
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Makes session verifications before executing user administration actions
	 */	
	def beforeInterceptor = [action:{
			if(!session.user){
				flash.message="You must log in first"
				redirect(uri:"/login", absolute:true)
				return false
			}
			else{
				def user = User.get(session.user.id)
				session.user.refresh(user)
				if(!userGroupService.isAdmin(user)){
					flash.message="You must be administrator to see this content"
					redirect(uri:"/error", absolute:true)
					return false
				}
			}
		}, except: [
			'login',
			'logout',
			'index'
		]]
	
	/**
	 * Init action. Selects redirection depending on session status
	 */
	def index() {
		if(session.user){
			redirect(uri:"/home", absolute:true)
			return true
		}
		else{
			redirect(uri:"/login", absolute:true)
			return true
		}
	}
	
	/**
	 * User list action
	 * @return List of all users
	 */
	def list(){
		[users: User.list().sort{it.id}];
	}
	
	/**
	 * Validates passwords and changes session user. Redirects to home page
	 * if credentials are correct, or to login page if they're not
	 */	
	def login(){
		def user = userService.getUser(params.username,params.password)
		if (user){
			if(user.status==UserStateEnum.AVAILABLE){	
				UserSession userSession = new UserSession(user.id, user.name, user.username, user.description, user.registerDate.toString(),user.isAdmin())			
				session.user = userSession
				flash.message=user.name
				redirect(uri: '/', absolute: true)
			}else{
				flash.message="Disabled user"
				redirect(uri: '/login', absolute: true)
			}
		}
		else {
			flash.message="Wrong username or password"
			redirect(uri: '/login', absolute: true)
		}
	}
	
	/**
	 * Removes the current session user. Redirects to login page
	 * @return
	 */
	def logout(){
		session.invalidate()
		redirect(uri: '/login', absolute: true)
	}
	
	/**
	 * Home action. 
	 */
	
	def home(){
		
		def user = User.get(session.user.id)
		
		TreeMap<String, Integer> treeImages = new TreeMap<String, Integer>();
		if(user.images.size()>0){
			treeImages.put('ALL',user.images.size())
			for(VirtualMachineImage image in user.images){
				if(treeImages.get(image.state.name)==null)treeImages.put(image.state.name,0)
				treeImages.put(image.state.name,treeImages.get(image.state.name)+1)
			}
		}	
		
		TreeMap<String, Integer> treeClusters = new TreeMap<String, Integer>();
		if(user.userClusters.size()>0){
			treeClusters.put('ALL',user.userClusters.size())
			treeClusters.put('DEPLOYED',0)
			for(Cluster cluster in user.userClusters){
				if(treeClusters.get(cluster.state.name)==null)treeClusters.put(cluster.state.name,0)
				treeClusters.put(cluster.state.name,treeClusters.get(cluster.state.name)+1)
				if(cluster.isDeployed())treeClusters.put('DEPLOYED',treeClusters.get('DEPLOYED')+1)
			}
		}
		
		TreeMap<String, Integer> treeDeployments = new TreeMap<String, Integer>();
		if(user.deployments.size()>0){
			treeDeployments.put('ALL',user.deployments.size())
			for(Deployment deployment in user.deployments){
				if(treeDeployments.get(deployment.status.name)==null)treeDeployments.put(deployment.status.name,0)
				treeDeployments.put(deployment.status.name,treeDeployments.get(deployment.status.name)+1)
			}
		}
		
		if(user.isAdmin()){
			def boxes  = []
			boxes.add([name:'Users',quantity:User.count(),color:'aqua',url:'/admin/user/list',icon:'ion-person'])
			boxes.add([name:'Groups',quantity:UserGroup.count(),color:'green',url:'/admin/group/list',icon:'ion-person-stalker'])
			boxes.add([name:'Hypervisors',quantity:Hypervisor.count(),color:'yellow',url:'/admin/hypervisor/list',icon:'ion-star'])
			boxes.add([name:'Operating Systems',quantity:OperatingSystem.count(),color:'blue',url:'/admin/os/list',icon:'ion-load-a'])			
			boxes.add([name:'Hosts',quantity:PhysicalMachine.count(),color:'teal',url:'/admin/lab/list',icon:'ion-monitor'])
			boxes.add([name:'Repositories',quantity:Repository.count(),color:'maroon',url:'/admin/repository/list',icon:'ion-folder'])
			[myImages:treeImages,myClusters:treeClusters,myDeployments:treeDeployments,boxes:boxes]
		}else		
		 	[myImages:treeImages,myClusters:treeClusters,myDeployments:treeDeployments]
		
	}	
	/**
	 * render create view
	 * @return
	 */
	def create(){
		
	}

	/**
	 * Create a new user
	 */
	def save(){
		if(params.name&&params.username&&params.passwd&&params.description){	
			try{
				userService.addUser(params.username, params.name,params.description,params.passwd,params.email)
				redirect(uri:"/admin/user/list", absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/user/new", absolute:true)
			}		
		}else{
			flash.message="All fields are required"					
			redirect(uri:"/admin/user/new", absolute:true)
		}	
	}
	
	/**
	 * Deletes the selected user. Redirects to user list when finished.
	 */	
	def delete(){		
		def user = User.get(params.id)
		if (user&&user.id!=session.user.id&&user.status!=UserStateEnum.BLOCKED) {
			try{
				userService.deleteUser(user)
				flash.message="The request will be processed in a few time"
				flash.type="info"
			}catch(Exception e){
				flash.message=e.message
			}
		}
		redirect(uri:"/admin/user/list", absolute:true)		
	}
	
	/**
	 * User edition form action.
	 * @return selected user
	 */
	
	def edit(){
		def user = User.get(params.id)
		if (user&&user.id!=session.user.id) 
			[user: user]
		else
		    redirect(uri:"/admin/user/list", absolute:true)
	}
	
	/**
	 * Saves user's information changes. Redirects to user list when finished.
	 */
	
	def saveEdit(){
		def user = User.get(params.id)
		if(params.name&&params.username&&params.description){
			if(!params.passwd||(params.passwd&&params.passwd.equals(params.cpasswd))){
				if (user&&user.id!=session.user.id){		
					try{						
						userService.setValues(user,params.username, params.name, params.description, params.password, params.email)
						flash.message="The user has been modified"
						flash.type="success"
					}catch(Exception e){
						flash.message=e.message
					}		
				}
			}else flash.message="Password fields don't match"			
		}else flash.message="All fields are required"		
		redirect(uri:"/admin/user/list", absolute:true)
	}
	
	/**
	 * Render page to edit restrictions
	 * 
	 */
	def config(){
		def user = User.get(params.id)
		if(!user){
			redirect(uri:"/admin/user/list", absolute:true)
		}else{
			[user:user.id,restrictions:[
				  [name:UserRestrictionEnum.ALLOCATOR.name,type:UserRestrictionEnum.ALLOCATOR.toString(),list:true,current:user.getRestriction(UserRestrictionEnum.ALLOCATOR),values:AllocatorEnum.getList(),multiple:false],
				  [name:UserRestrictionEnum.ALLOWED_LABS.name,type:UserRestrictionEnum.ALLOWED_LABS.toString(), list:true,current:user.getRestriction(UserRestrictionEnum.ALLOWED_LABS),values:laboratoryService.getLabsNames(),multiple:true],
				  [name:UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.name,type:UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.toString(),list:true,current:user.getRestriction(UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES),values:hardwareProfileService.getProfilesNames(),multiple:true]
				 // [name:UserRestrictionEnum.MAX_RAM_PER_VM.name,type:UserRestrictionEnum.MAX_RAM_PER_VM.toString(), list:false,current:user.getRestriction(UserRestrictionEnum.MAX_RAM_PER_VM),multiple:false]	
				]
			]
		}
	}
	
	/**
	 * Set restrictions of user
	 * @return
	 */
	def setRestrictions(){
		def user = User.get(params.id)
		if(!user){
			redirect(uri:"/admin/user/list", absolute:true)
		}else{
			def modify = false
			if(params.restriction){
				def value = params.value	
				if(UserRestrictionEnum.getRestriction(params.restriction)==UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES){
					if(value.getClass().equals(String)){
						userService.setRestriction(user,UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.toString(),value)
					}else{
						String list = ""
						for(hwdp in params.value)
							list+=hwdp+(hwdp.equals(params.value[params.value.size()-1])?"":",")
						userService.setRestriction(user,UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.toString(),list)
					}
					modify = true					
				}else if(UserRestrictionEnum.getRestriction(params.restriction)==UserRestrictionEnum.ALLOWED_LABS){
					if(value.getClass().equals(String)){
						userService.setRestriction(user,UserRestrictionEnum.ALLOWED_LABS.toString(),value)
					}else{
						String list = ""
						for(lab in params.value)
							list+=lab+(lab.equals(params.value[params.value.size()-1])?"":",")
						userService.setRestriction(user,UserRestrictionEnum.ALLOWED_LABS.toString(),list)
					}
					modify = true
				}else if(UserRestrictionEnum.getRestriction(params.restriction)==UserRestrictionEnum.ALLOCATOR){
					def allocator = AllocatorEnum.getAllocatorByName(value)
					userService.setRestriction(user,UserRestrictionEnum.ALLOCATOR.toString(),allocator?allocator.getName():null)	
					modify = true
				}		
			}	
			if(modify){
				flash.message="User restrictions have been modified"
				flash.type="success"
			}
			redirect(uri:"/admin/user/restrictions/"+user.id, absolute:true)
		}
	}
	
	/**
	 * Render page with current user session attributes
	 * @return
	 */
	def profile(){
		def user = User.get(session.user.id)
		[user: user]
	}
	
	/**
	 * Set changed values in profile
	 * @return
	 */
	def changeProfile(){
		if(params.name&&params.description){			
			try{
				def user = User.get(session.user.id)
				userService.setValues(user,user.username, params.name, params.description, null, params.email)
				session.user.refresh(user)
				flash.message="Profile values have been modified"
				flash.type="success"
			}catch(Exception e){
				e.printStackTrace()
				flash.message=e.message
			}
		}else flash.message="Email is optional, other values are required"
		redirect(uri:"/user/profile", absolute:true)
	}
	
	/**
	 * Render page to change password
	 * @return
	 */
	def changePassword(){		
	}
	
	/**
	 * Validates and saves new password
	 * @return
	 */
	def savePassword(){
		if(params.passwd&&params.newPasswd&&params.confirmPasswd){
			if(params.confirmPasswd.equals(params.newPasswd)){
				try{
					def user = User.get(session.user.id)
					userService.changePassword(user, params.passwd, params.newPasswd)
					session.user.refresh(user)
					flash.message="Password has been modified"
					flash.type="success"
				}catch(Exception e){
					flash.message=e.message
				}
			}else flash.message="Password fields don't match"
		}else flash.message="All fields are required"
		redirect(uri:"/user/profile/change", absolute:true)
	}
}