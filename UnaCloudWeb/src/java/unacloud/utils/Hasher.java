package unacloud.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Hasher {
	
	private static final String HASH_ALGORITHM = "SHA-256";

	public static String hashSha256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
		md.update(text.getBytes("UTF-8"));
		return Base64.encode(md.digest());		
	}
}
