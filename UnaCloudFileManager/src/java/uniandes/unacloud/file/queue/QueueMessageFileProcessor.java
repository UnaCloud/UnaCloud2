package uniandes.unacloud.file.queue;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.share.db.ImageManager;
import uniandes.unacloud.share.db.PlatformManager;
import uniandes.unacloud.share.db.StorageManager;
import uniandes.unacloud.share.queue.messages.QueueMessage;
import uniandes.unacloud.share.queue.QueueReader;
import uniandes.unacloud.share.queue.messages.MessageCreateCopyFromPublic;
import uniandes.unacloud.share.queue.messages.MessageDeleteUser;
import uniandes.unacloud.share.queue.messages.MessageIdOfImage;
import uniandes.unacloud.share.entities.PlatformEntity;
import uniandes.unacloud.share.entities.RepositoryEntity;
import uniandes.unacloud.share.entities.ImageEntity;
import uniandes.unacloud.share.enums.UserStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.db.UserManager;
import uniandes.unacloud.file.db.ImageFileManager;
import uniandes.unacloud.file.db.entities.UserEntity;
import uniandes.unacloud.file.db.entities.ImageFileEntity;

/**
 * Class to process messages in queue. This messages represent tasks to manage files from web application
 * @author CesarF
 *
 */
public class QueueMessageFileProcessor implements QueueReader{
		
	private ExecutorService threadPool;

	public QueueMessageFileProcessor(int threads) {
		threadPool=Executors.newFixedThreadPool(threads);
	}
	
	@Override
	public void processMessage(QueueMessage message) {
		System.out.println("Receives message "+message.getType());
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
					MessageIdOfImage messageId = new MessageIdOfImage(message);
					Long imageId = messageId.getIdImage();
					
					ImageFileEntity image = ImageFileManager.getImageWithFile(imageId, ImageEnum.IN_QUEUE,false,false,con);
					if(image!=null){
						if(!image.isPublic()){
							RepositoryEntity main = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
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
							ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null,null, null, change, null, null, null,null),false,con, false);						
						}else{
							ImageManager.setImage(new ImageEntity(image.getId(), null, null, ImageEnum.AVAILABLE, null),con);
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
	//TODO: remove platform validation because it is not necessary, use main file in public image
	private void createPrivateImage(QueueMessage message){
		threadPool.submit(new MessageProcessor(message) {			
			@Override
			protected void processMessage(QueueMessage message) throws Exception{
				try(Connection con = FileManager.getInstance().getDBConnection()){
					MessageCreateCopyFromPublic messageFromPublic = new MessageCreateCopyFromPublic(message);
					Long imageId = messageFromPublic.getIdImage();
					Long publicImageId = messageFromPublic.getIdPublicImage();
					
					ImageFileEntity publicImage = ImageFileManager.getImageWithFile(publicImageId, ImageEnum.AVAILABLE, false,false,con);
					ImageFileEntity privateImage = ImageFileManager.getImageWithFile(imageId, ImageEnum.IN_QUEUE, true,false,con);
					if(publicImage!=null&&privateImage!=null){
						if(publicImage.isPublic()){
							RepositoryEntity main = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
							File folder = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+publicImage.getName());
							if(folder.exists()){
								List<PlatformEntity>platforms = PlatformManager.getAll(con);
								String regex = "";
								for(PlatformEntity hv:platforms)regex+=".*"+hv.getExtension()+(platforms.indexOf(hv)<platforms.size()-1?"|":"");
								UserEntity user = UserManager.getUserWithRepository(privateImage.getOwner().getId(),con);
								String mainFile = null;
								for(File imagefile: folder.listFiles()){
									File newFile = new File(privateImage.getRepository().getRoot()+privateImage.getName()+"_"+user.getUsername()+File.separator+imagefile.getName());
									FileUtils.copyFile(imagefile, newFile);
									if(imagefile.getName().matches(regex)){
										mainFile = user.getRepository().getRoot()+privateImage.getName()+"_"+user.getUsername()+File.separator+newFile.getName();
									}							
								}
								ImageFileManager.setImageFile(new ImageFileEntity(privateImage.getId(), ImageEnum.AVAILABLE, null, null, null, null, null, mainFile, null,null),false,con, false);
							}else{
								ImageFileManager.setImageFile(new ImageFileEntity(publicImage.getId(), null, null, null, null, false, null, null, null,null),false,con, false);
								ImageFileManager.setImageFile(new ImageFileEntity(privateImage.getId(), ImageEnum.UNAVAILABLE, null, null, null, null, null, null, null,null),false,con, false);
							}
						}else{
							ImageFileManager.setImageFile(new ImageFileEntity(privateImage.getId(), ImageEnum.UNAVAILABLE, null, null, null, null, null, null, null,null),false,con, false);
						}
					}else{
						if(privateImage!=null){
							ImageFileManager.setImageFile(new ImageFileEntity(privateImage.getId(), ImageEnum.UNAVAILABLE, null, null, null, null, null, null, null,null),false,con, false);
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
					MessageIdOfImage messageId = new MessageIdOfImage(message);
					Long imageId = messageId.getIdImage();
					
					ImageFileEntity image = ImageFileManager.getImageWithFile(imageId, ImageEnum.IN_QUEUE,false,false,con);
					if(image!=null){
						try {
							File file = new java.io.File(image.getMainFile());
							if(file!=null){
								File dir = file.getParentFile();
								for(File f: dir.listFiles())
									System.out.println("Delete file: "+f.getAbsolutePath()+" "+f.delete());
								System.out.println("Delete file: "+dir.getAbsolutePath()+" "+dir.delete());
							}
						} catch (Exception e) {
							System.err.println("No delete original image files "+image.getMainFile());
						}
						try {
							if(image.isPublic()){
								RepositoryEntity main = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
								File folder = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);						
								if(folder.exists())System.out.println("Delete file: "+folder.getAbsolutePath()+" "+folder.delete());	
							}
						} catch (Exception e) {
							System.err.println("No delete public copy files "+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);
						}					
						ImageManager.deleteImage(new ImageEntity(image.getId(), null, null, ImageEnum.IN_QUEUE, null),con);
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
					MessageDeleteUser messageDelete = new MessageDeleteUser(message);
					Long userId = messageDelete.getIdUser();
					
					UserEntity user = UserManager.getUser(userId,con);
					if(user!=null&&user.getState().equals(UserStateEnum.DISABLE)){
						System.out.println("Delete user: "+user.getId());
						List<ImageFileEntity> images = ImageFileManager.getAllImagesByUser(user.getId(),con);
						for(ImageFileEntity image: images){
							if(image!=null){
								try {
									File file = new File(image.getMainFile());
									if(file!=null)System.out.println("Delete file: "+file.getParentFile().getAbsolutePath()+" "+file.getParentFile().delete());
								} catch (Exception e) {
									System.err.println("No delete original image files "+image.getMainFile());
								}
								try {
									if(image.isPublic()){
										RepositoryEntity main = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
										File folder = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);						
										if(folder.exists())System.out.println("Delete folder: "+folder.getAbsolutePath()+" "+folder.delete());	
									}
								} catch (Exception e) {
									System.err.println("No delete public copy files "+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);
								}					
								ImageManager.deleteImage(new ImageEntity(image.getId(), null, null, ImageEnum.IN_QUEUE, null),con);
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
					MessageIdOfImage messageId = new MessageIdOfImage(message);
					Long imageId = messageId.getIdImage();
					
					ImageFileEntity image = ImageFileManager.getImageWithFile(imageId, ImageEnum.IN_QUEUE,false,false,con);
					if(image!=null){
						if(image.isPublic()){
							RepositoryEntity main = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY,con);
							File folder = new File(main.getRoot()+UnaCloudConstants.TEMPLATE_PATH+File.separator+image.getName()+File.separator);						
							if(folder.exists())
								System.out.println("Deletes folder: "+folder.getAbsolutePath()+" "+folder.delete());
						}
						ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null, null, null, false, null, null, null,null),false,con, false);
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
