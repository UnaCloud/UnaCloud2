package unacloud

import unacloud.Cluster;

class DeployedCluster {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * representation of the cluster 
	 */
	Cluster cluster
	
	/**
	 * representation of Deployment 
	 */
	Deployment deployment
	
	static belongsTo = [deployment:Deployment]
	
	/**
	 * list of deployed images present in the deployment
	 */
	static hasMany = [images: DeployedImage]
	
    static constraints = {
    }
	
	static mapping = {
		sort 'cluster_id':'desc'
	}
}
