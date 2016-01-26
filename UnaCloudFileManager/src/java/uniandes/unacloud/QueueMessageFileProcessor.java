package uniandes.unacloud;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import db.VirtualImageManager;
import queue.QueueMessage;
import queue.QueueReader;
import unacloud.entities.VirtualMachineImage;
import unacloud.enums.VirtualMachineImageEnum;
import uniandes.unacloud.db.VirtualMachineImageManager;
import uniandes.unacloud.db.entities.Repository;
import uniandes.unacloud.db.entities.VirtualImageFile;

/**
 * Class to process messages sent to manage files
 * @author Cesar
 *
 */
public class QueueMessageFileProcessor implements QueueReader{
	
	private ExecutorService threadPool=Executors.newFixedThreadPool(5);

	@Override
	public void processMessage(QueueMessage message) {
		System.out.println("Receive message "+message.getType());
		switch (message.getType()) {
		case CREATE_PUBLIC_IMAGE:	
			createPublicImage(message);
			break;
		case CREATE_COPY_FROM_PUBLIC:			
			
			break;
		case DELETE_IMAGE:
			
			break;
		case DELETE_PUBLIC_IMAGE:	
			
			break;
		case DELETE_USER:		
			
			break;
		default:
			break;
		}
	}
	
	/**
	 * Copy a current private image to a public folder
	 * @param message
	 */
	private void createPublicImage(QueueMessage message) {
		try {
			Long imageId = Long.parseLong(message.getMessageParts()[0]);
			VirtualImageFile image = VirtualMachineImageManager.getVirtualImageWithFile(imageId, VirtualMachineImageEnum.IN_QUEUE);
			if(image!=null){
				if(!image.isPublic()){
					Repository main = VirtualMachineImageManager.getMainRepository();
					
				}else{
					VirtualImageManager.setVirtualMachine(new VirtualMachineImage(image.getId(), null, null, VirtualMachineImageEnum.AVAILABLE, null));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
