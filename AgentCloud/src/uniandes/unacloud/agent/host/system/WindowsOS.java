package uniandes.unacloud.agent.host.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uniandes.unacloud.agent.exceptions.UnsupportedCommandException;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Class which represents Windows Operating System
 * Response commands and execute tasks for this OS
 * @author CesarF
 *
 */
public class WindowsOS extends OperatingSystem{
	
	public static final String[] WINDOWS_TURN_OFF_COMMAND = new String[]{"c:\\windows\\system32\\shutdown.exe", "-s", "-t", "60"};
    public static final String[] WINDOWS_RESTART_COMMAND = new String[]{"c:\\windows\\system32\\shutdown.exe", "-r", "-t", "30"};
    public static final String[] WINDOWS_LOGOUT_COMMAND = new String[]{"c:\\windows\\system32\\shutdown.exe", "-l", "-f"};
    public static final String WINDOWS_HOSTNAME_COMMAND = "hostname";

	@Override
	public String[] getTurnOffCommand() {		
		return WINDOWS_TURN_OFF_COMMAND;
	}

	@Override
	public String[] getRestartCommand() {		
		return WINDOWS_RESTART_COMMAND;
	}

	@Override
	public String[] getLogOutCommand() {		
		return WINDOWS_LOGOUT_COMMAND;
	}

	@Override
	public String getUserName() throws UnsupportedCommandException {
	    String userName = null;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe","/c","quser"});
            InputStream is = p.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                br.readLine();
                for (String linea,lw; (linea = br.readLine()) != null;) {
                	lw = linea.toLowerCase();
                	if (lw.contains("active") || lw.contains("activo")) {
                		final String user = linea.trim().split(" |\t")[0].replaceAll("[^\\w\\.-]","");
                        userName = (userName == null ? "" : (userName+";")) + user;
                	}
                    
                }
            }
            p.destroy();
        } catch (IOException ex) {
        }
        return userName;
	}

	@Override
	public String getWhoAmI() throws UnsupportedCommandException {
		String userName = null;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"whoami"});
            InputStream is = p.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                userName = br.readLine();
            }
            p.destroy();
        } catch (IOException ex) {
        }
        return userName;
	}

	@Override
	public String getHostNameCommand() throws UnsupportedCommandException {
		return WINDOWS_HOSTNAME_COMMAND;
	}

	@Override
	public String turnOnMachines(String[] machines) throws UnsupportedCommandException {
        for (String mac : machines) {
            try {
                 Runtime.getRuntime().exec("wol.exe " + mac.replace(":", ""));
            } catch (IOException ex) {
            }
        }
		return UnaCloudConstants.SUCCESSFUL_OPERATION;
	}

	@Override
	public String getProgramDataPath() throws UnsupportedCommandException {		
		return "C:\\ProgramData\\VMware\\hostd\\datastores.xml";
	}

	@Override
	public boolean isRunningBySuperUser() throws UnsupportedCommandException {
		String user = getWhoAmI();
		return user!=null&&!user.toLowerCase().contains("system");
	}

	@Override
	public void setPriorityProcess(String processName) {
    	try {
			executeCommandOS(new String[]{"wmic", "process", "where", "name=\"" + processName + ".exe\"", "CALL", "setpriority", "64"});
		} catch (Exception e) {
			e.printStackTrace();
		}	    
	}

	@Override
	public String getJavaCommand() throws UnsupportedCommandException {		
		return "java";
	}

	@Override
	public String getPingCommand(String ipAddress)throws UnsupportedCommandException {		
		return "ping " + ipAddress + " -n 2";
	}	

}
