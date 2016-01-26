package uniandes.unacloud.db.entities;

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
	private boolean isPublic;
	private Long fixDisk;
	private String mainFile;
	
	public VirtualImageFile(Long id, 
			VirtualMachineImageEnum state, String token, Repository repository, boolean isPublic, Long disk, String mainFile) {
		super(id, null, null, state, token);
		this.repository = repository;
		this.isPublic = isPublic;
		this.fixDisk = disk;
		this.mainFile = mainFile;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
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

}
