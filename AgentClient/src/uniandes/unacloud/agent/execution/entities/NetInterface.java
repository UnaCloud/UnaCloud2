package uniandes.unacloud.agent.execution.entities;

/**
 * Class to represent a net interface in virtual machine
 * @author CesarF
 *
 */
public class NetInterface {
	private String name;
	private String ip;
	private String netMask;	
	
	public NetInterface(String name, String ip, String mask) {
		super();
		this.name = name;
		this.ip = ip;
		this.netMask = mask;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
}
