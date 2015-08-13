package virtualMachineConfiguration;

import hypervisorManager.HypervisorOperationException;

import java.io.File;
import java.io.PrintWriter;

import utils.AddressUtility;

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
     * @param netmask
     * @param ip
     * @throws HypervisorOperationException 
     */
    @Override
    public void configureIP() throws HypervisorOperationException {
    	AddressUtility au = new AddressUtility(execution.getIp(),execution.getNetMask());
    	File out=generateRandomFile();
    	try(PrintWriter pw = new LinuxPrintWriter(out)){
    		pw.println("auto lo");
            pw.println("iface lo inet loopback");
            pw.println("auto eth0");
            pw.println("iface eth0 inet static");
            pw.println("address " + au.getIp());
            pw.println("netmask " + au.getNetmask());
            pw.println("network " + au.getNetwork());
            pw.println("broadcast " + au.getBroadcast());
            pw.println("gateway " + au.getGateway());
    	}catch (Exception e) {
			return;
		}
    	execution.getImage().copyFileOnVirtualMachine("/etc/network/interfaces",out);
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
