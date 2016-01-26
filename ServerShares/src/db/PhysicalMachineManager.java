package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import unacloud.entities.PhysicalMachine;
import unacloud.enums.PhysicalMachineStateEnum;

/**
 * Generic class used to process queries and updates on PhysicalMachine entity 
 * @author Cesar
 *
 */
public class PhysicalMachineManager {
	
	/**
	 * Return a PhysicalMachine entity requested by id
	 * @param id physical machine id
	 * @return physical machine entity
	 */
	public static PhysicalMachine getPhysicalMachine(Long id, PhysicalMachineStateEnum machineState){
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT pm.id, i.ip, pm.state, pm.last_report FROM physical_machine pm INNER JOIN ip i ON pm.ip_id = i.id WHERE pm.state == ? and pm.id = ?;");
			ps.setString(1, machineState.name());
			ps.setLong(2, id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next())return new PhysicalMachine(rs.getLong(1), rs.getString(2), rs.getDate(3), PhysicalMachineStateEnum.getEnum(rs.getString(4)));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * Return a list of all ON physical machines requested by parameters
	 * @param idList
	 * @return
	 */
	public static List<PhysicalMachine> getPhysicalMachineList(Long[] idList, PhysicalMachineStateEnum machineState){
		if(idList.length==0)return null;
		try {
			List<PhysicalMachine> list = new ArrayList<PhysicalMachine>();
			Connection con = DatabaseConnection.getInstance().getConnection();
			StringBuilder builder = new StringBuilder();
			for(@SuppressWarnings("unused") Long pm: idList){
				builder.append("?,");
			}
			String query = "SELECT pm.id, i.ip, pm.state, pm.last_report FROM physical_machine pm INNER JOIN ip i ON pm.ip_id = i.id WHERE pm.state == ? and pm.id in ("+builder.deleteCharAt( builder.length() -1 ).toString()+");";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, machineState.name());
			int index = 2;
			for(Long idpm: idList){
				ps.setLong(index++, idpm);
			}
			ResultSet rs = ps.executeQuery();		
			while(rs.next())list.add(new PhysicalMachine(rs.getLong(1), rs.getString(2), rs.getDate(3), PhysicalMachineStateEnum.getEnum(rs.getString(4))));
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Return a list of all ON physical machines
	 * @param idList
	 * @return
	 */
	public static List<PhysicalMachine> getAllPhysicalMachine(PhysicalMachineStateEnum machineState){		
		try {
			List<PhysicalMachine> list = new ArrayList<PhysicalMachine>();
			Connection con = DatabaseConnection.getInstance().getConnection();			
			String query = "SELECT pm.id, i.ip, pm.state, pm.last_report FROM physical_machine pm INNER JOIN ip i ON pm.ip_id = i.id WHERE pm.state == ? ;";
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setString(1, machineState.name());
			ResultSet rs = ps.executeQuery();		
			while(rs.next())list.add(new PhysicalMachine(rs.getLong(1), rs.getString(2), rs.getDate(3), PhysicalMachineStateEnum.getEnum(rs.getString(4))));
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Update a physical machine entity on database.
	 * @param machine
	 * @return
	 */
	public static boolean setPhysicalMachine(PhysicalMachine machine){
		if(machine.getId()==null||machine.getId()<1)return false;
		try {
			String query = "update physical_machine pm "; 
			int status = 0;
			int report = 0;
			if(machine.getStatus()!=null){query+=" set pm.state = ? ";status = 1;}
			if(machine.getLastReport()!=null){query+=(status>0?",":"")+" set pm.last_report = ? ";report=status+1;};
			if(status>0||report>0){
				query += "where pm.id = ? and pm.id > 0;";
				Connection con = DatabaseConnection.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query);
				int id = 0;
				if(status>0){ps.setString(status, machine.getStatus().name());id++;};
				if(report>0){ps.setDate(report, new java.sql.Date(machine.getLastReport().getTime()));id++;};
				ps.setLong(id, machine.getId());
				System.out.println("Change "+ps.executeUpdate()+" lines");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}

}
