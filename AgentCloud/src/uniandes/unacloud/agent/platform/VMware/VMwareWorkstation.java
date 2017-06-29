/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uniandes.unacloud.agent.platform.vmware;


/**
 * Implementation of platform abstract class to give support for
 * VMwareWorkstation platform.
 *
 * @author Clouder
 */

public class VMwareWorkstation extends VMwareAbstractHypervisor {
	
	public VMwareWorkstation(String path) {
		super(path);
	}
	
	@Override
	protected String getType() {
		return "ws";
	}
}