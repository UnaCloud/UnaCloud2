package uniandes.unacloud.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Utility Class used to load variables from a properties file.
 * Typically used to load configuration values
 * @author CesarF
 *
 */
public class ConfigurationReader {
	
	/**
	 * Data structure to save variables
	 */
	private TreeMap<String, String> values = new TreeMap<String, String>();
	
	private String fileName;
	
	/**
	 * Constructor to create reader, read file and load variables in tree.
	 * filename must be a properties file
	 * @param filename
	 * @param variables
	 * @throws IOException in case file doesn't exists or can't be read
	 */
	public ConfigurationReader(String filename, String[] variables) throws IOException {
		this.fileName = filename;
		
		Properties prop = new Properties();
		InputStream inputStream = new FileInputStream(filename);
		prop.load(inputStream);
		for (String value : variables) {
			String data = prop.getProperty(value);			
			if (data != null)
				values.put(value, data);
		}
		inputStream.close();
	}	
	
	/**
	 * Constructor to create reader, read file and load all variables in tree.
	 * filename must be a properties file
	 * @param filename
	 * @throws IOException 
	 */
	public ConfigurationReader(String filename) throws IOException{
		this.fileName = filename;
		{
			File f = new File(filename);
			if (!f.exists()) return;
		}
		Properties prop = new Properties();
		InputStream inputStream = new FileInputStream(filename);
		prop.load(inputStream);
		for (String key:prop.stringPropertyNames()) {
			String data = prop.getProperty(key);
			if (data != null)
				values.put(key, data);
		}
		inputStream.close();
	}
	/**
	 * Saves values in tree map to a properties file
	 * @throws IOException 
	 */
	public void saveConfiguration() throws IOException { 
		Properties prop = new Properties();
		OutputStream out = new FileOutputStream(this.fileName);
		for (String key : values.keySet()) 
			prop.setProperty(key, values.get(key));		
		prop.store(out, null);
	}
	/**
	 * Returns a variable requested by parameters as String, null: value is not in file
	 * @param nameVariable
	 * @return variable as String value
	 */
	public String getStringVariable(String nameVariable) {
		return values.get(nameVariable);
	}
	
	/**
	 * Returns a variable requested by parameters as Integer, null: values is not in file
	 * @param nameVariable
	 * @return variable as Integer value
	 * @throws Exception in case variable is not int
	 */
	public Integer getIntegerVariable(String nameVariable) throws Exception {
		return Integer.parseInt(values.get(nameVariable));
	}
	
	/**
	 * Returns a variable requested by parameters as Long, null: values is not in file
	 * @param nameVariable
	 * @return variable as Long value
	 * @throws Exception in case variable is not long
	 */
	public Long getLongVariable(String nameVariable) throws Exception {
		return Long.parseLong(values.get(nameVariable));
	}
	
	/**
	 * Sets String variable in values tree
	 * @param key
	 * @param variable
	 */
	public void setStringVariable(String key, String variable) {
		values.put(key, variable);
	}
	
	/**
	 * Sets Long variable in values tree
	 * @param key
	 * @param variable
	 */
	public void setLongVariable(String key, Long variable) {
		values.put(key, variable + "");
	}

	/**
	 * Sets Integer variable in values tree
	 * @param key
	 * @param variable
	 */
	public void setIntegerVariable(String key, Integer variable) {
		values.put(key, variable + "");
	}
	
	/**
	 * Gets a variable from tree if variable doesn't exist set by other variable
	 * @param nameVariable search variable
	 * @param variableToSet variable to be save 
	 * @return variable in tree
	 */
	public String getSetStringValue(String nameVariable, String variableToSet) {
		String val = values.get(nameVariable);
		if (val == null) {
			values.put(nameVariable, variableToSet);
			val = variableToSet;
		}
		return val;
	}
}
