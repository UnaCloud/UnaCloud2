package uniandes.unacloud.agent.execution.configuration;

import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.utils.AddressUtility;

/**
 * Responsible to configure Windows 7 machine
 * @author Clouder
 */
public class Windows7 extends AbstractExecutionConfigurator {
	/**
	 * Check AbstractExecutionConfigurator for more information
	 */
    @Override
    public void configureIP() throws PlatformOperationException {
    	AddressUtility au = new AddressUtility(execution.getMainInterface().getIp(),execution.getMainInterface().getNetMask());
    	execution.getImage().executeCommandOnExecution(
    			"netsh.exe",
    			"interface",
    			"ip","set",
    			"address",
    			"name=Conexión de Área local",
    			"static",
    			au.getIp(),
    			au.getNetmask(),
    			au.getGateway(),
    			"1");
    }
    @Override
    public void configureDHCP() {
    }
    
	@Override
	public void configureHostname() throws PlatformOperationException {
		
	}
	
	@Override
	public void configureHostTable() throws PlatformOperationException {
		
	}
	
	@Override
	public boolean doPostConfigure() {
		return false;
	}
    
}