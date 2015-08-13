package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.losandes.utils.VariableManager;

public class AbstractGrailsCommunicator{
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
		
	/**
	 * Sends a new message to a given server service with a group of parameters
	 * @param serviceName exact service name to be called
	 * @param params params requested by the server
	 * @return true if the message was sent or false otherwise 
	 */
	
	public static boolean pushInfo(String serviceName,Object...params){
		final String serverUrl=VariableManager.global.getStringValue("SERVER_URL");
		String urlParams=null;
		for(int e=0,i=params.length;e<i;e+=2)urlParams=(urlParams==null?"?":(urlParams+"&"))+params[e]+"="+params[e+1];
		try {
			URL url=new URL((serverUrl+"/"+serviceName+urlParams).replace(" ","%20"));
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.connect();
			BufferedReader br=new BufferedReader(new InputStreamReader(http.getInputStream()));
			for(;br.readLine()!=null;);
			http.disconnect();
		} catch (IOException e){
			System.out.println(" Error en: "+serverUrl+"/"+serviceName+urlParams+" "+e.getMessage());
			return false;
		}
		return true;
	}
}