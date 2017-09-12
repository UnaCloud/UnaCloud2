package uniandes.unacloud.common.net.tcp.message.exe;

import java.io.Serializable;

/**
 * Component in interface start message
 * Specifies net interface in an execution
 * @author CesarF
 *
 */
public class ImageNetInterfaceComponent implements Serializable {

	private static final long serialVersionUID = 7448905244463622715L;

	private String name;
	
	private String ip;
	
	private String netMask;
	
	public ImageNetInterfaceComponent(String ip, String netMask, String name) {
		super();
		this.ip = ip;
		this.netMask = netMask;
		this.name = name;
	}
	
	public String getIp() {
		return ip;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNetMask() {
		return netMask;
	}
		
}
