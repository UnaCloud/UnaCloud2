package uniandes.unacloud.agent.platform.virtualbox;

import uniandes.unacloud.agent.exceptions.UnsupportedPlatformException;
import uniandes.unacloud.utils.LocalProcessExecutor;

/**
 * Responsible class to create a virtualbox instance depending of version
 * @author CesarF
 *
 */
public class VBoxFactory {
	
	/**
	 * Returns the current virtualbox version installed in host
	 * @param path for vboxmanage application
	 * @return VirtualBox current installed version
	 * @throws UnsupportedPlatformException in case virtualbox version is not supported
	 */
	public static VirtualBox getInstalledVirtualBoxPlatform(String path) throws UnsupportedPlatformException
	{
		String h = LocalProcessExecutor.executeCommandOutput(path, "--version");
		if (h.startsWith(VBox5.VERSION))
			return new VBox5(path);
		if (h.startsWith(VBox43.VERSION))
			return new VBox43(path);		
		throw new UnsupportedPlatformException("VBox: "+path);
	}

}