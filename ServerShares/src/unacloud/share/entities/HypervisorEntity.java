package unacloud.share.entities;

/**
 * Class to represent an entity from database
 * Represents PhysicalMachine
 * @author Cesar
 *
 */
public class HypervisorEntity {
	
	private Long id;
	private String version;
	private String name;
	private String extension;
	private String otherExtensions;
	
	
	public HypervisorEntity(Long id, String version, String name, String extension, String otherExt) {
		super();
		this.id = id;
		this.version = version;
		this.name = name;
		this.extension = extension;
		this.otherExtensions = otherExt;
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
	
	public String getOtherExtensions() {
		return otherExtensions;
	}
	
	public void setOtherExtensions(String otherExtensions) {
		this.otherExtensions = otherExtensions;
	}
	
	/**
	 * Validates if a extension matches with extension list from hypervisor
	 * @param extension
	 * @return
	 */
	public boolean validatesExtension(String extension){
		try {
			if(extension.matches(".*"+this.extension))return true;
			String regex = "";
			int index = 0;
			String[] extensions = otherExtensions.split(",");
			for(String ext: extensions){
				regex+=".*"+ext+(index<extensions.length-1?"|":"");	
				index++;
			}
			if(extension.matches(regex))return true;
			return false;
		} catch (Exception e) {
			return false;
		}		
	}

}
