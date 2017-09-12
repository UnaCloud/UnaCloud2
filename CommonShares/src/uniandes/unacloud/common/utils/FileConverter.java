package uniandes.unacloud.common.utils;

import java.io.File;
import java.io.Serializable;

/**
 * Utility class to get zip and torrent file from executable one 
 * @author CesarF
 *
 */
public class FileConverter implements Serializable {
	
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 2866756708758378981L;
	
	/**
	 * Zip extension
	 */
	public static String ZIP_EXTENSION = ".zip";
	
	/**
	 * Torrent extension
	 */
	public static String TORRENT_EXTENSION = ".zip.torrent";
	
	/**
	 * Executable file path
	 */
	private String filePath;
	
	/**
	 * Creates a file converter from an executable path
	 * @param filePath executable file path
	 */
	public FileConverter(String executablePath) {
		this.filePath = executablePath;
	}
	
	/**
	 * Creates a file converter from a zip file
	 * @param zipFile
	 */
	public FileConverter(File zipFile) {
		filePath = zipFile.getAbsolutePath().replace(".zip", "");
	}
	
	/**
	 * Return file path
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * returns executable file
	 * @return
	 */
	public File getExecutableFile() {
		return new File(filePath);
	}
	
	/**
	 * returns zip file
	 * @return
	 */
	public File getZipFile() {
		return new File(filePath + ZIP_EXTENSION);
	}
	
	/**
	 * returns torrent file
	 * @return
	 */
	public File getTorrentFile() {
		return new File(filePath + TORRENT_EXTENSION );
	}

}
