package uniandes.unacloud.agent.platform.VirtualBox;

public class VBox43 extends VBoxAPIVersion{
	
	public static final String VERSION = "4.3";
	
	@Override
	public String[] createExecutionCommand(String path, String imageName,String command, String username, String password) {
		return new String[]{path, "--nologo", "guestcontrol", imageName, "execute", "--image", command, "--username", username, "--password", password, "--wait-exit", "--"};
	}

	@Override
	public String[] createCopyToCommand(String path, String imageName,String sourcePath, String guestPath, String username,String password) {
		return new String[]{path, "guestcontrol", imageName, "copyto", sourcePath, guestPath, "--username", username, "--password", password};
	}

}
