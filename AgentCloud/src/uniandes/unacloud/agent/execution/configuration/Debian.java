package uniandes.unacloud.agent.execution.configuration;

import java.io.File;
import java.io.PrintWriter;

import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.utils.AddressUtility;

/**
 * Class responsible to implement methods to configure Debian execution
 */
public class Debian extends AbstractExecutionConfigurator {
	
	@Override
	public void configureHostname() throws PlatformOperationException {
		File out=generateRandomFile();
		try(PrintWriter pw = new LinuxPrintWriter(out)) {
            pw.println(execution.getHostname());
        } catch (Exception e) {
            return;
        }
		execution.getImage().copyFileOnExecution("/etc/hostname", out);
		execution.getImage().executeCommandOnExecution("/bin/hostname", execution.getHostname());
		out.delete();
	}
	
    /**
     * Configures the IP address of the Debian managed execution
     * @throws PlatformOperationException 
     */
    @Override
    public void configureIP() throws PlatformOperationException {
    	AddressUtility au = new AddressUtility(execution.getMainInterface().getIp(), execution.getMainInterface().getNetMask());
    	File out=generateRandomFile();
    	try (PrintWriter pw = new LinuxPrintWriter(out)) {
    		pw.println("auto lo");
            pw.println("iface lo inet loopback");
            pw.println("auto eth0");
            pw.println("iface eth0 inet static");
            pw.println("address " + au.getIp());
            pw.println("netmask " + au.getNetmask());
            pw.println("network " + au.getNetwork());
            pw.println("broadcast " + au.getBroadcast());
            pw.println("gateway " + au.getGateway());
    	} catch (Exception e) {
			return;
		}
    	execution.getImage().copyFileOnExecution("/etc/network/interfaces",out);
    	execution.getImage().executeCommandOnExecution("/etc/init.d/networking","restart");
    	execution.getImage().executeCommandOnExecution("/usr/bin/wget","www.google.com");
        out.delete();
    }

    /**
     * Configures a DHCP client of the Debian managed execution
     */
    @Override
    public void configureDHCP() {        
    }

    /**
     * Configure the host table of the Debian managed execution
     */
    @Override
    public void configureHostTable() {        
    }
    
	@Override
	public boolean doPostConfigure() {
		return false;
	}
}
