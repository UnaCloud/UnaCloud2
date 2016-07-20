package co.edu.uniandes.virtualMachineManager.configuration;

import co.edu.uniandes.hypervisorManager.HypervisorOperationException;
import co.edu.uniandes.utils.AddressUtility;

/**
 * Responsible to configure Windows 7 machine
 * @author Clouder
 */
public class Windows7 extends AbstractVirtualMachineConfigurator{
	/**
	 * Check AbstractVirtualMachineConfigurator for more information
	 */
    @Override
    public void configureIP() throws HypervisorOperationException {
    	AddressUtility au = new AddressUtility(execution.getMainInterface().getIp(),execution.getMainInterface().getNetMask());
    	execution.getImage().executeCommandOnMachine("netsh.exe","interface","ip","set","address","name=Conexi�n de �rea local","static",au.getIp(),au.getNetmask(),au.getGateway(),"1");
    }
    @Override
    public void configureDHCP() {
    }
	@Override
	public void configureHostname() throws HypervisorOperationException {
		
	}
	@Override
	public void configureHostTable() throws HypervisorOperationException {
		
	}
	@Override
	public boolean doPostConfigure() {
		return false;
	}
    
}
