package uniandes.unacloud.web.controllers

import uniandes.unacloud.web.services.ClusterService;
import uniandes.unacloud.web.services.LaboratoryService;
import uniandes.unacloud.web.services.UserRestrictionService;
import uniandes.unacloud.web.domain.enums.ClusterEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.web.domain.Cluster;
import uniandes.unacloud.web.domain.HardwareProfile;
import uniandes.unacloud.web.domain.Image
import uniandes.unacloud.web.domain.Platform
import uniandes.unacloud.web.domain.User;

import javassist.bytecode.stackmap.BasicBlock.Catch;

/**
 * This Controller contains actions to manage cluster services: crud and deploy cluster.
 * This class render pages for user or process request in services to update entities, there is session verification before all actions
 * @author CesarF
 *
 */
class ClusterController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	/**
	 * Representation of cluster service
	 */
	ClusterService clusterService
	
	/**
	 * Representation of User Restriction service
	 */
	UserRestrictionService userRestrictionService
	
	/**
	 * Representation of Lab service
	 */
	LaboratoryService laboratoryService
	
	//-----------------------------------------------------------------
	// Actions MVC
	//-----------------------------------------------------------------
	
	/**
	 * Makes session verifications before executing any action
	 */
	
	def beforeInterceptor = {
		if (!session.user) {
			flash.message = "You must log in first"
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
    def index() { 
		redirect(uri:"/services/cluster/list", absolute:true)
	}
	
	/**
	 * Cluster list action
	 * @return list of all clusters owned by user
	 */
	def list() {
		def user = User.get(session.user.id)
		[clusters: user.getOrderedClusters()]
	}
	
	/**
	 * New cluster creation action that sends the available images for the user
	 * @return list of ordered images that user can add to a new cluster
	 */
	def newCluster() {
		def user = User.get(session.user.id)
		[images: user.getAvailableImages()]
	}
	
	/**
	 * Action to create a new cluster. It redirects to index after saving
	 */
	def save() {		
		if (params.name && params.images) {			
			def user = User.get(session.user.id)
			try {
				clusterService.createCluster(params.name, params.images, user)
				flash.message = "Your new cluster has been created"
				flash.type = "success"
				redirect(uri:"/services/cluster/list", absolute:true)
			} catch(Exception e) {
				flash.message = e.message
				redirect(uri:"/services/cluster/new", absolute:true)
			}		
		} else {
			if (!params.name)
				flash.message = "The name for your new cluster is required"
			else if (!params.images)
				flash.message = "At least one image must be selected"						
			redirect(uri:"/services/cluster/new", absolute:true)
		}	
	}
	
	/**
	 * Delete cluster action. Receives the cluster id  and
	 * responses success and redirect to index after deletion
	 * @return
	 */
	def delete() {
		def cluster = Cluster.get(params.id)
		if (cluster) {
			def user = User.get(session.user.id)
			try {
				clusterService.deleteCluster(cluster, user)
				flash.message = "Your cluster has been deleted"
				flash.type = "success"
			} catch(Exception e) {
				flash.message = e.message;
			}			
		}
		redirect(uri:"/services/cluster/list", absolute:true)
	}
	
	/**
	 * Deploy options action that brings the form with deploying options for each
	 * image in the cluster
	 * @return limits shown in the information of form and cluster to be deployed
	 */
	def deployOptions() {
		//get cluster and validates if cluster is Available
		def cluster = Cluster.get(params.id);
		if (cluster && cluster.state == ClusterEnum.AVAILABLE) {
			//Validates if all images are available in the cluster
			def unavailable = cluster.images.findAll {it.state == ImageEnum.AVAILABLE}
			if (unavailable.size() != cluster.images.size()) {
				flash.message = "Some images in cluster are not available at this moment. Please, change cluster or remove images in cluster."
				redirect(uri:"/services/cluster/list", absolute:true)
				return
			}	
			//get current user
			def user = User.get(session.user.id)
			//get user available resources based in restrictions
			def allowedHwdProfiles = userRestrictionService.getAllowedHwdProfiles(user)
			def allowedLabs = userRestrictionService.getAllowedLabs(user)			
			def quantitiesTree = new TreeMap<String, Integer>()
			def quantitiesAvailableTree = new TreeMap<String, Integer>()
			def platforms = new TreeMap<String, String>()
			
			//get all platforms used for images in cluster
			for (Image im : cluster.images)
				if (!platforms.get(im.platform.name))
					platforms.put(im.platform.name, im.platform)
					
		    def resourcesByPlatform = []
			//loop for allowed labs 
		    platforms.values().each{
				Platform plat = it
				allowedLabs.each {
					//Get resources for lab
					def results = laboratoryService.calculateDeploys(it, allowedHwdProfiles, false, plat)
					def resultsAvailable = laboratoryService.calculateDeploys(it, allowedHwdProfiles, true, plat)
					for (HardwareProfile hwd in allowedHwdProfiles) {
						if (!quantitiesTree.get(hwd.name))
							quantitiesTree.put(hwd.name, results.get(hwd.name))
						else 
							quantitiesTree.put(hwd.name, results.get(hwd.name) + quantitiesTree.get(hwd.name))
						if (!quantitiesAvailableTree.get(hwd.name))
							quantitiesAvailableTree.put(hwd.name, resultsAvailable.get(hwd.name))
						else 
							quantitiesAvailableTree.put(hwd.name, resultsAvailable.get(hwd.name) + quantitiesAvailableTree.get(hwd.name))
					}
				}
				def quantities = []
				def quantitiesAvailable = []
				def high = false;
				for (HardwareProfile hwd in allowedHwdProfiles) {
					quantities.add(['name':hwd.name, 'quantity':quantitiesTree.get(hwd.name) ? quantitiesTree.get(hwd.name) : 0])
					if (quantitiesAvailableTree.get(hwd.name) > 0 && !high)
						high = true
					quantitiesAvailable.add(['name':hwd.name, 'quantity':quantitiesAvailableTree.get(hwd.name) ? quantitiesAvailableTree.get(hwd.name) : 0])
				}
				def res = [name:plat.name,quantities:quantities, quantitiesAvailable:quantitiesAvailable, images:cluster.images.findAll{it.platform.id == plat.id}, high:high]
				resourcesByPlatform.add(res)
			}
			[resources:resourcesByPlatform,hardwareProfiles: HardwareProfile.list(), clusterid:cluster.id]
		} else
			redirect(uri:"/services/cluster/list", absolute:true)
		 		
	}
	
}
