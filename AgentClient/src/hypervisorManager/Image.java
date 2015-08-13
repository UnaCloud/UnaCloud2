package hypervisorManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Image implements Serializable{
	
	private static final long serialVersionUID = -2386734224180305694L;
	
	/**
	 * image properties
	 * 
	 */
	
	long id;
	String username;
	String password;
	String configuratorClass;
	String hypervisorId;
	
	/**
	 * image copies in disk
	 */
	List<ImageCopy> imageCopies=new ArrayList<>();
	
	/**
	 * getters and setters
	 */
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username = username;
	}
	public String getPassword(){
		return password;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public String getConfiguratorClass() {
		return configuratorClass;
	}
	public String getHypervisorId() {
		return hypervisorId;
	}
	public void setHypervisorId(String hypervisorId) {
		this.hypervisorId = hypervisorId;
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