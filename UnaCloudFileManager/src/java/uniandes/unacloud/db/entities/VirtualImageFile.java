package uniandes.unacloud.db.entities;

import unacloud.entities.Repository;
import unacloud.entities.VirtualMachineImage;
import unacloud.enums.VirtualMachineImageEnum;

/**
 * Class to represent a Virtual Image in database.
 * This class manages additional information about file repository
 * Used to manage file 
 * @author Cesar
 *
 */
public class VirtualImageFile extends VirtualMachineImage{
	
	private Repository repository;
	private Boolean isPublic;
	private Long fixDisk;
	private String mainFile;
	private String name;
	private User owner;
	
	public VirtualImageFile(Long id, 
			VirtualMachineImageEnum state, String token, Repository repository, Boolean isPublic, Long disk, String mainFile, String name) {
		super(id, null, null, state, token);
		this.repository = repository;
		this.isPublic = isPublic;
		this.fixDisk = disk;
		this.mainFile = mainFile;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public Boolean isPublic() {
		return isPublic;
	}

	public void setPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Long getFixDisk() {
		return fixDisk;
	}

	public void setFixDisk(Long fixDisk) {
		this.fixDisk = fixDisk;
	}

	public String getMainFile() {
		return mainFile;
	}

	public void setMainFile(String mainFile) {
		this.mainFile = mainFile;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
}
