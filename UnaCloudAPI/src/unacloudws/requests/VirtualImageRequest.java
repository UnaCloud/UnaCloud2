package unacloudws.requests;

public class VirtualImageRequest {
	int instances,imageId;
	String hostname,hardwareProfile;
	public VirtualImageRequest(int instances, String hardwareprofilename, int imageId, String hostname) {
		this.instances = instances;
		this.imageId = imageId;
		this.hardwareProfile=hardwareprofilename;
		this.hostname = hostname;
	}
	public int getInstances() {
		return instances;
	}
	public String getHardwareProfile() {
		return hardwareProfile;
	}
	public int getImageId() {
		return imageId;
	}
	public String getHostname() {
		return hostname;
	}
}
