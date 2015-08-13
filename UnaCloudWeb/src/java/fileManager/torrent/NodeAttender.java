package fileManager.torrent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class NodeAttender extends Thread{
	public static final String SEPARATOR=";";
	@SuppressWarnings("unused")
	private Tracker track;
	Socket s;
	public NodeAttender(Socket s) {
		this.s = s;
	}
	@Override
	public void run() {
		try(BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));DataOutputStream dos=new DataOutputStream(s.getOutputStream())){
			for(String h;(h=br.readLine())!=null;){
				attendRequest(h.split(SEPARATOR),dos);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void attendRequest(String[] request,DataOutputStream dos){
		
	}
}
