package uniandes.unacloud.file.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Observer;

import org.apache.commons.lang.NullArgumentException;

import uniandes.unacloud.common.utils.Zipper;

/**
 * Class responsible to manages file processes: copy, delete and zip
 * @author CesarF
 *
 */
public class FileProcessor {
	
	/**
	 * Zip file in a concurrent way using a thread
	 * @param fileName name of file to be zipped
	 * @param observer to return result when process finish
	 */
	public static void zipFileAsync(final String fileName, final Observer observer) {			
		new Thread() {
			@Override
			public void run() {
				try {
					File zip = zipFileSync(fileName);
					observer.update(null, zip);
				} catch (Exception e) {
					e.printStackTrace();
					observer.update(null, null);
				}				
			};
		}.start();		
	}
	
	/**
	 * Zip file in a Synchronized way and return new zip file
	 * @param fileName name of file
	 * @return zip file
	 * @throws Exception in case zipping error
	 */
	public static File zipFileSync(String fileName) throws Exception {
		File zipParent = new File(fileName).getParentFile();
		File zip = new File(fileName + ".zip");
		Zipper.zipIt(zip, zipParent);
		return zip;
	}
	
	/**
	 * Deletes file in a concurrent way using a thread
	 * @param fileName name of file o directory to be deleted
	 * @param observer to return result when process finish
	 */
	public static void deleteFileAsync(final String fileName, final Observer observer) {			
		new Thread() {
			@Override
			public void run() {
				try {
					Boolean aws = deleteFileSync(fileName);
					observer.update(null, aws);
				} catch (Exception e) {
					e.printStackTrace();
					observer.update(null, false);
				}				
			};
		}.start();		
	}
	
	/**
	 * Deletes file in a Synchronized way and return process result
	 * @param pathFile  name of file o directory to be deleted
	 * @return true in case file has been deleted, false in otherwise
	 * @throws Exception 
	 */
	public static boolean deleteFileSync(String pathFile) throws Exception{
		File file = new File(pathFile);
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File f: file.listFiles())
					if (f.isDirectory()) 
						deleteFileSync(f.getAbsolutePath());
					else 
						System.out.println("Deletes file: " + f.getAbsolutePath() + " " + f.delete());
				System.out.println("Deletes file: " + file.getAbsolutePath() + " " + file.delete());
			}
			else 
				System.out.println("Deletes file: " + file.getAbsolutePath() + " " + file.delete());
			return true;
		}
		return false;
	}

	/**
	 * Creates a new file based in original one
	 * @param pathOriginal where is stored original file
	 * @param pathCopy where will be stored copy file
	 * @return new file
	 * @throws Exception
	 */
	public static File copyFileSync(String pathOriginal, String pathCopy) throws Exception{
		final byte[] buffer = new byte[1024 * 100];
		File original = new File(pathOriginal);
		File copy = new File(pathCopy);
		if (!original.exists()) 
			throw new NullArgumentException("Original File does not exist");
		try (FileInputStream streamTemp = new FileInputStream(original); FileOutputStream ouFile = new FileOutputStream(copy)) {
			for (int n; (n = streamTemp.read(buffer)) != -1;)
				ouFile.write(buffer, 0, n);																						
		}	
		return copy;
	}
}
