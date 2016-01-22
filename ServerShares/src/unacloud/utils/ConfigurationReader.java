package unacloud.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Utility Class used to load variables from a properties file.
 * Typically used to load configuration values
 * @author Cesar
 *
 */
public class ConfigurationReader {
	
	/**
	 * Data structure to save variables
	 */
	private TreeMap<String, String> values = new TreeMap<String, String>();
	
	/**
	 * Constructor to create reader, read file and load variables in tree.
	 * filename must be a properties file
	 * @param filename
	 * @param variables
	 * @throws IOException in case file doesn't exists or can't be read
	 */
	public ConfigurationReader(String filename, String[] variables) throws IOException{
		Properties prop = new Properties();
		InputStream inputStream = new FileInputStream(filename);
		prop.load(inputStream);
		for(String value:variables){
			String data = prop.getProperty(value);
			if(data!=null)
				values.put(value, data);
		}
		inputStream.close();
	}	
	
	/**
	 * Return a variable requested by parameters as String, null: value is not in file
	 * @param nameVariable
	 * @return
	 */
	public String getStringVariable(String nameVariable){
		return values.get(nameVariable);
	}
	
	/**
	 * Return a variable requested by parameters as Integer, null: values is not in file
	 * @param nameVariable
	 * @return
	 * @throws Exception
	 */
	public Integer getIntegerVariable(String nameVariable) throws Exception{
		return Integer.parseInt(values.get(nameVariable));
	}
	
	/**
	 * Return a variable requested by parameters as Long, null: values is not in file
	 * @param nameVariable
	 * @return
	 * @throws Exception
	 */
	public Long getLongVariable(String nameVariable) throws Exception{
		return Long.parseLong(values.get(nameVariable));
	}

}
