package uniandes.unacloud.queue;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import com.losandes.utils.UnaCloudConstants;

import unacloud.share.db.HypervisorManager;
import unacloud.share.db.RepositoryManager;
import unacloud.share.db.VirtualImageManager;
import unacloud.share.queue.QueueMessage;
import unacloud.share.queue.QueueReader;
import unacloud.share.entities.HypervisorEntity;
import unacloud.share.entities.RepositoryEntity;
import unacloud.share.entities.VirtualMachineImageEntity;
import unacloud.share.enums.UserStateEnum;
import unacloud.share.enums.VirtualMachineImageEnum;
import uniandes.unacloud.FileManager;
import uniandes.unacloud.MessageProcessor;
import uniandes.unacloud.db.UserManager;
import uniandes.unacloud.db.VirtualMachineImageManager;
import uniandes.unacloud.db.entities.UserEntity;
import uniandes.unacloud.db.entities.VirtualImageFileEntity;

/**
 * Class to process messages sent to manage files from web application
 * @author CesarF
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
			createPrivateImage(message);
			break;
		case DELETE_IMAGE:
			deleteImage(message);
			break;
		case DELETE_PUBLIC_IMAGE:	
			deletePublicImage(message);
			break;
		case DELETE_USER:		
			deleteUser(message);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Creates a copy from a current private image in a public folder
	 * @param message
	 */
	private void createPublicImage(QueueMessage message) {
		threadPool.submit(new MessageProcessor(message) {			
			@Override
			protected void processMessage(QueueMessage message) throws Exception{
				try(Connection con = FileManager.getInstance().getDBConnection()){
					Long imageId = Long.parseLong(message.getMessageParts()[0]);
					VirtualImageFileEntity image = VirtualMachineImageManager.getVirtualImageWithFile(imageId, VirtualMachineImageEnum.IN_QUEUE,false,false,con);
					if(image!=null){
						if(!image.isPublic()){
							RepositoryEntity main = RepositoryManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
							File file = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName());
							System.out.println("Change to public "+image.getMainFile());
							boolean change = false;
							if(!file.exists()){
								File folder = new File(image.getMainFile().substring(0, image.getMainFile().lastIndexOf(File.separator.toString())));
								for(File imagefile: folder.listFiles()){
									File newFile = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator+imagefile.getName());
									FileUtils.copyFile(imagefile, newFile);
								}
								change = true;
							}
							//TODO if there is a previous image doesn't override and should send a notification
							VirtualMachineImageManager.setVirtualMachineFile(new VirtualImageFileEntity(image.getId(), VirtualMachineImageEnum.AVAILABLE, null, null, change, null, null, null,null),false,con);						
						}else{
							VirtualImageManager.setVirtualMachine(new VirtualMachineImageEntity(image.getId(), null, null, VirtualMachineImageEnum.AVAILABLE, null),con);
						}
					}
				} catch (Exception e) {	e.printStackTrace();}
			}			
			@Override
			protected void processError(Exception e) {
				//TODO notification
			}
		});	
	}
	
	/**
	 * Creates a private image copy from a public one
	 * @param message
	 */
	//TODO: remove hypervisor validation because it is not necessary, use main file in public image
	private void createPrivateImage(QueueMessage message){
		threadPool.submit(new MessageProcessor(message) {			
			@Override
			protected void processMessage(QueueMessage message) throws Exception{
				try(Connection con = FileManager.getInstance().getDBConnection()){
					Long imageId = Long.parseLong(message.getMessageParts()[0]);
					Long publicImageId = Long.parseLong(message.getMessageParts()[1]);
					VirtualImageFileEntity publicImage = VirtualMachineImageManager.getVirtualImageWithFile(publicImageId, VirtualMachineImageEnum.AVAILABLE, false,false,con);
					VirtualImageFileEntity privateImage = VirtualMachineImageManager.getVirtualImageWithFile(imageId, VirtualMachineImageEnum.IN_QUEUE, true,false,con);
					if(publicImage!=null&&privateImage!=null){
						if(publicImage.isPublic()){
							RepositoryEntity main = RepositoryManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
							File folder = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+publicImage.getName());
							if(folder.exists()){
								List<HypervisorEntity>hypervisors = HypervisorManager.getAllHypervisors(con);
								String regex = "";
								for(HypervisorEntity hv:hypervisors)regex+=".*"+hv.getExtension()+(hypervisors.indexOf(hv)<hypervisors.size()-1?"|":"");
								UserEntity user = UserManager.getUserWithRepository(privateImage.getOwner().getId(),con);
								String mainFile = null;
								for(File imagefile: folder.listFiles()){
									File newFile = new File(privateImage.getRepository().getRoot()+privateImage.getName()+"_"+user.getUsername()+File.separator+imagefile.getName());
									FileUtils.copyFile(imagefile, newFile);
									if(imagefile.getName().matches(regex)){
										mainFile = user.getRepository().getRoot()+privateImage.getName()+"_"+user.getUsername()+File.separator+newFile.getName();
									}							
								}
								VirtualMachineImageManager.setVirtualMachineFile(new VirtualImageFileEntity(privateImage.getId(), VirtualMachineImageEnum.AVAILABLE, null, null, null, null, mainFile, null,null),false,con);
							}else{
								VirtualMachineImageManager.setVirtualMachineFile(new VirtualImageFileEntity(publicImage.getId(), null, null, null, false, null, null, null,null),false,con);
								VirtualMachineImageManager.setVirtualMachineFile(new VirtualImageFileEntity(privateImage.getId(), VirtualMachineImageEnum.UNAVAILABLE, null, null, null, null, null, null,null),false,con);
							}
						}else{
							VirtualMachineImageManager.setVirtualMachineFile(new VirtualImageFileEntity(privateImage.getId(), VirtualMachineImageEnum.UNAVAILABLE, null, null, null, null, null, null,null),false,con);
						}
					}else{
						if(privateImage!=null){
							VirtualMachineImageManager.setVirtualMachineFile(new VirtualImageFileEntity(privateImage.getId(), VirtualMachineImageEnum.UNAVAILABLE, null, null, null, null, null, null,null),false,con);
						}
					}

				} catch (Exception e) {	e.printStackTrace();}
			}			
			@Override
			protected void processError(Exception e) {
				//TODO notification
			}
		});	
	}
	
	/**
	 * Delete files and image entity
	 * @param message
	 */
	private void deleteImage(QueueMessage message){
		threadPool.submit(new MessageProcessor(message) {			
			@Override
			protected void processMessage(QueueMessage message) throws Exception{
				try(Connection con = FileManager.getInstance().getDBConnection()){
					Long imageId = Long.parseLong(message.getMessageParts()[0]);
					VirtualImageFileEntity image = VirtualMachineImageManager.getVirtualImageWithFile(imageId, VirtualMachineImageEnum.IN_QUEUE,false,false,con);
					if(image!=null){
						try {
							File file = new java.io.File(image.getMainFile());
							if(file!=null)System.out.println("Delete file: "+file.getParentFile().getAbsolutePath()+" "+file.getParentFile().delete());
						} catch (Exception e) {
							System.err.println("No delete original image files "+image.getMainFile());
						}
						try {
							if(image.isPublic()){
								RepositoryEntity main = RepositoryManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
								File folder = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);						
								if(folder.exists())System.out.println("Delete file: "+folder.getAbsolutePath()+" "+folder.delete());	
							}
						} catch (Exception e) {
							System.err.println("No delete public copy files "+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);
						}					
						VirtualImageManager.deleteVirtualMachineImage(new VirtualMachineImageEntity(image.getId(), null, null, VirtualMachineImageEnum.IN_QUEUE, null),con);
					}
				} catch (Exception e) {	e.printStackTrace();}
			}			
			@Override
			protected void processError(Exception e) {
				//TODO notification
			}
		});	
	}
	
	/**
	 * Delete files by user, image entities and user entity
	 * @param message
	 */
	private void deleteUser(QueueMessage message) {
		threadPool.submit(new MessageProcessor(message) {			
			@Override
			protected void processMessage(QueueMessage message) throws Exception{
				try(Connection con = FileManager.getInstance().getDBConnection()){
					Long userId = Long.parseLong(message.getMessageParts()[0]);
					UserEntity user = UserManager.getUser(userId,con);
					System.out.println("update");
					if(user!=null&&user.getState().equals(UserStateEnum.DISABLE)){
						List<VirtualImageFileEntity> images = VirtualMachineImageManager.getAllVirtualMachinesByUser(user.getId(),con);
						for(VirtualImageFileEntity image: images){
							if(image!=null){
								try {
									File file = new File(image.getMainFile());
									if(file!=null)System.out.println("Delete file: "+file.getParentFile().getAbsolutePath()+" "+file.getParentFile().delete());
								} catch (Exception e) {
									System.err.println("No delete original image files "+image.getMainFile());
								}
								try {
									if(image.isPublic()){
										RepositoryEntity main = RepositoryManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
										File folder = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);						
										if(folder.exists())System.out.println("Delete folder: "+folder.getAbsolutePath()+" "+folder.delete());	
									}
								} catch (Exception e) {
									System.err.println("No delete public copy files "+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);
								}					
								VirtualImageManager.deleteVirtualMachineImage(new VirtualMachineImageEntity(image.getId(), null, null, VirtualMachineImageEnum.IN_QUEUE, null),con);
							}
						}
						UserManager.deleteUser(user,con);
					}
				} catch (Exception e) {	e.printStackTrace();}
			}			
			@Override
			protected void processError(Exception e) {
				//TODO notification
			}
		});	
	}

	/**
	 * Deletes files from a public image
	 * @param message
	 */
	private void deletePublicImage(QueueMessage message) {
		threadPool.submit(new MessageProcessor(message) {			
			@Override
			protected void processMessage(QueueMessage message) throws Exception{
				try(Connection con = FileManager.getInstance().getDBConnection()){
					Long imageId = Long.parseLong(message.getMessageParts()[0]);
					VirtualImageFileEntity image = VirtualMachineImageManager.getVirtualImageWithFile(imageId, VirtualMachineImageEnum.IN_QUEUE,false,false,con);
					if(image!=null){
						if(image.isPublic()){
							RepositoryEntity main = RepositoryManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
							File folder = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);						
							if(folder.exists())System.out.println("Delete folder: "+folder.getAbsolutePath()+" "+folder.delete());
						}
						VirtualMachineImageManager.setVirtualMachineFile(new VirtualImageFileEntity(image.getId(), VirtualMachineImageEnum.AVAILABLE, null, null, false, null, null, null,null),false,con);
					}
				} catch (Exception e) {	e.printStackTrace();}
			}			
			@Override
			protected void processError(Exception e) {
				//TODO notification
			}
		});	
	}

}
