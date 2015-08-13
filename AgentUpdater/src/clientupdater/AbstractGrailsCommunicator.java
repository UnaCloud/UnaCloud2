package clientupdater;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AbstractGrailsCommunicator{
	public static String getVersion(){
		final String serverUrl=VariableManager.getStringValue("SERVER_URL");
		String ret="";
		try {
			URL url=new URL(serverUrl+"/UnaCloudServices/agentVersion");
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			try(BufferedReader br=new BufferedReader(new InputStreamReader(http.getInputStream()))){
				ret=br.readLine();
			}
			http.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	public static void getAgentZip(PrintWriter versionFile){
		final String serverUrl=VariableManager.getStringValue("SERVER_URL");
		try {
			URL url=new URL(serverUrl+"/UnaCloudServices/agent");
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			try(ZipInputStream zis=new ZipInputStream(http.getInputStream());){
				byte[] buffer=new byte[100*1024];
				for(ZipEntry ze;(ze=zis.getNextEntry())!=null;){
					versionFile.println(ze.getName());
					File output=new File(ze.getName());
					if(output.getParentFile()!=null)output.getParentFile().mkdirs();
					try(FileOutputStream fos=new FileOutputStream(output)){
						for(int n;(n=zis.read(buffer))!=-1;)fos.write(buffer,0,n);
					}
				}
			}
			http.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}