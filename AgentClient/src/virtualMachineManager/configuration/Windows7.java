package virtualMachineManager.configuration;

import hypervisorManager.HypervisorOperationException;
import utils.AddressUtility;

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
    	execution.getImage().executeCommandOnMachine("netsh.exe","interface","ip","set","address","name=Conexión de Área local","static",au.getIp(),au.getNetmask(),au.getGateway(),"1");
    }
    @Override
    public void configureDHCP() {
    }
	@Override
	public void configureHostname() throws HypervisorOperationException {
		// TODO to be implemented
		
	}
	@Override
	public void configureHostTable() throws HypervisorOperationException {
		// TODO to be implemented
		
	}
	@Override
	public boolean doPostConfigure() {
		return false;
	}
    
}