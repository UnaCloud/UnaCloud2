/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.losandes.utils;

/**
 * Cladd containing utilities to data convertion
 * @author Clouder
 */
public class ByteUtils {

    /**
     * Converts a long number to a byte array
     * @param number
     * @return
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
     * @return
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


}
