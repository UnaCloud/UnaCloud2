package unacloudws.responses;

public class ImageWS {
	
	long id;
	String name;

	public ImageWS(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
