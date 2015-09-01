package unacloud2

class DeployedCluster {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * representation of the cluster 
	 */
	static belongsTo = [cluster:Cluster]
	
	/**
	 * list of deployed images present in the deployment
	 */
	static hasMany = [images: DeployedImage]
	
	
    static constraints = {
    }
	
	def Cluster getCluster(){
		return cluster;
	}
	
	static mapping = {
		sort 'cluster_id':'desc'
	}
}
