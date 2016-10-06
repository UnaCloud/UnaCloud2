package uniandes.unacloud.web.services

import uniandes.unacloud.web.domain.enums.ClusterEnum;
import uniandes.unacloud.web.domain.Cluster;
import uniandes.unacloud.web.domain.Deployment;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.domain.Image;

import grails.transaction.Transactional

/**
 * This service contains all methods to manage cluster: create and delete cluster.
 * This class connects with database using hibernate 
 * @author CesarF
 *
 */
@Transactional
class ClusterService {

    /**
	 * Creates a new cluster with the given parameters
	 * It validates that images owner is the same user that requires cluster
	 * @param images images belonging to new cluster
	 * @param cluster empty new cluster
	 * @param user cluster owner
	 */
	
    def createCluster(clusterName, images, User user) {
		Cluster cluster = new Cluster(name: clusterName, user:user);		
		cluster.images=[]
		if(images.getClass().equals(String)){
			Image image = Image.get(images);
			if(image.owner.id == user.id)
				cluster.images.add(Image.get(images))
			else throw new Exception("Forbidden access to image")
		}
		else{
			for(image in images){
				Image vImage = Image.get(images);
				if(vImage.owner.id == user.id)
					cluster.images.add(Image.get(image))
				else throw new Exception("Forbidden access to image")
			}
		}
		cluster.save(failOnError: true)		
    }
	
	/**
	 * Deletes the cluster given as parameter
	 * @param cluster cluster to be deleted
	 * @param user cluster owner
	 */
	def deleteCluster(Cluster cluster, User user){
		if (cluster.isDeployed())throw new Exception("The cluster is currently deployed")
		else if(!cluster.user.id.equals(user.id))throw new Exception("Forbidden action: you can not delete this cluster")
		Deployment.executeUpdate("update Deployment dc set dc.cluster=null where dc.cluster.id= :id",[id:cluster.id]);	
		cluster.delete()
	}
}
