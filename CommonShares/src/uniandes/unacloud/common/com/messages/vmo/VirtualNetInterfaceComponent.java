package uniandes.unacloud.common.com.messages.vmo;

import java.io.Serializable;

/**
 * Component in virtual interface start message
 * Specifies net interface in a virtual machine
 * @author CesarF
 *
 */
public class VirtualNetInterfaceComponent implements Serializable{

	private static final long serialVersionUID = 7448905244463622715L;

	public String name;
	public String ip;
	public String netMask;
	public VirtualNetInterfaceComponent(String ip, String netMask, String name) {
		super();
		this.ip = ip;
		this.netMask = netMask;
		this.name = name;
	}
	
	
}
