package uniandes.unacloud.web.controllers

import java.util.TreeMap;

import uniandes.unacloud.web.pmallocators.AllocatorEnum;
import uniandes.unacloud.web.services.UserService;
import uniandes.unacloud.share.enums.UserRestrictionEnum;
import uniandes.unacloud.share.enums.UserStateEnum;
import uniandes.unacloud.share.enums.VirtualMachineImageEnum;
import uniandes.unacloud.web.domain.Cluster;
import uniandes.unacloud.web.domain.Deployment;
import uniandes.unacloud.web.domain.Hypervisor;
import uniandes.unacloud.web.domain.OperatingSystem;
import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Repository;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.domain.UserGroup;
import uniandes.unacloud.web.domain.VirtualMachineImage;
import uniandes.unacloud.web.utils.groovy.UserSession;

/**
 * This Controller contains actions to manage User services: home, login and logout, profile and change password.
 * This class renders pages or processes request in services to update entities, there is session verification before all actions except login, logout and index.
 * @author CesarF
 *
 */
class UserController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of user services
	 */
	
	UserService userService		
	
	
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
			}
		}, except: [
			'login',
			'logout',
			'index'
		]]
	
	/**
	 * Initial action. Selects redirection depending on session status
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
	 * Renders page with current user session attributes
	 */
	def profile(){
		def user = User.get(session.user.id)
		[user: user]
	}
	
	/**
	 * Sets changed values in profile
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
	 * Renders page to change password
	 */
	def changePassword(){		
	}
	
	/**
	 * Validates and saves new password
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