/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hypervisorManager;

import com.losandes.utils.Constants;

/**
 * Implementation of hypervisor abstract class to give support for
 * VMwareWorkstation hypervisor.
 *
 * @author Clouder
 */

public class VMwareWorkstation extends VMwareAbstractHypervisor{
	public static final String HYPERVISOR_ID=Constants.VM_WARE_WORKSTATION;
	public VMwareWorkstation(String path) {
		super(path);
	}
	
	@Override
	public String getType() {
		return "ws";
	}
}