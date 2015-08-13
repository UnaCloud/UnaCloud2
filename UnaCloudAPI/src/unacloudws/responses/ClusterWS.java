package unacloudws.responses;

import java.util.List;

public class ClusterWS {
	long id;
	String name;
	List<ImageWS> images;
	public ClusterWS(long id, String name, List<ImageWS> images) {
		this.id = id;
		this.name = name;
		this.images = images;
	}
	
	public ClusterWS(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ImageWS> getImages() {
		return images;
	}

	public void setImages(List<ImageWS> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "ClusterWS [id=" + id + ", name=" + name + ", images=" + images + "]";
	}
}
