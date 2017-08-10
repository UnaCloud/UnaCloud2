package uniandes.unacloud.agent.platform.virtualbox;

/**
 * Represents VirtualBox version API 5.*
 * @author CesarF
 *
 */
public class VBox5 extends VirtualBox{

	public static final String VERSION = "5";
	
	public VBox5(String path) {
		super(path);
	}
	
	@Override
	public String[] createExecutionCommand(String path, String imageName, String command, String username, String password) {		
		return new String[]{
				path, 
				"--nologo",
				"guestcontrol",
				imageName, 
				"--username",
				username,
				"--password",
				password,
				"run",
				"--exe",
				command,
				"--wait-stdout",
				"--wait-stderr",
				"--",
				"-l"};
	}

	@Override
	public String[] createCopyToCommand(String path, String imageName, String sourcePath, String guestPath, String username, String password) {		
		guestPath = guestPath.replaceAll("/", "//");
		return new String[]{
				path,
				"--nologo",
				"guestcontrol",
				imageName,
				"--username",
				username,
				"--password",
				password,
				"copyto",
				"--target-directory",
				guestPath,
				sourcePath};
	}

}
