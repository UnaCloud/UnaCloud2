package unacloudws.responses;

import java.util.List;

public class DeployedClusterWS {
	
	int id;
	ClusterWS clusterWS;
	List<DeployedImageWS> images;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ClusterWS getClusterWS() {
		return clusterWS;
	}
	public void setClusterWS(ClusterWS clusterWS) {
		this.clusterWS = clusterWS;
	}
	public List<DeployedImageWS> getImages() {
		return images;
	}
	public void setImages(List<DeployedImageWS> images) {
		this.images = images;
	}
	
}
