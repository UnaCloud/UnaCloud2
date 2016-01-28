package uniandes.unacloud.db.entities;

import unacloud.entities.RepositoryEntity;


/**
 * Class to represent an User from database.
 * @author Cesar
 *
 */
public class UserEntity {
	
	private Long id;
	private String username;	
	private RepositoryEntity repository;
	
	public UserEntity(Long id, String username) {
		super();
		this.id = id;
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}	
	
	public RepositoryEntity getRepository() {
		return repository;
	}
	
	public void setRepository(RepositoryEntity repository) {
		this.repository = repository;
	}

}
