package uniandes.unacloud.web.utils.java;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.RandomStringUtils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * Class used to create random hash, numbers and strings
 * @author CesarF
 *
 */
public class Hasher {
	
	private static final String charset = "abcdefghijklmnopqrstuvwxyz0123456789";
	
	private static final String HASH_ALGORITHM = "SHA-256";
	
	/**
	 * Method to create a hash based in a text using sha256
	 * @param text
	 * @return hash String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String hashSha256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
		md.update(text.getBytes("UTF-8"));
		return Base64.encode(md.digest());		
	}
	
	/**
	 * Using to create a randomString based in a length sent by user
	 * @param ln
	 * @return random string based in charset
	 */
	public static String randomString(int ln){
		return RandomStringUtils.random(ln, charset.toCharArray());
	}
}
