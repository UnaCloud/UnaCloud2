package uniandes.unacloud.share.db.entities;

/**
 * Class to represent a NetInterface entity 
 * @author CesarF
 *
 */
public class NetInterfaceEntity {
	
	private Long id;
	
	private String ip;
	
	private String netMask;
	
	private String name;	
	
	public NetInterfaceEntity(Long id, String name, String ip, String netMask) {
		this.id = id;
		this.ip = ip;
		this.netMask = netMask;
		this.name = name;
	}
	
	public NetInterfaceEntity() {

	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getNetMask() {
		return netMask;
	}
	
	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
