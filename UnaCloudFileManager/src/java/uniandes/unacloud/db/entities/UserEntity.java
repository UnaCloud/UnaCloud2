package uniandes.unacloud.db.entities;

import unacloud.share.entities.RepositoryEntity;
import unacloud.share.enums.UserStateEnum;


/**
 * Class to represent an User from database.
 * @author Cesar
 *
 */
public class UserEntity {
	
	private Long id;
	private String username;	
	private RepositoryEntity repository;
	private UserStateEnum state;
	
	public UserEntity(Long id, String username, UserStateEnum state) {
		super();
		this.id = id;
		this.username = username;
		this.state = state;
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
	
	public UserStateEnum getState() {
		return state;
	}
	
	public void setState(UserStateEnum state) {
		this.state = state;
	}

}
