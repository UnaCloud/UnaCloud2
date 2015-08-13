package com.losandes.utils;

import java.math.BigInteger;
import java.util.Random;

public class RandomUtils {
	
	private static Random r=new Random();
	
	/**
	 * Generates a random string
	 * @param lenght string length
	 */
	public static String generateRandomString(int lenght){
		byte[] buffer=new byte[128];
		r.nextBytes(buffer);
		String ret=new BigInteger(1,buffer).toString(32);
		if(ret.length()<=lenght)return ret;
		return ret.substring(0,lenght);
	}
	
}
