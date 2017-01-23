package uniandes.unacloud.common.com.messages.exeo;

import java.io.Serializable;

/**
 * Component in interface start message
 * Specifies net interface in an execution
 * @author CesarF
 *
 */
public class ImageNetInterfaceComponent implements Serializable{

	private static final long serialVersionUID = 7448905244463622715L;

	public String name;
	public String ip;
	public String netMask;
	public ImageNetInterfaceComponent(String ip, String netMask, String name) {
		super();
		this.ip = ip;
		this.netMask = netMask;
		this.name = name;
	}
	
	
}
