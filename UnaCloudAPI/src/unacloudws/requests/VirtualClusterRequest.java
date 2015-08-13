package unacloudws.requests;

import java.util.Arrays;

public class VirtualClusterRequest {

	long clusterId;
	long time;
	VirtualImageRequest[]vms;
	/**
	 * 
	 * @param clusterId Id of the cluster to be deployed
	 * @param time Deployment time on minutes
	 * @param vms List of virtual machine configurations for this cluster
	 */
	public VirtualClusterRequest(long clusterId, long time,VirtualImageRequest...vms) {
		this.clusterId = clusterId;
		this.time = time;
		this.vms = vms;
	}
	public long getClusterId() {
		return clusterId;
	}
	public long getTime() {
		return time;
	}
	public VirtualImageRequest[] getVms() {
		return vms;
	}
	@Override
	public String toString() {
		return "VirtualClusterRequest [clusterId=" + clusterId + ", time=" + time + ", vms=" + Arrays.toString(vms) + "]";
	}
	
}
