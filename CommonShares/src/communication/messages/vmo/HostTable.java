package communication.messages.vmo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class HostTable implements Serializable{
	private static final long serialVersionUID = 2549432609629692430L;
	private List<String[]> hosts=new LinkedList<>();
	public List<String[]> getHosts() {
		return hosts;
	}
	public void addHost(String hostname,String ipAddress){
		hosts.add(new String[]{hostname,ipAddress});
	}
	
}
