package pmStateManager;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import unacloud2.PhysicalMachine;

public class StateManager {

	static Map<String,PmReportStatus> pmMap=new TreeMap<String, PmReportStatus>();
	public static synchronized boolean registerPhysicalMachineReport(String hostname,String user){
		return true;
		/*PmReportStatus pmReport=pmMap.get(hostname);
		boolean withuser=user!=null&&!user.isEmpty()&&!user.equals("null")&&!user.equals(">null");
		if(pmReport==null){
			pmMap.put(hostname,new PmReportStatus(withuser,System.currentTimeMillis()));
			return true;
		}
		pmReport.lastReport=System.currentTimeMillis();
		boolean b=pmReport.withUser; 
		pmReport.withUser=withuser;
		//return (b!=withuser);
		return true;*/
	}
	public static synchronized boolean isPMoff(String hostname){
		PmReportStatus pmReport=pmMap.get(hostname);
		return pmReport==null||(System.currentTimeMillis()-pmReport.lastReport)>10000;
	}
	public static List<PhysicalMachine> filterPhysicalMachines(List<PhysicalMachine> pms){
		//List<PhysicalMachine> ret=new ArrayList<>(); 
		//for(PhysicalMachine pm:pms)if(!isPMoff(pm.getName()))ret.add(pm);
		return pms;
	}
	public static class PmReportStatus{
		boolean withUser;
		long lastReport;
		public PmReportStatus(boolean withUser, long lastReport) {
			super();
			this.withUser = withUser;
			this.lastReport = lastReport;
		}
	}
}
