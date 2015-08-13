package com.losandes.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class responsible for manage system variables and stores its values
 */
public class VariableManager {
	
	public static final VariableManager global=new VariableManager("vars");
	public static final VariableManager local=new VariableManager("local");
	
	/**
     * File used to persist global variable values
     */
    private File configFile;
    
    /**
     * map used to save variables values. It is used to improve response time
     */
    private final Map<String, Object> map = new TreeMap<String, Object>();
    
    public VariableManager(String configFile){
		this.configFile = new File(configFile);
		init();
	}

	/**
     * Return the value of the string variable with the given key.
     * @param key The key that identify the variable
     * @return The value of the variable or null if there is no value for the given key
     */
    public synchronized String getStringValue(String key) {
    	Object c=map.get("String." + key);
    	if(c==null)return null;
    	return (String) c;
    }

    public synchronized void setStringValue(String key, String v) {
    	map.put("String." + key, v);
    	saveChanges();
    }
    
    /**
     * Returns the value stored on the manager, if it doesn't exists then creates a new one with the given value
     * @param key
     * @param v
     */
    public synchronized String getsetStringValue(String key, String v) {
    	if(map.containsKey("String."+key))return getStringValue(key);
    	setStringValue(key, v);
    	return v;
    }

    /**
     * Return the value of the int variable with the given key.
     * @param key The key that identify the variable
     * @return The value of the variable or null if there is no value for the given key
     */
    public synchronized int getIntValue(String key) {
    	Object c=map.get("Integer." + key);
    	if(c==null)return -1;
        return (Integer) c;
    }

    public synchronized void setIntValue(String key, int v) {
    	map.put("Integer." + key, v);
    	saveChanges();
    }
    
    /**
     * Return the value of the Boolean variable with the given key.
     * @param key The key that identify the variable
     * @return The value of the variable or null if there is no value for the given key
     */
    public synchronized boolean getBooleanValue(String key) {
    	Object r=map.get("Boolean." + key);
        return r!=null&&(Boolean) r;
    }

    public synchronized void setBooleanValue(String key, boolean v) {
    	map.put("Boolean." + key, v);
    	saveChanges();
    }

    /**
     * Stores the variables on the storage file.
     */
    private void saveChanges() {
        try(PrintWriter pw = new PrintWriter(configFile)){
            for (Map.Entry<String, Object> e : map.entrySet())pw.println(e.getKey() + "=" + e.getValue());
        } catch (Exception e) {
        }
    }

    /**
     * Inits this variable manager using the given file to manage its variables.
     * @param varsPath
     */
    private void init() {
        try(BufferedReader br = new BufferedReader(new FileReader(configFile));) {
            for (String h; (h = br.readLine()) != null;)processLine(map,h);
        } catch (Exception e) {
            System.err.println("El archivo vars no existe");
        }
    }
    private static void processLine(Map<String,Object> map,String line){
    	try{
    		String[] j = line.split("=");
    		if (j[0].startsWith("String."))map.put(j[0], j[1]);
            else if (j[0].startsWith("Integer."))map.put(j[0], Integer.parseInt(j[1]));
            else if (j[0].startsWith("Boolean."))map.put(j[0], Boolean.parseBoolean(j[1]));
    	}catch(Exception ex){
    		
    	}
    }
}
