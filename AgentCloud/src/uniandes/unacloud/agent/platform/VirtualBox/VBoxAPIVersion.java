package uniandes.unacloud.agent.platform.VirtualBox;

/**
 * Represents virtualbox version api
 * @author Cesar
 *
 */
public abstract class VBoxAPIVersion {
	
	/**
	 * Method to create command to be executed in guest machine
	 * @param path: VBoxManage path
	 * @param imageName: image name
	 * @param command: command to be executed in guest
	 * @param username: username in virtual machine
	 * @param password: password for username
	 * @return Array with all command elements
	 */
	public abstract String[] createExecutionCommand(String path, String imageName, String command, String username, String password);
	
	/**
	 * Mathod to create command to copy files in guest machine
	 * @param path: VBoxManage path
	 * @param imageName: image name
	 * @param sourcePath: file path to be copied in guest
	 * @param guestPath: file path to be replaced in guest
	 * @param username: username in virtual machine
	 * @param password: password for username
	 * @return Array with all command elements
	 */
	public abstract String[] createCopyToCommand(String path, String imageName, String sourcePath, String guestPath, String username, String password);

}
