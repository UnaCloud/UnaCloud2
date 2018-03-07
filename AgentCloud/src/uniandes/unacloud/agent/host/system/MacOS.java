package uniandes.unacloud.agent.host.system;

import uniandes.unacloud.agent.exceptions.UnsupportedCommandException;

/**
 * Class which represents Mac Operating System
 * Response commands and execute tasks for this OS
 * @author CesarF
 *
 */
public class MacOS extends OperatingSystem{
	
	public static final String MAC_PERL_COMMAND = "/usr/bin/perl";
    public static final String MAC_TURN_OFF_FILE_COMMAND = "turnOffMac.sh";
    public static final String MAC_RESTART_FILE_COMMAND = "restartMac.sh";
    public static final String MAC_LOGOUT_FILE_COMMAND = "logoutMac.pl";
	public static final String MAC_HOSTNAME_COMMAND = "hostname";
    
	@Override
	public String[] getTurnOffCommand() {		
		return new String[]{"sh",BIN + PATH_SEPARATOR + MAC_TURN_OFF_FILE_COMMAND};
	}

	@Override
	public String[] getRestartCommand() {
		return new String[]{"sh", BIN + PATH_SEPARATOR + MAC_RESTART_FILE_COMMAND};
	}

	@Override
	public String[] getLogOutCommand() throws UnsupportedCommandException {		
		throw new UnsupportedCommandException("log out", "Mac");
	}

	@Override
	public String getUserName() throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Get user", "Mac");
	}

	@Override
	public String getWhoAmI() throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Get who I am", "Mac");
	}

	@Override
	public String getHostNameCommand() throws UnsupportedCommandException {
		return MAC_HOSTNAME_COMMAND;
	}

	@Override
	public String turnOnMachines(String[] message)throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Turn on", "Mac");
	}
	
	@Override
	public String getProgramDataPath() throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Data path for vmware", "Mac");
	}

	@Override
	public boolean isRunningBySuperUser() throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Running by user", "Mac");
	}

	@Override
	public void setPriorityProcess(String processName) throws UnsupportedCommandException{
		throw new UnsupportedCommandException("Set priority to process","Mac");		
	}

	@Override
	public String getJavaCommand() throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Java Command","Mac");		
	}

	@Override
	public String getPingCommand(String ipAddress)throws UnsupportedCommandException {
		throw new UnsupportedCommandException("Ping Command","Mac");		
	}
}
