package uniandes.unacloud.agent.execution.configuration;

import java.io.File;
import java.util.Random;

import uniandes.unacloud.agent.execution.entities.VirtualMachineExecution;
import uniandes.unacloud.agent.hypervisor.HypervisorOperationException;
import uniandes.unacloud.common.utils.UnaCloudConstants;
/**
 * Abstract configuration class for physical machines
 * @author Clouder
 *
 */
public abstract class AbstractVirtualMachineConfigurator{
	
	/**
	 * Random object to calculate numbers
	 */
	private static Random random=new Random();
	
	/**
	 * Execution instance 
	 */
	protected VirtualMachineExecution execution;
	
	/**
	 * sets VM property
	 * @param execution Virtual machine to be configured
	 */
	public void setExecution(VirtualMachineExecution execution) {
		this.execution = execution;
	}
	/**
	 * generates a random file
	 * @return file created
	 */
	public static File generateRandomFile(){
		if(!new File(UnaCloudConstants.TEMP_FILE).exists())new File(UnaCloudConstants.TEMP_FILE).mkdir();
		return new File(UnaCloudConstants.TEMP_FILE+"/"+Math.abs(random.nextLong())+UnaCloudConstants.FILE_EXTENSION);
	}
	
	/**
	 * Makes hostname configuration
	 * @throws HypervisorOperationException
	 */
	public abstract void configureHostname() throws HypervisorOperationException;
	/**
	 * Makes IP configuration
	 * @throws HypervisorOperationException
	 */
	public abstract void configureIP() throws HypervisorOperationException;
    /**
     * Makes DHCP configuration
     * @throws HypervisorOperationException
     */
	public abstract void configureDHCP() throws HypervisorOperationException;
    
	/**
	 * Configures host table
	 * @throws HypervisorOperationException
	 */
	public abstract void configureHostTable() throws HypervisorOperationException;
    /**
     * validates if a VM should be started again
     * @return true if the VM should be started again, false in case not
     */
    public abstract boolean doPostConfigure();
   
    /**
     * waits while configuration is done
     * @param time wait time
     */
    public final void waitTime(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
        }
    }
}
