package com.losandes.utils;

/**
 * Class to get variables from current system. 
 * @author CesarF
 *
 */
public class MySystem {

    
    static String hostname=null;
    /**
     * Responsible for obtaining  hostname
     * @return hostname
     */
    public static String getHostname() {
    	if(hostname!=null)return hostname;
    	hostname=LocalProcessExecutor.executeCommandOutput("hostname").trim();
    	return hostname;
    }

}
