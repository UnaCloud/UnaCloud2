package uniandes.unacloud.agent.execution.configuration;

import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.utils.AddressUtility;

import java.io.File;
import java.io.PrintWriter;

/**
 * Class responsible to implement methods to configure Ubuntu executions
 * @author Clouder
 */
public class Ubuntu extends AbstractExecutionConfigurator {
    /**
     * Configures the IP address of a Ubuntu managed execution
     */
    @Override
    public void configureIP() throws PlatformOperationException {
    	AddressUtility au = new AddressUtility(execution.getMainInterface().getIp(),execution.getMainInterface().getNetMask());
    	
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
    	execution.getImage().copyFileOnExecution("/etc/network/interfaces", out);
    	execution.getImage().executeCommandOnExecution("/bin/rm", "/etc/udev/rules.d/*net.rules");
    	execution.getImage().executeCommandOnExecution("/sbin/ifdown", "eth0");
    	execution.getImage().executeCommandOnExecution("/sbin/ifup", "eth0");
        out.delete();
    }

    /**
     * Configures a DHCP client of the Ubuntu managed execution
     */
    @Override
    public void configureDHCP() {
    	
    }

	public void configureHostname() throws PlatformOperationException {
		File out=generateRandomFile();
		try (PrintWriter pw = new LinuxPrintWriter(out)) {
			pw.println(execution.getHostname());
        } catch (Exception e) {
            return;
        }
		execution.getImage().copyFileOnExecution("/etc/hostname", out);
		execution.getImage().executeCommandOnExecution("/bin/hostname", execution.getHostname());
	}

	@Override
	public void configureHostTable() throws PlatformOperationException {
		
	}

	@Override
	public boolean doPostConfigure() {
		return false;
	}

}