/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uniandes.unacloud.agent.platform;

import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Implementation of platform abstract class to give support for
 * VMwareWorkstation platform.
 *
 * @author Clouder
 */

public class VMwareWorkstation extends VMwareAbstractHypervisor{
	public static final String PLATFORM_ID=UnaCloudConstants.VM_WARE_WORKSTATION;
	public VMwareWorkstation(String path) {
		super(path);
	}
	
	@Override
	public String getType() {
		return "ws";
	}
}