package co.edu.uniandes.virtualMachineManager.configuration;

import co.edu.uniandes.hypervisorManager.HypervisorOperationException;

import java.io.File;
import java.io.PrintWriter;

import co.edu.uniandes.utils.AddressUtility;

/**
 * Class responsible to implement methods to configure Debian virtual machines
 */
public class Debian extends AbstractVirtualMachineConfigurator{
	@Override
	public void configureHostname() throws HypervisorOperationException{
		File out=generateRandomFile();
		try(PrintWriter pw = new LinuxPrintWriter(out)){
            pw.println(execution.getHostname());
        } catch (Exception e) {
            return;
        }
		execution.getImage().copyFileOnVirtualMachine("/etc/hostname",out);
		execution.getImage().executeCommandOnMachine("/bin/hostname",execution.getHostname());
		out.delete();
	}
    /**
     * Configures the IP address of the Debian managed virtual machine
     * @throws HypervisorOperationException 
     */
    @Override
    public void configureIP() throws HypervisorOperationException {
    	AddressUtility au = new AddressUtility(execution.getMainInterface().getIp(),execution.getMainInterface().getNetMask());
    	File out=generateRandomFile();
    	try(PrintWriter pw = new LinuxPrintWriter(out)){
            pw.println("auto eth1");
            pw.println("iface eth1 inet static");
            pw.println("address " + au.getIp());
            pw.println("netmask " + au.getNetmask());
            pw.println("network " + au.getNetwork());
            pw.println("broadcast " + au.getBroadcast());
            pw.println("gateway " + au.getGateway());
    	}catch (Exception e) {
			return;
		}
    	execution.getImage().copyFileOnVirtualMachine("/etc/network/interfaces.d/unacloud_interfaces",out);
    	execution.getImage().executeCommandOnMachine("/etc/init.d/networking","restart");
    	execution.getImage().executeCommandOnMachine("/usr/bin/wget","www.google.com");
        out.delete();
    }

    /**
     * Configures a DHCP client of the Debian managed virtual machine
     */
    @Override
    public void configureDHCP() {
        
    }

    /**
     * Configure the host table of the Debian managed virtual machine
     */
    @Override
    public void configureHostTable() {
        
    }
	@Override
	public boolean doPostConfigure(){
		//hypervisor.stopVirtualMachine(execution.getImage());
		return false;
	}
}
