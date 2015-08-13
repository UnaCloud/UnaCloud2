package unacloudws.responses;

public class DeploymentWS {
	Long id;
	ClusterWS cluster;
	public DeploymentWS(Long id, ClusterWS cluster) {
		this.id = id;
		this.cluster = cluster;
	}
	public Long getId() {
		return id;
	}
	public ClusterWS getCluster() {
		return cluster;
	}
	@Override
	public String toString() {
		return "DeploymentWS [id=" + id + ", cluster=" + cluster + "]";
	}
	
}
