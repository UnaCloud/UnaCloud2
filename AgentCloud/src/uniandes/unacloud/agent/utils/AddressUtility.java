/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uniandes.unacloud.agent.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * to manage address for physical machine
 * @author Clouder
 */
public class AddressUtility {
	
	private String ip;
	
	private String netmask;
    
    private String network;
    
    private String broadcast;
    
    private String gateway;
    
    /**
     * Class constructor
     * @param ip physical machine IP address
     * @param netmask IP's netmask
     */
    public AddressUtility(String ip, String netmask) {
        this.ip = ip;
        this.netmask = netmask;
        String[] d = ip.split("\\.");
        String[] nm = netmask.split("\\.");
        if( d.length == 4) {
            short[] dir = new short[4];
            for (int e = 0; e < 4; e++) 
            	dir[e] = Short.parseShort(d[e]);
            short[] netm = new short[4];
            for (int e = 0; e < 4; e++)
            	netm[e] = Short.parseShort(nm[e]);

            short[] network = new short[4],
            		broadcast = new short[4],
            		gateway = new short[4];
            for (int e = 0; e < 4; e++) {
                gateway[e] = (short) (dir[e]&netm[e]);
                network[e] = (short) (dir[e]&netm[e]);
                broadcast[e] = (short) (((dir[e]&netm[e])|(-netm[e]-1))&0xFF);
            }
            gateway[3]++;
            this.network = "" + network[0];
            this.broadcast = "" + broadcast[0];
            this.gateway = "" + gateway[0];
            for (int e = 1; e < 4; e++) {
                this.network += "." + network[e];
                this.broadcast += "." + broadcast[e];
                this.gateway += "." + gateway[e];
            }
        }
    }
    
    /**
     * Gets broadcast
     * @return broadcast
     */
    public String getBroadcast() {
        return broadcast;
    }

    /**
     * gets gateway
     * @return gateway
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * Gets IP address
     * @return ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Gets network mask
     * @return mask
     */
    public String getNetmask() {
        return netmask;
    }

    /**
     * Gets network 
     * @return address for network
     */
    public String getNetwork() {
        return network;
    }
    
    /**
     * Selects the default network interface
     * @return network interface
     */
    public static NetworkInterface getDefaultNetworkInterface() {
    	try{
    		Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
    		while (networks.hasMoreElements()) {
    			NetworkInterface ni = networks.nextElement();
    			Enumeration<InetAddress> addresses = ni.getInetAddresses();
    			boolean hasAddress = false;
    			while (addresses.hasMoreElements()) {
    				InetAddress ia = addresses.nextElement();
    				if (!(ia.isAnyLocalAddress() || ia.isLinkLocalAddress() 
    						|| ia.isLoopbackAddress() || ia.isSiteLocalAddress() 
    						|| ni.isPointToPoint())) {
    					if (!hasAddress){
    						return ni;
    					}
    				}
    			}
    		}
    	}catch(Exception ex){
    		
    	}
		return null;
	}
}
