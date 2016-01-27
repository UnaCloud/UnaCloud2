package uniandes.unacloud.db.entities;

import unacloud.entities.Repository;

/**
 * Class to represent an User from database.
 * @author Cesar
 *
 */
public class User {
	
	private Long id;
	private String username;	
	private Repository repository;
	
	public User(Long id, String username) {
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
	
	public Repository getRepository() {
		return repository;
	}
	
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

}
