package uniandes.unacloud.file.db.entities;

import uniandes.unacloud.common.utils.FileConverter;
import uniandes.unacloud.share.db.entities.PlatformEntity;
import uniandes.unacloud.share.db.entities.RepositoryEntity;
import uniandes.unacloud.share.db.entities.ImageEntity;
import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Class to represent an Image in database.
 * This class manages additional information about file repository. 
 * Used to manage file 
 * @author CesarF
 *
 */
public class ImageFileEntity extends ImageEntity {
	
	private RepositoryEntity repository;
	
	private Boolean isPublic;
	
	private Long fixDisk;
	
	private String mainFile;
	
	private String name;
	
	private UserEntity owner;
	
	private String configurer;
	
	private PlatformEntity platform;
		
	public ImageFileEntity(Long id, ImageEnum state, String token, RepositoryEntity repository, PlatformEntity platform, Boolean isPublic, Long disk, String mainFile, String name, String configurer) {
		
		super(id, null, null, state, token);
		this.repository = repository;
		this.isPublic = isPublic;
		this.fixDisk = disk;
		this.mainFile = mainFile;
		this.name = name;
		this.configurer = configurer;
		this.platform = platform;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public RepositoryEntity getRepository() {
		return repository;
	}

	public void setRepository(RepositoryEntity repository) {
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
	
	public UserEntity getOwner() {
		return owner;
	}
	
	public void setOwner(UserEntity owner) {
		this.owner = owner;
	}
	
	public String getConfigurer() {
		return configurer;
	}
	
	public void setConfigurer(String configurer) {
		this.configurer = configurer;
	}

	@Override
	public String toString() {
		return "ImageFileEntity [repository=" + repository
				+ ", isPublic=" + isPublic + ", fixDisk=" + fixDisk
				+ ", mainFile=" + mainFile + ", name=" + name + ", owner="
				+ owner + ", configurer=" + configurer + ", " +getId()+"  - "+getState()+ "]";
	}
	
	public PlatformEntity getPlatform() {
		return platform;
	}
	
	public FileConverter getFileConversor() {
		return new FileConverter(mainFile);
	}
}
