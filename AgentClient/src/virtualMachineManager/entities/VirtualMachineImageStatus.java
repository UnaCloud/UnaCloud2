package virtualMachineManager.entities;

/**
 * Status of virtual machine image
 * @author clouder
 *
 */
public enum VirtualMachineImageStatus {
	/**
	 * represents an image used by hypervisors
	 */
	LOCK,
	/**
	 * Represents an image free to be deployed
	 */
	FREE,
	/**
	 * Represents an image when in being configuring and testing connection
	 */
	STARTING
}
