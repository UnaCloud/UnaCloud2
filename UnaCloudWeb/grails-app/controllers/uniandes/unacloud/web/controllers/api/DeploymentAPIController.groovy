package uniandes.unacloud.web.controllers.api

import grails.converters.JSON
import javassist.bytecode.stackmap.BasicBlock.Catch;

import org.codehaus.groovy.grails.web.json.JSONObject;

import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.web.domain.Cluster
import uniandes.unacloud.web.domain.HardwareProfile
import uniandes.unacloud.web.domain.enums.ClusterEnum;
import uniandes.unacloud.web.exception.NotFoundException
import uniandes.unacloud.web.exception.PreconditionException
import uniandes.unacloud.web.exception.UnathorizedException
import uniandes.unacloud.web.services.DeploymentService;
import uniandes.unacloud.web.utils.groovy.ImageRequestOptions

class DeploymentAPIController extends AbstractController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of cluster service
	 */
	DeploymentService deploymentService
	
    def create() {
		def data = flash.data
		Cluster cluster = Cluster.get(data.cluster.id)
		if (cluster) {
			def user = flash.user
			
			//validates if user is owner to deploy cluster
			if (user.userClusters.find {it.id == cluster.id} != null ) {
				if(cluster.state.equals(ClusterEnum.AVAILABLE)) {
					
					//Validates if images are available in the platform
					def availables = cluster.images.findAll{it.state == ImageEnum.AVAILABLE}
					if (availables.size() != cluster.images.size())
						throw new PreconditionException("Some images of this cluster are not available at this moment.");
					
					//validates if cluster is good configured
					def requests = new ImageRequestOptions[cluster.images.size()];
					cluster.images.eachWithIndex {it, idx->
						ImageRequestOptions requested = null;
						for(node in data.cluster.nodes) 
							if(node.id == it.id){
								HardwareProfile hp = HardwareProfile.findByName(node.hwp);
								requested = new ImageRequestOptions(it, hp, node.quantity, node.gHostName, node.type);
								break;
							}	
						if (requested == null)
							throw new PreconditionException("All images are not in request.");
						requests[idx] = requested;
					}
					deploymentService.deploy(cluster, user, data.time * 60 * 60 * 1000, requests)
					return
				}
				else 
					throw new PreconditionException('Cluster is not available');
			}
			else 
				throw new UnathorizedException('You don\'t have enough privileges to deploy this cluster');			
		}
		else 
			throw new NotFoundException("Cluster not found");
	}
	
	def list() {
		def user = flash.user
		if (!user.isAdmin()) {
			def responseData = ["myDeployments": user.getActiveDeployments()]
			render responseData as JSON		
		}	
		else {
			def deployments = deploymentService.getActiveDeployments(user)
			def responseData = ["myDeployments": user.getActiveDeployments(), "deployments": deployments]
			render responseData as JSON		 
		}	
		
	}
}
