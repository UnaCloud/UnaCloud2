package unacloud.task.queue;

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
		QueueMessage message = new QueueMessage("createPublic", user.getDatabaseId()+"", new String[]{image.getDatabaseId()+""});
		fileQueue.sendMessage(message);
	}
	/**
	 * Create a task to copy a public image to a private one.
	 * @param publicImage
	 * @param image
	 * @param user
	 */
	public static void createCopyFromPublic(VirtualMachineImage publicImage, VirtualMachineImage image, User user){
		QueueMessage message = new QueueMessage("createCopy", user.getDatabaseId()+"", new String[]{image.getDatabaseId()+"",publicImage.getDatabaseId()+""});
		fileQueue.sendMessage(message);
	}
	/**
	 * Create a task to delete image image from repositories
	 * @param image
	 * @param user
	 */
	public static void deleteImage(VirtualMachineImage image, User user){
		QueueMessage message = new QueueMessage("deleteImage", user.getDatabaseId()+"", new String[]{image.getDatabaseId()+""});
		fileQueue.sendMessage(message);
	}
	
	/**
	 * Create a task to delete public image from repositories
	 * @param image
	 * @param user
	 */
	public static void deletePublicImage(VirtualMachineImage image, User user){
		QueueMessage message = new QueueMessage("deletePublic", user.getDatabaseId()+"", new String[]{image.getDatabaseId()+""});
		fileQueue.sendMessage(message);
	}

//	/**
//	 * Used to delete public image files from main repository templates
//	 * @param publicImage
//	 */
//	private void deletePublicImage(VirtualMachineImage publicImage){
//		def repository= Repository.findByName("Main Repository")
//		File f = new java.io.File(repository.root+"imageTemplates"+separator+publicImage.name+separator);
//		f.deleteDir();
//	}
}
