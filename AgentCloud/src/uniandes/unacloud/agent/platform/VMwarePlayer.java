package uniandes.unacloud.agent.platform;

import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Implementation of platform abstract class to give support for VMwarePlayer platform.
 * @author Clouder
 */
class VMwarePlayer extends VMwareAbstractHypervisor{
	public static final String PLATFORM_ID=UnaCloudConstants.VM_WARE_PLAYER;
	public VMwarePlayer(String path) {
		super(path);
	}
	@Override
	public String getType() {
		return "player";
	}
}