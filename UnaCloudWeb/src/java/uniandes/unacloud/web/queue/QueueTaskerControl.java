package uniandes.unacloud.web.queue;

import java.util.List;

import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.share.queue.QueueTaskerConnection;
import uniandes.unacloud.share.queue.messages.MessageAddInstances;
import uniandes.unacloud.share.queue.messages.MessageCreateCopyFromExecution;
import uniandes.unacloud.share.queue.messages.MessageDeployCluster;
import uniandes.unacloud.share.queue.messages.MessageIdOfImage;
import uniandes.unacloud.share.queue.messages.MessageStopExecutions;
import uniandes.unacloud.share.queue.messages.MessageTaskMachines;
import uniandes.unacloud.share.enums.QueueMessageType;
import uniandes.unacloud.common.enums.TaskEnum;
import uniandes.unacloud.web.domain.DeployedImage;
import uniandes.unacloud.web.domain.Deployment;
import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.domain.Execution;
import uniandes.unacloud.web.domain.Image;

/**
 * Class used to put task in queue messaging service that will be read by Control project
 * @author CesarF
 *
 */
public class QueueTaskerControl {
	
	/**
	 * Represents class to connect to queue provider
	 */
	private static QueueTaskerConnection controlQueue;
	
	/**
	 * Sets the queue manager used to send task. This method should only be called one time.
	 * @param newQueue
	 */
	public static void setQueueConnection(QueueTaskerConnection newQueue) {
		controlQueue = newQueue;
	}

	/**
	 * Puts a task to Remove an image from all connected machines 
	 * @param image that will be removed from cache
	 * @param user who asks the task
	 */
	public static void clearImageFromCacheAndUpdate(Image image, User user) {
		MessageIdOfImage message = new MessageIdOfImage(QueueMessageType.CLEAR_CACHE_UPDATE, String.valueOf(user.getDatabaseId()), image.getDatabaseId());
		controlQueue.sendMessage(message);
	}	
	
	/**
	 * Puts a task to Remove an image from all connected machines 
	 * @param image that will be removed from cache
	 * @param user who asks the task
	 */
	public static void clearImageFromCache(Image image, User user) {
		MessageIdOfImage message = new MessageIdOfImage(QueueMessageType.CLEAR_CACHE, String.valueOf(user.getDatabaseId()), image.getDatabaseId());
		controlQueue.sendMessage(message);
	}	
	
	/**
	 * Puts a task to machines to stop, update agent or remove its cache
	 * @param machines
	 * @param task
	 * @param user
	 * @throws Exception in case task is null or number of machines is 0
	 */
	public static void taskMachines(List<PhysicalMachine> machines, TaskEnum task, User user) {
		long[] listIds = new long[machines.size()];
		for (int i = 0; i < machines.size(); i++)
			listIds[i] = machines.get(i).getDatabaseId();
		
		MessageTaskMachines message = new MessageTaskMachines(String.valueOf(user.getDatabaseId()), listIds, task.getName());
		controlQueue.sendMessage(message);
	}
	
	/**
	 * Puts a task to deploy a cluster
	 * @param deployment
	 * @param user
	 */
	public static void deployCluster(Deployment deployment, User user, TransmissionProtocolEnum transmissionType) {
		MessageDeployCluster message = new MessageDeployCluster(String.valueOf(user.getDatabaseId()), deployment.getDatabaseId(), transmissionType);
		controlQueue.sendMessage(message);
	}
	
	/**
	 * Puts a task to stop deployments in array
	 * @param deployments
	 */
	public static void stopExecutions(List<Execution> executions, User user) {
		Long[] idExecutions = new Long[executions.size()];
		for (int i = 0; i < executions.size(); i++)
			idExecutions[i]=executions.get(i).getDatabaseId();
		
		MessageStopExecutions message = new MessageStopExecutions(String.valueOf(user.getDatabaseId()), idExecutions);
		controlQueue.sendMessage(message);
	}
	
	/**
	 * Puts a task to add instances to a selected deployed image
	 * @param image
	 * @param user
	 */
	public static void addInstancesToDeploy(List<Execution> executions, User user, DeployedImage image, TransmissionProtocolEnum transmissionType) {
		Long[] listIds = new Long[executions.size()];
		for (int i = 0; i < executions.size(); i++)
			listIds[i] = executions.get(i).getDatabaseId();
		
		MessageAddInstances message = new MessageAddInstances(String.valueOf(user.getDatabaseId()), ((Image)image.getImage()).getDatabaseId(), listIds, transmissionType);
		controlQueue.sendMessage(message);
	}
	
	/**
	 * Puts a task to create a copy from a current execution deployed
	 * @param execution
	 * @param image
	 * @param user
	 */
	public static void createCopyFromExecution(Execution execution, Image newImage, Image pastImage, User user) {
		MessageCreateCopyFromExecution message = new MessageCreateCopyFromExecution(String.valueOf(user.getDatabaseId()), execution.getDatabaseId(), newImage.getDatabaseId(), pastImage.getDatabaseId());
		controlQueue.sendMessage(message);
	}
}
