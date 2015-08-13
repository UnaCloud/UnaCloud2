package unacloud2

class DeployedCluster {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * representation of the cluster 
	 */
	Cluster cluster
	
	/**
	 * list of deployed images present in the deployment
	 */
	static hasMany = [images: DeployedImage]
	
    static constraints = {
		cluster nullable: true
    }
	static mapping = {
		sort 'cluster_id':'desc'
	}
}
