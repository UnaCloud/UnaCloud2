package webutils

import unacloud2.HardwareProfile;

class ImageRequestOptions {

	HardwareProfile hp	int instances 
	long imageId
	String hostname
	
	public ImageRequestOptions(long imageId,HardwareProfile hp, int instances, String hostname) {
		this.imageId = imageId
		this.hp = hp
		this.instances = instances
		this.hostname = hostname
	}
	public ImageRequestOptions() {
		
	}
	@Override
	public String toString() {
		return "ImageRequestOptions [ram=" + hp.ram + ", cores=" + hp.cores
				+ ", instances=" + instances + "]";
	}
	
	
}
