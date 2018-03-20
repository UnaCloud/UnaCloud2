package uniandes.unacloud.agent.host.system;

import uniandes.unacloud.agent.exceptions.UnsupportedOSException;

/**
 * This class is responsible to manage static Operating System object
 * @author CesarF
 *
 */
public class OSFactory {
	
	/**
	 * Instance of Operating System
	 */
	private static OperatingSystem system;
	
	/**
	 * Responsible to create a operating system in case is null and return instance
	 * @return instance of operating system
	 */
	public static OperatingSystem getOS() throws UnsupportedOSException {
		if (system == null) {
			if(OperatingSystem.isWindows())
				system = new WindowsOS();
			else if(OperatingSystem.isUnix())
				system = new LinuxOS();
			else if(OperatingSystem.isMac())
				system = new MacOS();
			else 
				throw new UnsupportedOSException(OperatingSystem.getOperatingSystemName());
		}
		return system;
	}

}
