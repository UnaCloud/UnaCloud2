package unacloud.share.entities;

import unacloud.share.enums.VirtualMachineImageEnum;

/**
 * Class to represent a Virtual Machine Image entity 
 * @author CesarF
 *
 */
public class VirtualMachineImageEntity {
	
	private Long id;
	private String user;
	private String password;
	private VirtualMachineImageEnum state;
	private String token;
	
	public VirtualMachineImageEntity(Long id, String user, String password,
			VirtualMachineImageEnum state, String token) {
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

	public VirtualMachineImageEnum getState() {
		return state;
	}

	public void setState(VirtualMachineImageEnum state) {
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
		return "VirtualMachineImageEntity [id=" + id + ", user=" + user
				+ ", password=" + password + ", state=" + state + ", token="
				+ token + "]";
	}
	
}
