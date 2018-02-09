package uniandes.unacloud.agent.execution.configuration;

import java.io.File;
import java.util.Random;

import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.host.system.OperatingSystem;
import uniandes.unacloud.agent.utils.SystemUtils;
import uniandes.unacloud.common.utils.UnaCloudConstants;
/**
 * Abstract configuration class for physical machines
 * @author Clouder
 *
 */
public abstract class AbstractExecutionConfigurator {
	
	/**
	 * Random object to calculate numbers
	 */
	private static Random random = new Random();
	
	/**
	 * Execution instance 
	 */
	protected Execution execution;
	
	/**
	 * sets VM property
	 * @param execution execution to be configured
	 */
	public void setExecution(Execution execution) {
		this.execution = execution;
	}
	
	/**
	 * generates a random file
	 * @return file created
	 */
	public static File generateRandomFile(){
		if (!new File(UnaCloudConstants.TEMP_FILE).exists())
			new File(UnaCloudConstants.TEMP_FILE).mkdir();
		return new File(UnaCloudConstants.TEMP_FILE + OperatingSystem.PATH_SEPARATOR + Math.abs(random.nextLong()) + UnaCloudConstants.FILE_EXTENSION);
	}
	
	/**
	 * Makes hostname configuration
	 * @throws PlatformOperationException
	 */
	public abstract void configureHostname() throws PlatformOperationException;
	/**
	 * Makes IP configuration
	 * @throws PlatformOperationException
	 */
	public abstract void configureIP() throws PlatformOperationException;
    /**
     * Makes DHCP configuration
     * @throws PlatformOperationException
     */
	public abstract void configureDHCP() throws PlatformOperationException;
    
	/**
	 * Configures host table
	 * @throws PlatformOperationException
	 */
	public abstract void configureHostTable() throws PlatformOperationException;
    /**
     * validates if a VM should be started again
     * @return true if the VM should be started again, false in case not
     */
    public abstract boolean doPostConfigure();
   
    /**
     * waits while configuration is done
     * @param time wait time
     */
    public final void waitTime(long time) {
    	SystemUtils.sleep(time);      
    }
}
