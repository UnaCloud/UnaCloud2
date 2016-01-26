package unacloud.task.queue;

import java.util.List;

import queue.QueueMessage;
import queue.QueueMessageType;
import queue.QueueTaskerConnection;
import unacloud.DeployedImage;
import unacloud.Deployment;
import unacloud.PhysicalMachine;
import unacloud.User;
import unacloud.VirtualMachineExecution;
import unacloud.VirtualMachineImage;
import unacloud.enums.TaskEnum;

/**
 * Class used to put task in queue messaging service that will be read by Control project
 * @author Cesar
 *
 */
public class QueueTaskerControl {
	
	/**
	 * Represents class to connect to queue provider
	 */
	private static QueueTaskerConnection controlQueue;
	
	/**
	 * Set the queue manager used to send task. This method should only be called one time.
	 * @param newQueue
	 */
	public static void setQueueConnection(QueueTaskerConnection newQueue){
		controlQueue = newQueue;
	}

	/**
	 * Put a task to Remove an image from all connected machines 
	 * @param image that will be removed from cache
	 * @param user who asks the task
	 */
	public static void clearCache(VirtualMachineImage image, User user){
		QueueMessage message = new QueueMessage(QueueMessageType.CLEAR_CACHE, user.getDatabaseId()+"", new String[]{image.getDatabaseId()+""});
		controlQueue.sendMessage(message);
	}	
	
	/**
	 * Put a task to machines to stop, update agent or remove its cache
	 * @param machines
	 * @param task
	 * @param user
	 * @throws Exception in case task is null or number of machines is 0
	 */
	public static void taskMachines(List<PhysicalMachine> machines, TaskEnum task, User user){		
		String[] parts = new String[machines.size()+1];
		parts[0]=task.getName();
		for (int i = 0; i < machines.size(); i++) {
			parts[i+1]=machines.get(i).getDatabaseId()+"";
		}		
		QueueMessage message = new QueueMessage(QueueMessageType.SEND_TASK, user.getDatabaseId()+"", parts);
		controlQueue.sendMessage(message);
	}
	
	/**
	 * Put a task to deploy a cluster
	 * @param deployment
	 * @param user
	 */
	public static void deployCluster(Deployment deployment, User user){
		QueueMessage message = new QueueMessage(QueueMessageType.DEPLOY_CLUSTER, user.getDatabaseId()+"", new String[]{deployment.getDatabaseId()+""});
		controlQueue.sendMessage(message);
	}
	
	/**
	 * Put a task to stop deployments in array
	 * @param deployments
	 */
	public static void stopExecutions(VirtualMachineExecution[] executions, User user){
		String[] parts = new String[executions.length];
		for (int i = 0; i < executions.length; i++) {
			parts[i]=executions[i].getDatabaseId()+"";
		}		
		QueueMessage message = new QueueMessage(QueueMessageType.STOP_DEPLOYS, user.getDatabaseId()+"", parts);
		controlQueue.sendMessage(message);
	}
	
	/**
	 * Put a task to add instances to a selected deployed image
	 * @param image
	 * @param user
	 */
	public static void addInstancesToDeploy(VirtualMachineExecution[] executions, User user, DeployedImage image){
		String[] parts = new String[executions.length+1];
		parts[0]=image.getDatabaseId()+"";
		for (int i = 0, j = 1; i < executions.length; i++, j++) {
			parts[j]=executions[i].getDatabaseId()+"";
		}	
		QueueMessage message = new QueueMessage(QueueMessageType.ADD_INSTANCES, user.getDatabaseId()+"", parts);
		controlQueue.sendMessage(message);
	}
	
	/**
	 * Put a task to create a copy from a current execution deployed
	 * @param execution
	 * @param image
	 * @param user
	 */
	public static void createCopyFromExecution(VirtualMachineExecution execution, VirtualMachineImage image, User user){
		QueueMessage message = new QueueMessage(QueueMessageType.CREATE_COPY, user.getDatabaseId()+"", new String[]{execution.getDatabaseId()+"",image.getDatabaseId()+""});
		controlQueue.sendMessage(message);
	}
}
