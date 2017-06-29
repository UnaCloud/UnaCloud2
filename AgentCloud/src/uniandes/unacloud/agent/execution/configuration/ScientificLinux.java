package uniandes.unacloud.agent.execution.configuration;

import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.utils.AddressUtility;

import java.io.File;
import java.io.PrintWriter;

/**
 * Class responsible to implement methods to configure Scientific Linux executions
 * @author Clouder
 */
public class ScientificLinux extends AbstractExecutionConfigurator{

    /**
     * Configures the IP address of the Scientific Linux managed execution
     */
    public void configureIP() throws PlatformOperationException {
    	AddressUtility au = new AddressUtility(execution.getMainInterface().getIp(),execution.getMainInterface().getNetMask());
    	File out=generateRandomFile();
    	try (PrintWriter pw = new LinuxPrintWriter(out)) {
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
    	} catch (Exception e) {
			return;
		}
    	execution.getImage().copyFileOnExecution("/etc/network/interfaces", out);
    	execution.getImage().executeCommandOnExecution("/sbin/ifdown", "eth0");
    	execution.getImage().executeCommandOnExecution("/sbin/ifup", "eth0");
        out.delete();
    }

    /**
     * Configures a DHCP client of the Scientific Linux managed execution
     */
    @Override
    public void configureDHCP() {
    	
    }

    @Override
	public void configureHostname() throws PlatformOperationException {
		File out=generateRandomFile();
		try (PrintWriter pw = new LinuxPrintWriter(out)) {
			pw.println("NETWORKING=yes");
	        pw.println("NETWORKING_IPV6=no");
	        pw.println("HOSTNAME=" + execution.getHostname());
        } catch (Exception e) {
            return;
        }
		execution.getImage().copyFileOnExecution("/etc/sysconfig/network",out);
		execution.getImage().executeCommandOnExecution("/bin/hostname",execution.getHostname());
	}

	@Override
	public void configureHostTable() throws PlatformOperationException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean doPostConfigure() {
		return false;
	}
}