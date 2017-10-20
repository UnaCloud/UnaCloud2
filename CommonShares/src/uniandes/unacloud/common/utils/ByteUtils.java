/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uniandes.unacloud.common.utils;

import java.text.DecimalFormat;

/**
 * Class containing utilities to data conversion
 * @author Clouder
 * @author CesarF added some methods
 */
public class ByteUtils {

    /**
     * Converts a long number to a byte array
     * @param number
     * @return bytes as long value
     */
    public static byte[] longToBytes(long number) {
        byte[] ret = new byte[8];
        ret[0] = (byte) ((number >>> 8 * 7) & 0xFF);
        ret[1] = (byte) ((number >> 8 * 6) & 0xFF);
        ret[2] = (byte) ((number >> 8 * 5) & 0xFF);
        ret[3] = (byte) ((number >> 8 * 4) & 0xFF);
        ret[4] = (byte) ((number >>> 8 * 3) & 0xFF);
        ret[5] = (byte) ((number >> 8 * 2) & 0xFF);
        ret[6] = (byte) ((number >> 8 * 1) & 0xFF);
        ret[7] = (byte) (number & 0xFF);
        return ret;
    }

    /**
     * Converts a byte array to a long number
     * @param bytes
     * @return long  as bytes value
     */
    public static long bytesToLong(byte[] bytes) {
        long n = ((long) (bytes[7])) & 0xFFL;
        n |= (((long) bytes[6]) << 8) & 0xFF00L;
        n |= (((long) bytes[5]) << 16) & 0xFF0000L;
        n |= (((long) bytes[4]) << 24) & 0xFF000000L;
        n |= (((long) bytes[3]) << 32) & 0xFF00000000L;
        n |= (((long) bytes[2]) << 40) & 0xFF0000000000L;
        n |= (((long) bytes[1]) << 48) & 0xFF000000000000L;
        n |= (((long) bytes[0]) << 56) & 0xFF00000000000000L;
        return n;
    }

    /**
     * Transform a quantity of bytes in a String with a determinate measurement unit
     * @param bytes to make conversion
     * @return String wish represent bytes in a determinate measurement unit
     */
    public static String conversionUnitBytes (long bytes) {
    	DecimalFormat df = new DecimalFormat("#.00");
    	double diskSize = bytes / 1024;
		if (diskSize > 1) {
			if (diskSize / 1024 > 1 ) {				
				diskSize = diskSize / 1024;
				if (diskSize / 1024 > 1)
					return df.format(diskSize/1024) + " GB";
				else 
					return df.format(diskSize) + " MB";
			}				
			else return df.format(diskSize) + " KB";		
		}
		else return df.format(bytes) + " Bytes";
    }
}
