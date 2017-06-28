package uniandes.unacloud.share.db.entities;


/**
 * Class to represent a Platform to execute images
 * @author CesarF
 *
 */
public class PlatformEntity {
	
	private Long id;
	
	private String version;
	
	private String name;
	
	private String extension;
	
	private String otherExtensions;
	
	private String configurer;
		
	public PlatformEntity(Long id, String version, String name, String extension, String otherExt, String configurer) {
		super();
		this.id = id;
		this.version = version;
		this.name = name;
		if (extension.contains("."))
			extension = extension.replace(".", "");
		this.extension = extension;
		this.otherExtensions = otherExt;
		this.configurer = configurer;
	}


	public String getConfigurer() {
		return configurer;
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
	 * Validates if a extension matches with extension list from platform
	 * @param filename name of file
	 * @return true if extension is valid, false in case not
	 */
	public boolean validatesExtension(String filename) {
		try {
			if (filename.matches(".*." + this.extension))
				return true;
			String regex = "";
			int index = 0;
			String[] extensions = otherExtensions.split(",");
			for (String ext: extensions) {
				ext = ext.replace(".", "");
				regex += ".*." + ext + (index < extensions.length-1 ? "|" : "");	
				index++;
			}
			if (filename.matches(regex))
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}		
	}

}
