package virtualMachineConfiguration;

import hypervisorManager.HypervisorOperationException;

import java.io.File;
import java.util.Random;

import virtualMachineManager.VirtualMachineExecution;

public abstract class AbstractVirtualMachineConfigurator{
	
	private static Random r=new Random();
	VirtualMachineExecution execution;
	
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
		if(!new File("temp").exists())new File("temp").mkdir();
		return new File("temp/"+Math.abs(r.nextLong())+".txt");
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
     * Returns true if the VM should be started again 
     * @return
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
