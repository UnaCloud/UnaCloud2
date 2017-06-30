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
import uniandes.unacloud.share.db.entities.PlatformEntity;
import uniandes.unacloud.share.db.entities.RepositoryEntity;
import uniandes.unacloud.share.db.entities.ImageEntity;
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
public class QueueMessageFileProcessor implements QueueReader {
		
	/**
	 * Thread pool 
	 */
	private ExecutorService threadPool;
	
	/**
	 * Main repository for all images
	 */
	private RepositoryEntity mainRepo;

	/**
	 * Creates a queue processor to process messages to manage file
	 * @param threads
	 */
	public QueueMessageFileProcessor(int threads) {
		threadPool = Executors.newFixedThreadPool(threads);		
	}
	
	@Override
	public void processMessage(QueueMessage message) {
		System.out.println("Receives message " + message.getType());
		if (mainRepo == null) 
			initializeMainRepo();
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
	 * Initialize main repo to avoid extra queries to database
	 */
	private void initializeMainRepo() {
		try (Connection con = FileManager.getInstance().getDBConnection()) {			
			mainRepo = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con);
		} catch (Exception e) {	
			e.printStackTrace();
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
				
				MessageIdOfImage messageId = new MessageIdOfImage(message);
				Long imageId = messageId.getIdImage();
				ImageFileEntity image =  null;
				try (Connection con = FileManager.getInstance().getDBConnection()) {
					image = ImageFileManager.getImageWithFile(imageId, ImageEnum.IN_QUEUE, false, false, con);				
				} catch (Exception e) {	
					e.printStackTrace();
				}
					
				if (image != null) {
					boolean change = false;
					boolean isNotPublic = false;
					if (!image.isPublic()) {
						isNotPublic = true;
						File file = new File(mainRepo.getRoot() + UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName());
						System.out.println("Changes to public " + image.getMainFile());
						
						if (!file.exists()) {
							File folder = new File(image.getMainFile().substring(0, image.getMainFile().lastIndexOf(File.separator.toString())));
							for (File imagefile: folder.listFiles()) {
								File newFile = new File(mainRepo.getRoot() + UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName() + File.separator + imagefile.getName());
								FileUtils.copyFile(imagefile, newFile);
							}
							change = true;
						}
												
					} 
					try (Connection con = FileManager.getInstance().getDBConnection()) {
						if (isNotPublic)
							//TODO if there is a previous image doesn't override and should send a notification		
							ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null, null, null, change, null, null, null, null), false, con, false);				
						else
							ImageManager.setImage(new ImageEntity(image.getId(), null, null, ImageEnum.AVAILABLE, null), con);
						
					} catch (Exception e) {	
						e.printStackTrace();
					}
				}
				
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
	private void createPrivateImage(QueueMessage message) {
		threadPool.submit(new MessageProcessor(message) {			
			@Override
			protected void processMessage(QueueMessage message) throws Exception {
				
				MessageCreateCopyFromPublic messageFromPublic = new MessageCreateCopyFromPublic(message);
				Long imageId = messageFromPublic.getIdImage();
				Long publicImageId = messageFromPublic.getIdPublicImage();
				
				ImageFileEntity publicImage = null;
				ImageFileEntity privateImage = null;
				List<PlatformEntity> platforms = null;
				UserEntity user = null;
				try (Connection con = FileManager.getInstance().getDBConnection()) {					
					privateImage = ImageFileManager.getImageWithFile(imageId, ImageEnum.IN_QUEUE, true, false, con);
					if (privateImage != null) {
						platforms = PlatformManager.getAll(con);						
						user = UserManager.getUserWithRepository(privateImage.getOwner().getId(), con);
						publicImage = ImageFileManager.getImageWithFile(publicImageId, ImageEnum.AVAILABLE, false, false, con);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}	
									
				if (privateImage != null) {
					String mainFile = null;
					if (publicImage != null && publicImage.isPublic()) {
						File folder = new File(mainRepo.getRoot() + UnaCloudConstants.TEMPLATE_PATH + File.separator + publicImage.getName());
						//TODO add elimination if folder does not exists
						if (folder.exists()) {								
							String regex = "";
							for (PlatformEntity hv : platforms)
								regex += ".*" + hv.getExtension() + (platforms.indexOf(hv) < platforms.size() -1 ? "|" : "");
							
							for (File imagefile: folder.listFiles()) {
								File newFile = new File(privateImage.getRepository().getRoot() + privateImage.getName() + "_" + user.getUsername() + File.separator + imagefile.getName());
								FileUtils.copyFile(imagefile, newFile);
								if (imagefile.getName().matches(regex))
									mainFile = user.getRepository().getRoot() + privateImage.getName() + "_" + user.getUsername() + File.separator + newFile.getName();
							}							
						} 
					} 
					try (Connection con = FileManager.getInstance().getDBConnection()) {
						if (mainFile == null) {
							if (publicImage != null)
								ImageFileManager.setImageFile(new ImageFileEntity(publicImage.getId(), null, null, null, null, false, null, null, null, null), false, con, false);
							ImageFileManager.setImageFile(new ImageFileEntity(privateImage.getId(), ImageEnum.UNAVAILABLE, null, null, null, null, null, null, null, null), false, con, false);
						}
						else 
							ImageFileManager.setImageFile(new ImageFileEntity(privateImage.getId(), ImageEnum.AVAILABLE, null, null, null, null, null, mainFile, null, null), false, con, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} 
				
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
			protected void processMessage(QueueMessage message) throws Exception {
				MessageIdOfImage messageId = new MessageIdOfImage(message);
				Long imageId = messageId.getIdImage();
				ImageFileEntity image = null;
				try (Connection con = FileManager.getInstance().getDBConnection()) {					
					image = ImageFileManager.getImageWithFile(imageId, ImageEnum.IN_QUEUE, false, false, con);
				} catch (Exception e) {	
					e.printStackTrace();
				}
				if (image != null) {
					try {
						File file = new java.io.File(image.getMainFile());
						if (file != null) {
							File dir = file.getParentFile();
							for (File f: dir.listFiles())
								System.out.println("Deletes file: " + f.getAbsolutePath() + " " + f.delete());
							System.out.println("Deletes file: " + dir.getAbsolutePath() + " " + dir.delete());
						}
					} catch (Exception e) {
						System.err.println("original image files can't be deleted "+image.getMainFile());
					}
					try {
						if (image.isPublic()) {
							File folder = new File(mainRepo.getRoot() + UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName() + File.separator);						
							if (folder.exists())
								System.out.println("Delete file: " + folder.getAbsolutePath() + " " + folder.delete());	
						}
					} catch (Exception e) {
						System.err.println("public copy files can't be deleted " + UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName() + File.separator);
					}	
					try (Connection con = FileManager.getInstance().getDBConnection()) {					
						ImageManager.deleteImage(new ImageEntity(image.getId(), null, null, ImageEnum.IN_QUEUE, null), con);
					} catch (Exception e) {	
						e.printStackTrace();
					}					
				}
				
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
			protected void processMessage(QueueMessage message) throws Exception {
				
				MessageDeleteUser messageDelete = new MessageDeleteUser(message);
				Long userId = messageDelete.getIdUser();
				UserEntity user = null;
				List<ImageFileEntity> images = null;
				try (Connection con = FileManager.getInstance().getDBConnection()) {
					user = UserManager.getUser(userId, con);
					if (user != null) 
						images =  ImageFileManager.getAllImagesByUser(user.getId(), con);
				} catch (Exception e) {	
					e.printStackTrace();
				}
				
				if (user != null && user.getState().equals(UserStateEnum.DISABLE)) {
					System.out.println("Delete user: " + user.getId());
					if (images != null) {
						for (ImageFileEntity image : images) {						
							try {
								File file = new File(image.getMainFile());
								if (file != null) {
									File dir = file.getParentFile();
									for (File f : dir.listFiles())
										System.out.println("Deletes file: " + f.getAbsolutePath() + " " + f.delete());
									System.out.println("Deletes file: " + dir.getAbsolutePath() + " " + dir.delete());
								}									
							} catch (Exception e) {
								System.err.println("original image files can't be deleted " + image.getMainFile());
							}
							try {
								if (image.isPublic()) {
									File folder = new File(mainRepo.getRoot() + UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName() + File.separator);						
									if (folder.exists())
										System.out.println("Delete folder: " + folder.getAbsolutePath() + " " + folder.delete());	
								}
							} catch (Exception e) {
								System.err.println("public copy files can't be deleted  " + UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName() + File.separator);
							}					
							
						}
					}					
					try (Connection con = FileManager.getInstance().getDBConnection()) {						
						if (images != null) 
							for (ImageFileEntity image : images) 
								ImageManager.deleteImage(new ImageEntity(image.getId(), null, null, ImageEnum.IN_QUEUE, null), con);
						UserManager.deleteUser(user, con);
					} catch (Exception e) {	
						e.printStackTrace();
					}					
				}				
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
			protected void processMessage(QueueMessage message) throws Exception {
				MessageIdOfImage messageId = new MessageIdOfImage(message);
				Long imageId = messageId.getIdImage();
				ImageFileEntity image = null;
				try (Connection con = FileManager.getInstance().getDBConnection()) {
					image = ImageFileManager.getImageWithFile(imageId, ImageEnum.IN_QUEUE, false, false, con);
				} catch (Exception e) {	
					e.printStackTrace();
				}						
				if (image != null) {
					if (image.isPublic()) {
						File folder = new File(mainRepo.getRoot()+UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName() + File.separator);						
						if (folder.exists()) {
							for(File f : folder.listFiles())
								System.out.println("Deletes file: " + f.getAbsolutePath() + " " + f.delete());
							System.out.println("Deletes folder: " + folder.getAbsolutePath() + " " + folder.delete());
						}								
					}
					try (Connection con = FileManager.getInstance().getDBConnection()) {
						ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null, null, null, false, null, null, null, null), false, con, false);
					} catch (Exception e) {	
						e.printStackTrace();
					}					
				}				
			}			
			@Override
			protected void processError(Exception e) {
				//TODO notification
			}
		});	
	}

}
