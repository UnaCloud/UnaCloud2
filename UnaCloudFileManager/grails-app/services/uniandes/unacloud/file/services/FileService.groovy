package uniandes.unacloud.file.services

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.Connection
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.internal.compiler.flow.FinallyFlowContext;
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import sun.rmi.log.ReliableLog.LogFile;
import uniandes.unacloud.common.utils.FileConverter;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.share.db.ImageManager;
import uniandes.unacloud.share.db.StorageManager;
import uniandes.unacloud.share.db.entities.RepositoryEntity;
import uniandes.unacloud.share.db.entities.ImageEntity;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.utils.file.FileProcessor;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.db.UserManager;
import uniandes.unacloud.file.db.ImageFileManager;
import uniandes.unacloud.file.db.entities.UserEntity
import uniandes.unacloud.file.db.entities.ImageFileEntity;
import uniandes.unacloud.file.net.torrent.TorrentTracker;
import grails.transaction.Transactional

/**
 * This service contains all methods to manage files: saves files for a new image or update files for a current image.
 * This class not use hibernate connection to database, it uses UnaCloud pool database connection library
 * @author CesarF
 *
 */
class FileService implements ApplicationContextAware { 
	
	/**
	 * Application context
	 */
     ApplicationContext applicationContext 

	/**
	 * Saves files in repository
	 * All files must be valid (extension file)
	 * @param files
	 * @param token
	 * @return boolean, true if image was copy to file repository or not.
	 */
    def upload(files, String token) {
		boolean copy = false;
		try {
			RepositoryEntity main = null;
			UserEntity user = null;
			Connection con = null;
			ImageFileEntity image = null;
			try {
				con = FileManager.getInstance().getDBConnection();
				image = ImageFileManager.getImageWithFile(token, con)
				if (image) {
					main = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con);
					user = UserManager.getUser(image.getOwner().getId(), con)
				}				
			} catch (Exception e) {
				e.printStackTrace()
			} finally {
				if (con != null)
					con.close()
			}		
			
			if (image) {
				println 'Image: ' + image
				boolean isValid = true;
				files.each {
					def fileName = it.getOriginalFilename().trim()
					if (!image.getPlatform().validatesExtension(fileName))
						isValid = false;
				}
				
				if (!isValid)
					return null			
				if (image.isPublic()) {
					File file = new File(main.getRoot() + UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName());
					if (file.exists()) {
						image.setPublic(false)
						copy = false;
					} else 
						copy = true
				}
				
				Long sizeImage = 0;
				files.each {
					
					def fileName = it.getOriginalFilename()
					File file = new File(image.getRepository().getRoot() + image.getName() + "_" + user.getUsername() + File.separator + fileName)
					file.mkdirs()
					it.transferTo(file)
					
					if (fileName.matches(".*" + image.getPlatform().getExtension()))
						image.setMainFile(file.getAbsolutePath())
				
					sizeImage += it.getSize()
				}
					
				try {
					con = FileManager.getInstance().getDBConnection();
					ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.PROCESSING, null, null, null, image.isPublic(), sizeImage, image.getMainFile(), null, null), false, con, true)
				} catch (Exception e) {
					e.printStackTrace()
				} finally {
					if (con != null)
						con.close()
				}		
				shareFile(image, main)
			}
		} catch(Exception e){
			e.printStackTrace()
		}		
		return copy;
	}
	
	/**
	 * Updates image files in repository
	 * @param files list of files
	 * @param token to query image
	 */
	
	def updateFiles(files, String token) {
		try {
			RepositoryEntity main = null;
			UserEntity user = null;
			Connection con = null;
			ImageFileEntity image = null;
			try {
				con = FileManager.getInstance().getDBConnection();
				image = ImageFileManager.getImageWithFile(token, con)
				if (image) {
					main = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con);
					user = UserManager.getUser(image.getOwner().getId(), con)
				}
			} catch (Exception e) {
				e.printStackTrace()
			} finally {
				if (con != null)
					con.close()
			}
			if (image) {
				println 'Main file: ' + image.getMainFile()
				files.each {
					def fileName = it.getOriginalFilename()
					if (!image.getPlatform().validatesExtension(fileName))
						return null
				}
				if (image.getMainFile() != null) {
					TorrentTracker.getInstance().removeTorrent(image.getFileConversor().getTorrentFile())
					FileProcessor.deleteFileSync(new java.io.File(image.getMainFile()).getParentFile().getAbsolutePath());
					if (image.isPublic())
						FileProcessor.deleteFileSync(main.getRoot() + UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName());					
				}
					
				def sizeImage = 0;
				files.each {
					def filename = it.getOriginalFilename()
					File file = new File(image.getRepository().getRoot() + image.getName() + "_" + user.getUsername() + File.separator + filename)
					file.mkdirs()
					it.transferTo(file)
					if (filename.matches(".*" + image.getPlatform().getExtension()))
						image.setMainFile(file.getAbsolutePath())				
					sizeImage += it.getSize()
				}				
				try {
					con = FileManager.getInstance().getDBConnection();
					println 'Update: ' + ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.PROCESSING, null, null, null, image.isPublic(), sizeImage, image.getMainFile(), null, null), true, con, true)
				} catch (Exception e) {
					e.printStackTrace()
				} finally {
					if (con != null)
						con.close()
				}
				shareFile(image, main)
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace()
		}		
		return false;	
	}
	
	/**
	 * Zip and share file in P2P protocol
	 * @author CesarF
	 *
	 */
	private void shareFile(final ImageFileEntity image, final RepositoryEntity main) {
		FileProcessor.zipFileAsync(image.getMainFile(), new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				Connection con = null;
				try  {
					println 'Finished process to zip file ' + image.getId()
					con = FileManager.getInstance().getDBConnection();
					File zip = (File) arg;
					if (zip != null) {
						println '\t publishing ' + image.getId()
						ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.AVAILABLE, null, null, null, image.isPublic(), null, image.getMainFile(), null, null), false, con, true)
						FileProcessor.deleteFilesFolder(new java.io.File(image.getMainFile()).getParentFile().getAbsolutePath(), ".*zip\$")
						if (image.isPublic()) {
							File newFolder = new File(main.getRoot() + UnaCloudConstants.TEMPLATE_PATH + File.separator + image.getName())
							newFolder.mkdirs()
							FileProcessor.copyFileSync(zip.getAbsolutePath(), newFolder.getAbsolutePath() + File.separator + zip.getName());
						}					
						FileConverter converter = new FileConverter(zip);
						TorrentTracker.getInstance().publishFile(converter);
					}					
					else {
						println '\t deleting ' + image.getId()
						FileProcessor.deleteFileSync(new java.io.File(image.getMainFile()).getParentFile().getAbsolutePath());
						ImageFileManager.setImageFile(new ImageFileEntity(image.getId(), ImageEnum.UNAVAILABLE, null, null, null, image.isPublic(), null, image.getMainFile(), null, null), false, con, true)
					}
				} catch (Exception e) {
					e.printStackTrace()
				} finally {
					if (con != null)
						con.close();
				}
			}
		})
	}
	
	/**
	 * This method is used to set system property with the current path for project.
	 * The purpose of this method is to set variable base in groovy environment to be used in java classes	
	 */
	def updateProperty() {
		System.setProperty(UnaCloudConstants.ROOT_PATH, applicationContext.getResource("/").getFile().getAbsolutePath())
	}
	
	/**
	 * 
	 * @param outputStream
	 * @param logName
	 * @return
	 */
	def getLog(OutputStream outputStream, String hostname, String logName) {
		Connection con
		RepositoryEntity entity
		try	{
			con = FileManager.getInstance().getDBConnection();
			entity = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con);
		}
		catch (Exception e) {
			e.printStackTrace()
		} 
		finally {
			if (con != null)
				con.close();
		}
		ZipOutputStream zos = new ZipOutputStream(outputStream);
		if (entity != null) {
			File logFile = new File(entity.getRoot() + File.separator + 
				UnaCloudConstants.LOGS_PATH + File.separator + hostname + File.separator + logName);	
			println '\t path' + logFile		
			if(logFile.exists()) {
				
				zos.putNextEntry(new ZipEntry(logName));
				Files.copy(logFile.toPath(), zos);
				zos.closeEntry();		
			}		
			else
				writeResponseError("File does not exist", zos);
		}
		else 
			writeResponseError("Error reading database", zos);			
		zos.close();
	}	
	
	private void writeResponseError(String message, ZipOutputStream zos) {
		PrintWriter pw = new PrintWriter(zos);
		zos.putNextEntry(new ZipEntry("Error.txt"));
		pw.println("Error in download process: " + message);
		pw.flush();
		zos.closeEntry();
	}
}
