package uniandes.unacloud.agent.system;

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

	@Override
	public String getTurnOffCommand() {		
		return "sh " + BIN + PATH_SEPARATOR + LINUX_TURN_OFF_FILE_COMMAND;
	}

	@Override
	public String getRestartCommand() {		
		return "sh " + BIN + PATH_SEPARATOR + LINUX_RESTART_FILE_COMMAND;
	}

	@Override
	public String getLogOutCommand() {
		try {
			return "sh " + BIN + PATH_SEPARATOR + LINUX_LOGOUT_FILE_COMMAND + getUserName();
		} catch (UnsupportedCommandException e) {			
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getUserName() throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Get user name", "Linux");
	}

	@Override
	public String getWhoAmI() throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Get who am I", "Linux");
	}

	@Override
	public String getHostNameCommand() throws UnsupportedCommandException {
		return LINUX_HOSTNAME_COMMAND;
	}

	@Override
	public String turnOnMachines(String[] message)throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Turn on", "Mac");
	}

}
