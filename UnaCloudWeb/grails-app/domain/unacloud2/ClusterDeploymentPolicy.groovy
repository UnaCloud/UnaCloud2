package unacloud2

class ClusterDeploymentPolicy {
	
	String schedulingPolicy
	
	String qosDelpoymentPolicy
	
	static belongsTo = [cluster:Cluster]
	
    static constraints = {
    }
}
