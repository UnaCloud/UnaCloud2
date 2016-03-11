package uniandes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import unacloud.share.enums.PhysicalMachineStateEnum;
import unacloud.share.enums.VirtualMachineExecutionStateEnum;

/**
 * Class used to execute query, update and delete processes in database for Physical Machine Entity. 
 * Although this class execute query in virtualmachine execution entities
 * @author CesarF
 *
 */
public class PhysicalMachineUpdater {

	/**
	 * Updates a physical machine entity on database based in hostname
	 * @param host name in network for physical machine
	 * @param hostUser user in physical machine
	 * @param ip where message is coming
	 * @param con Database Connection
	 * @return true in case physical machines could be updated, false in case not
	 * TODO: add validation of ip 
	 */
	public static boolean updatePhysicalMachine(String host, String hostUser, String ip, Connection con){
		try {
			String query = "update physical_machine pm set pm.with_user= ?, pm.state = CASE WHEN pm.state = \'"+PhysicalMachineStateEnum.OFF.name()+"\' THEN  \'"+PhysicalMachineStateEnum.ON.name()+"\' ELSE pm.state END, pm.last_report = CURRENT_TIMESTAMP WHERE pm.name = ?"; 
			PreparedStatement ps = con.prepareStatement(query);
			ps.setBoolean(1, (hostUser!=null&&!hostUser.isEmpty()&&!(hostUser.replace(">","").replace(" ","")).equals("null")));
			ps.setString(2, host);
			System.out.println(ps.toString());
			System.out.println("update "+host+" - "+ps.executeUpdate());
			try{ps.close();}catch(Exception e){}
			return true;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	

	/**
	 * Updates status from of virtual Machine
	 * @param id virtual machine 
	 * @param host unique in net	
	 * @param message description
	 * @param status in agent
	 * @param con connection to database
	 * @return true in case virtual execution could be updated, false in case not
	 */
	public static boolean updateVirtualExecution(Long id, String host, String message, VirtualMachineExecutionStateEnum status, Connection con){
		try {
			String query = "update virtual_machine_execution vm set vm.message= ?, vm.last_report = CURRENT_TIMESTAMP, vm.status = ?  WHERE vm.id = ? and vm.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?);"; 
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setString(1, message);
			ps.setString(2, status.name());
			ps.setLong(3, id);
			ps.setString(4, host);
			System.out.println(ps.toString()+" changes "+ps.executeUpdate()+" lines ");
			try{ps.close();}catch(Exception e){}
			return true;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	
	
	/**
	 * Updates all virtual executions with id in array and host by name
	 * @param ids virtual machine execution 
	 * @param host which reports
	 * @param con Database connection
	 * @return list of virtual machines execution which should be stopped in agents
	 */
	public static List<Long> updateVirtualMachinesExecutions(Long[]ids,String host, Connection con){
		if(ids==null||ids.length==0)return null;
		try {
			StringBuilder builder = new StringBuilder();
			for(@SuppressWarnings("unused") Long pm: ids){
				builder.append("?,");
			}
			List<Long> idsToStop = new ArrayList<Long>();
			String query = "SELECT vm.id FROM virtual_machine_execution vm where vm.id in ("+builder.deleteCharAt( builder.length() -1 ).toString()+") AND (vm.status = \'"+VirtualMachineExecutionStateEnum.FAILED.name()+"\' OR vm.status = \'"+VirtualMachineExecutionStateEnum.FINISHED.name()+"\' OR vm.status = \'"+VirtualMachineExecutionStateEnum.FINISHING.name()+"\')";
			PreparedStatement ps = con.prepareStatement(query);
			int index = 1;
			for(Long idvme: ids){
				ps.setLong(index++, idvme);
			}
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			while(rs.next())idsToStop.add(rs.getLong(1));
			try{rs.close();ps.close();}catch(Exception e){}
			
			String update = "update virtual_machine_execution vm set vm.last_report = CURRENT_TIMESTAMP where vm.id in ("+builder.deleteCharAt( builder.length() -1 ).toString()+") and vm.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?)";
			ps = con.prepareStatement(update);
			index = 1;
			for(Long idvme: ids){
				ps.setLong(index++, idvme);
			}
			ps.setString(index, host);
			System.out.println(ps.toString()+" changes "+ps.executeUpdate()+" lines ");
			try{ps.close();}catch(Exception e){}
			return idsToStop;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}

}
