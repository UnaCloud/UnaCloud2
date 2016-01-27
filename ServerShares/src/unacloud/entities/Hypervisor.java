package unacloud.entities;

/**
 * Class to represent an entity from database
 * Represents PhysicalMachine
 * @author Cesar
 *
 */
public class Hypervisor {
	
	private Long id;
	private String version;
	private String name;
	private String extension;
	
	
	public Hypervisor(Long id, String version, String name, String extension) {
		super();
		this.id = id;
		this.version = version;
		this.name = name;
		this.extension = extension;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getExtension() {
		return extension;
	}


	public void setExtension(String extension) {
		this.extension = extension;
	}

}
