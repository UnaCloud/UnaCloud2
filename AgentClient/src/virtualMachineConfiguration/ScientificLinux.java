package virtualMachineConfiguration;

import utils.AddressUtility;
import hypervisorManager.HypervisorOperationException;

import java.io.File;
import java.io.PrintWriter;

/**
 * Class responsible to implement methods to configure Scientific Linux virtual machines
 * @author Clouder
 */
public class ScientificLinux extends AbstractVirtualMachineConfigurator{

    /**
     * Configures the ip address of the Scientific Linux managed virtual machine
     * @param netmask
     * @param ip
     */
    public void configureIP() throws HypervisorOperationException {
    	AddressUtility au = new AddressUtility(execution.getIp(),execution.getNetMask());
    	File out=generateRandomFile();
    	try(PrintWriter pw = new LinuxPrintWriter(out)){
    		pw.println("# Advanced Micro Devices [AMD] 79c970 [PCnet32 LANCE]");
            pw.println("DEVICE=eth0");
            pw.println("BOOTPROTO=none");
            pw.println("ONBOOT=yes");
            pw.println("NETMASK=" + au.getNetmask());
            pw.println("IPADDR=" + au.getIp());
            pw.println("GATEWAY=" + au.getGateway());
            pw.println("TYPE=Ethernet");
            pw.println("USERCTL=no");
            pw.println("IPV6INIT=no");
            pw.println("PEERDNS=yes");
    	}catch (Exception e) {
			return;
		}
    	execution.getImage().copyFileOnVirtualMachine("/etc/network/interfaces",out);
    	execution.getImage().executeCommandOnMachine("/sbin/ifdown","eth0");
    	execution.getImage().executeCommandOnMachine("/sbin/ifup","eth0");
        out.delete();
    }

    /**
     * Configures a DHCP client of the Scientific Linux managed virtual machine
     */
    @Override
    public void configureDHCP() {
    }

    @Override
	public void configureHostname() throws HypervisorOperationException{
		File out=generateRandomFile();
		try(PrintWriter pw = new LinuxPrintWriter(out)){
			pw.println("NETWORKING=yes");
	        pw.println("NETWORKING_IPV6=no");
	        pw.println("HOSTNAME=" + execution.getHostname());
        } catch (Exception e) {
            return;
        }
		execution.getImage().copyFileOnVirtualMachine("/etc/sysconfig/network",out);
		execution.getImage().executeCommandOnMachine("/bin/hostname",execution.getHostname());
	}

	@Override
	public void configureHostTable() throws HypervisorOperationException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean doPostConfigure() {
		return false;
	}
}