package uniandes.unacloud.agent.execution.configuration;

import uniandes.unacloud.agent.platform.PlatformOperationException;
import uniandes.unacloud.agent.utils.AddressUtility;

/**
 * Does a no-op configuration of a docker container
 * @author Emanuel Krivoy
 */
public class DockerBlank extends AbstractExecutionConfigurator{
	/**
	 * Check AbstractExecutionConfigurator for more information
	 */
    @Override
    public void configureIP() throws PlatformOperationException {
    }
    
    @Override
    public void configureDHCP() {
    }
	@Override
	public void configureHostname() throws PlatformOperationException {
		execution.getImage().executeCommandOnExecution("hostname", execution.getHostname());
	}
	@Override
	public void configureHostTable() throws PlatformOperationException {
		
	}
	@Override
	public boolean doPostConfigure() {
		return false;
	}
    
}