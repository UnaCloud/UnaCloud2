package unacloud.task.queue;

import queue.QueueMessage;
import queue.QueueMessageType;
import queue.QueueTaskerConnection;
import unacloud.User;
import unacloud.VirtualMachineImage;

/**
 * Class to manage task to queue
 * @author Cesar
 *
 */
public class QueueTaskerFile {
	
	/**
	 * Represents class to connect to queue provider
	 */
	private static QueueTaskerConnection fileQueue;
	
	/**
	 * Set the queue manager used to send task. This method should only be called one time.
	 * @param newQueue
	 */
	public static void setQueueConnection(QueueTaskerConnection newQueue){
		fileQueue = newQueue;
	}
	
	/**
	 * Create a task to copy an image from a private folder to public one.
	 * @param image to copy
	 * @param user image owner
	 */
	public static void createPublicCopy(VirtualMachineImage image, User user){
		QueueMessage message = new QueueMessage(QueueMessageType.CREATE_PUBLIC_IMAGE, user.getDatabaseId()+"", new String[]{image.getDatabaseId()+""});
		fileQueue.sendMessage(message);
	}
	/**
	 * Create a task to copy a public image to a private one.
	 * @param publicImage
	 * @param image
	 * @param user
	 */
	public static void createCopyFromPublic(VirtualMachineImage publicImage, VirtualMachineImage image, User user){
		QueueMessage message = new QueueMessage(QueueMessageType.CREATE_COPY_FROM_PUBLIC, user.getDatabaseId()+"", new String[]{image.getDatabaseId()+"",publicImage.getDatabaseId()+""});
		fileQueue.sendMessage(message);
	}
	/**
	 * Create a task to delete image image from repositories
	 * @param image
	 * @param user
	 */
	public static void deleteImage(VirtualMachineImage image, User user){
		QueueMessage message = new QueueMessage(QueueMessageType.DELETE_IMAGE, user.getDatabaseId()+"", new String[]{image.getDatabaseId()+""});
		fileQueue.sendMessage(message);
	}
	
	/**
	 * Create a task to delete public image from repositories
	 * @param image
	 * @param user
	 */
	public static void deletePublicImage(VirtualMachineImage image, User user){
		QueueMessage message = new QueueMessage(QueueMessageType.DELETE_PUBLIC_IMAGE, user.getDatabaseId()+"", new String[]{image.getDatabaseId()+""});
		fileQueue.sendMessage(message);
	}
	
	/**
	 * Put a task to remove an user, its machines, clusters and deployments
	 * @param user User that will be removed
	 */
	public static void deleteUser(User user, User admin){
		QueueMessage message = new QueueMessage(QueueMessageType.DELETE_USER, admin.getDatabaseId()+"", new String[]{user.getDatabaseId()+""});
		fileQueue.sendMessage(message);
	}
	
}
