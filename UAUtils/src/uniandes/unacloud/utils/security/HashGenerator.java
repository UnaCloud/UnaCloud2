package uniandes.unacloud.utils.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author Cesar
 * based on //http://howtodoinjava.com/core-java/io/how-to-generate-sha-or-md5-file-checksum-hash-in-java/
 */
public class HashGenerator {
	
	/**
	 * @param file
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws FileNotFoundException 
	 */	
	public static String generateChecksumMD5(File file) throws Exception {		
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return generateCheckSum(digest, file);
	}
	
	public static String generateChecksumSHA1(File file) throws Exception {		
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        return generateCheckSum(digest, file);
	}
	
	public static String generateCheckSum(MessageDigest digest, File file) throws Exception {
		//Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);
         
        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0; 
          
        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };
         
        //close the stream; We don't need it now.
        fis.close();
         
        //Get the hash's bytes
        byte[] bytes = digest.digest();
         
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bytes.length ; i++)
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
              
        //return complete hash
        return sb.toString();
	}

}
