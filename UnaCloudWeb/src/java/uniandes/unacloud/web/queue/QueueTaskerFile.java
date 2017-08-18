package uniandes.unacloud.web.queue;


import uniandes.unacloud.share.enums.QueueMessageType;
import uniandes.unacloud.share.queue.QueueTaskerConnection;
import uniandes.unacloud.share.queue.messages.MessageCreateCopyFromPublic;
import uniandes.unacloud.share.queue.messages.MessageDeleteUser;
import uniandes.unacloud.share.queue.messages.MessageIdOfImage;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.domain.Image;

/**
 * Class used to put task in queue messaging service that will be read by File Manager project 
 * @author CesarF
 *
 */
public class QueueTaskerFile {
	
	/**
	 * Represents class to connect to queue provider
	 */
	private static QueueTaskerConnection fileQueue;
	
	/**
	 * Sets the queue manager used to send task. This method should only be called one time.
	 * @param newQueue
	 */
	public static void setQueueConnection(QueueTaskerConnection newQueue){
		fileQueue = newQueue;
	}
	
	/**
	 * Creates a task to copy an image from a private folder to public one.
	 * @param image to copy
	 * @param user image owner
	 */
	public static void createPublicCopy(Image image, User user) {
		MessageIdOfImage message = new MessageIdOfImage(QueueMessageType.CREATE_PUBLIC_IMAGE, String.valueOf(user.getDatabaseId()), image.getDatabaseId());
		fileQueue.sendMessage(message);
	}
	/**
	 * Creates a task to copy a public image to a private one.
	 * @param publicImage
	 * @param image
	 * @param user
	 */
	public static void createCopyFromPublic(Image publicImage, Image image, User user) { 
		MessageCreateCopyFromPublic message = new MessageCreateCopyFromPublic(String.valueOf(user.getDatabaseId()), image.getDatabaseId(), publicImage.getDatabaseId());
		fileQueue.sendMessage(message);
	}
	/**
	 * Creates a task to delete image from repositories
	 * @param image
	 * @param user
	 */
	public static void deleteImage(Image image, User user) {
		MessageIdOfImage message = new MessageIdOfImage(QueueMessageType.DELETE_IMAGE, String.valueOf(user.getDatabaseId()), image.getDatabaseId());
		fileQueue.sendMessage(message);
	}
	
	/**
	 * Creates a task to delete public image from repositories
	 * @param image
	 * @param user
	 */
	public static void deletePublicImage(Image image, User user) {
		MessageIdOfImage message = new MessageIdOfImage(QueueMessageType.DELETE_PUBLIC_IMAGE, String.valueOf(user.getDatabaseId()), image.getDatabaseId());
		fileQueue.sendMessage(message);
	}
	
	/**
	 * Puts a task to remove an user, its machines, clusters and deployments
	 * @param user User that will be removed
	 */
	public static void deleteUser(User user, User admin) {
		MessageDeleteUser message = new MessageDeleteUser(String.valueOf(admin.getDatabaseId()), user.getDatabaseId());
		fileQueue.sendMessage(message);
	}
	
}
