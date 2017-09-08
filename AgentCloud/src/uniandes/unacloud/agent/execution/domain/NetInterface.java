package uniandes.unacloud.agent.execution.domain;

import java.io.Serializable;

/**
 * Class to represent a net interface in image execution
 * @author CesarF
 *
 */
public class NetInterface implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -816110117889129324L;

	/**
	 * Net interface name
	 */
	private String name;
	
	/**
	 * Net interface IP
	 */
	private String ip;
	
	/**
	 * Net interface mask
	 */
	private String netMask;	
	
	/**
	 * Class constructor
	 * @param name
	 * @param ip
	 * @param mask
	 */
	public NetInterface(String name, String ip, String mask) {
		super();
		this.name = name;
		this.ip = ip;
		this.netMask = mask;
	}
	
	/**
	 * Gets net interface name
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets net interface name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets net interface IP
	 * @return ip
	 */
	public String getIp() {
		return ip;
	}
	
	/**
	 * Sets net interface IP
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	/**
	 * Gets net interface net mask
	 * @return mask
	 */
	public String getNetMask() {
		return netMask;
	}
	
	/**
	 * Sets net interface mask
	 * @param netMask
	 */
	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}
}
