package uniandes.unacloud.agent.execution.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an image entity
 * @author clouder
 *
 */
public class Image implements Serializable {
	
	private static final long serialVersionUID = -2386734224180305694L;
	
	/**
	 * Database ID
	 */
	private long id;
	
	private String username;
	
	private String password;
	
	private String configuratorClass;
	
	private String platformId;
	
	/**
	 * image copies in disk
	 */
	List<ImageCopy> imageCopies = new ArrayList<>();
	
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getConfiguratorClass() {
		return configuratorClass;
	}
	
	public String getPlatformId() {
		return platformId;
	}
	
	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}
	
	public void setConfiguratorClass(String configuratorClass) {
		this.configuratorClass = configuratorClass;
	}
	
	public List<ImageCopy> getImageCopies() {
		return imageCopies;
	}
	
	public void setImageCopies(List<ImageCopy> imageCopies) {
		this.imageCopies = imageCopies;
	}
}