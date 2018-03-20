package uniandes.unacloud.agent.host.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uniandes.unacloud.agent.exceptions.UnsupportedCommandException;

/**
 * Class which represents Linux Operating System: Used in Debian and RedHat families
 * Response commands and execute tasks for this OS
 * @author CesarF
 *
 */
public class LinuxOS extends OperatingSystem{
	
	public static final String LINUX_TURN_OFF_FILE_COMMAND = "turnOffLinux.sh ";
	public static final String LINUX_RESTART_FILE_COMMAND = "restartLinux.sh ";
	public static final String LINUX_LOGOUT_FILE_COMMAND = "logoutLinux.sh ";
	public static final String LINUX_HOSTNAME_COMMAND = "hostname";
	public static final String USER_FOR_OS = "root";

	@Override
	public String[] getTurnOffCommand() {		
		return new String[]{"sh", BIN + PATH_SEPARATOR + LINUX_TURN_OFF_FILE_COMMAND};
	}

	@Override
	public String[] getRestartCommand() {		
		return new String[]{"sh", BIN + PATH_SEPARATOR + LINUX_RESTART_FILE_COMMAND};
	}

	@Override
	public String[] getLogOutCommand() {
		try {
			return new String[]{"sh", BIN + PATH_SEPARATOR + LINUX_LOGOUT_FILE_COMMAND, getUserName()};
		} catch (UnsupportedCommandException e) {			
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getUserName() throws UnsupportedCommandException {
		String userName = null;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"w","-h"});
            InputStream is = p.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                for (String linea; (linea = br.readLine()) != null && userName == null;) {
                	String[] data = linea.split(" ");
                	if (data.length > 0) {                		
                        userName = data[0];
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
		return LINUX_HOSTNAME_COMMAND;
	}

	@Override
	public String turnOnMachines(String[] message)throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Turn on", "Linux");
	}

	@Override
	public String getProgramDataPath() throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Data path for vmware", "Linux");
	}

	@Override
	public boolean isRunningBySuperUser() throws UnsupportedCommandException {
		String user = getWhoAmI();
		return user != null && !user.toLowerCase().contains(USER_FOR_OS);
	}

	@Override
	public void setPriorityProcess(String processName) {
		try {	
			String [] result = executeCommandOS(new String[]{"pgrep", "-x", processName, "-u", USER_FOR_OS }).split("\n|\r");
			for (String line: result){
				if (line != null && !line.isEmpty())
				{
					System.out.println(line);
					String pid = line.split("\t")[0].trim();
					executeCommandOS(new String[]{"renice", "-n", "19", "-p", pid});
				}
			}
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
		return "ping -c 2 " + ipAddress;
	}

}
