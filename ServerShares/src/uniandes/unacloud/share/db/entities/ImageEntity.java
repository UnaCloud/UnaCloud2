package uniandes.unacloud.share.db.entities;

import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Class to represent a Image entity 
 * @author CesarF
 *
 */
public class ImageEntity {
	
	private Long id;
	
	private String user;
	
	private String password;
	
	private ImageEnum state;
	
	private String token;
	
	public ImageEntity(Long id, String user, String password,
			ImageEnum state, String token) {
		super();
		this.id = id;
		this.user = user;
		this.password = password;
		this.state = state;
		this.token = token;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ImageEnum getState() {
		return state;
	}

	public void setState(ImageEnum state) {
		this.state = state;
	}	
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "ImageEntity [id=" + id + ", user=" + user
				+ ", password=" + password + ", state=" + state + ", token="
				+ token + "]";
	}
	
}
