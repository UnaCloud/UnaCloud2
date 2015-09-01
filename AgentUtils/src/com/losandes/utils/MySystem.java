package com.losandes.utils;

/**
 * 
 * @author CesarF
 * 
 * Class to represent variables from current system.
 *
 */
public class MySystem {

    
    static String hostname=null;
    /**
     * Responsible for obtaining the hostname
     * @return
     */
    public static String getHostname() {
    	if(hostname!=null)return hostname;
    	hostname=LocalProcessExecutor.executeCommandOutput("hostname").trim();
    	return hostname;
    }

}
