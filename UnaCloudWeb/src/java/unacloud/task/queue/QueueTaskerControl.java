package unacloud.task.queue;

import java.util.List;

import unacloud.DeployedImage;
import unacloud.Deployment;
import unacloud.PhysicalMachine;
import unacloud.User;
import unacloud.VirtualMachineExecution;
import unacloud.VirtualMachineImage;

/**
 * Class used to put task in queue messaging service that will be read by Control project
 * @author Cesar
 *
 */
public class QueueTaskerControl {

	/**
	 * Put a task to Remove an image from all machines connected
	 * @param image that will be removed from cache
	 * @param user who asks the task
	 */
	public static void clearCache(VirtualMachineImage image, User user){
		
	}
	
	/**
	 * Put a task to remove an user, its machines, clusters and deployments
	 * @param user User that will be removed
	 */
	public static void deleteUser(User user, User admin){
		
	}
	
	/**
	 * Put a task to machines to stop, update agent or remove its cache
	 * @param machines
	 * @param task
	 * @param user
	 */
	public static void taskMachines(List<PhysicalMachine> machines, String task, User user){
		System.out.println(task);
		System.out.println(machines);
		System.out.println(user);
	}
	
	/**
	 * Put a task to deploy a cluster
	 * @param deployment
	 * @param user
	 */
	public static void deployCluster(Deployment deployment, User user){
		
	}
	
	/**
	 * Put a task to stop deployments in array
	 * @param deployments
	 */
	public static void stopDeployments(Deployment[] deployments){
		
	}
	
	/**
	 * Put a task to add instances to a selected deployed image
	 * @param image
	 * @param user
	 */
	public static void addInstancesToDeploy(DeployedImage image, User user){
		
	}
	
	/**
	 * Put a task to create a copy from a current execution deployed
	 * @param execution
	 * @param image
	 * @param user
	 */
	public static void createCopyFromExecution(VirtualMachineExecution execution, VirtualMachineImage image, User user){
		
	}
}
