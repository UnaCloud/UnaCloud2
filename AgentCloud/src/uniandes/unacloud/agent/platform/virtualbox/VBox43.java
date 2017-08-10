package uniandes.unacloud.agent.platform.virtualbox;

/**
 * Represents VirtualBox version API 4.3
 * @author CesarF
 *
 */
public class VBox43 extends VirtualBox {
	
	public static final String VERSION = "4.3";
	
	public VBox43(String path) {
		super(path);
	}
	
	@Override
	public String[] createExecutionCommand(String path, String imageName, String command, String username, String password) {
		return new String[]{
				path, 
				"--nologo", 
				"guestcontrol", 
				imageName, 
				"execute", 
				"--image", 
				command, 
				"--username", 
				username, 
				"--password", 
				password, 
				"--wait-exit", 
				"--"};
	}

	@Override
	public String[] createCopyToCommand(String path, String imageName, String sourcePath, String guestPath, String username, String password) {
		return new String[]{
				path, 
				"guestcontrol", 
				imageName, 
				"copyto", 
				sourcePath, 
				guestPath, 
				"--username", 
				username, 
				"--password", 
				password};
	}

}
