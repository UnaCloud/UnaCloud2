package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.share.db.entities.PhysicalMachineEntity;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;

/**
 * Class used to execute query, update and delete processes in database for Physical machine Entity. 
 * @author CesarF
 */
public class PhysicalMachineManager {
	
	/**
	 * Returns a PhysicalMachine entity requested by id and state
	 * @param id physical machine entity
	 * @param machineState physical machine state
	 * @param con Database Connection
	 * @return Physical Machine entity, could be null
	 */
	public static PhysicalMachineEntity getPhysicalMachine(Long id, PhysicalMachineStateEnum machineState, Connection con) {
		try {
			PreparedStatement ps = con.prepareStatement(
					"SELECT pm.id, i.ip, pm.state, pm.last_report, pm.name "
					+ "FROM physical_machine pm "
					+ "INNER JOIN ip i "
					+ "ON pm.ip_id = i.id "
					+ "WHERE pm.state = ? and pm.id = ?;");
			ps.setString(1, machineState.name());
			ps.setLong(2, id);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();	
			PhysicalMachineEntity machine = null;
			if (rs.next())
				machine = new PhysicalMachineEntity(rs.getLong(1), 
						rs.getString(2),
						new java.util.Date(rs.getTimestamp(4).getTime()), 
						PhysicalMachineStateEnum.getEnum(rs.getString(3)),
						rs.getString(5));
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();	
			}
			return machine;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * Return a list of all physical machines requested in id list and state
	 * @param idList list of ids from physical machines
	 * @param machineState state of machine
	 * @param con Database Connection
	 * @return List of Physical Machines entities
	 */
	public static List<PhysicalMachineEntity> getPhysicalMachineList(Long[] idList, PhysicalMachineStateEnum machineState, Connection con) {
		if (idList.length == 0)
			return null;
		try {
			List<PhysicalMachineEntity> list = new ArrayList<PhysicalMachineEntity>();
			StringBuilder builder = new StringBuilder();
			for(@SuppressWarnings("unused") Long pm: idList) {
				builder.append("?,");
			}
			String query = "SELECT pm.id, i.ip, pm.state, pm.last_report, pm.name "
						+ "FROM physical_machine pm "
								+ "INNER JOIN ip i "
								+ "ON pm.ip_id = i.id "
						+ "WHERE pm.state = ? and pm.id in (" + builder.deleteCharAt( builder.length() -1 ).toString() + ");";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, machineState.name());
			int index = 2;
			for (Long idpm : idList) {
				ps.setLong(index, idpm);
				index++;
			}
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			while (rs.next())
				list.add(new PhysicalMachineEntity(
						rs.getLong(1), 
						rs.getString(2), 
						new java.util.Date(rs.getTimestamp(4).getTime()), 
						PhysicalMachineStateEnum.getEnum(rs.getString(3)),
						rs.getString(5)));
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();	
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns a list of all physical machines by state
	 * @param machineState state to filter machines
	 * @param con Database Connection
	 * @return list of physical machines entities
	 */
	public static List<PhysicalMachineEntity> getAllPhysicalMachine(PhysicalMachineStateEnum machineState, Connection con) {		
		try {
			List<PhysicalMachineEntity> list = new ArrayList<PhysicalMachineEntity>();
			String query = 
					"SELECT pm.id, i.ip, pm.state, pm.last_report, pm.name "
					+ "FROM physical_machine pm "
						+ "INNER JOIN ip i "
						+ "ON pm.ip_id = i.id "
					+ "WHERE pm.state = ? ;";
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setString(1, machineState.name());
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			while(rs.next())
				list.add(new PhysicalMachineEntity(
						rs.getLong(1), 
						rs.getString(2), 
						new java.util.Date(rs.getTimestamp(4).getTime()), 
						PhysicalMachineStateEnum.getEnum(rs.getString(3)),
						rs.getString(5)));
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Update a physical machine entity on database.
	 * @param machine to be modified
	 * @param con Database connection
	 * @return true if entity was updated, false in case not
	 */
	public static boolean setPhysicalMachine(PhysicalMachineEntity machine, Connection con) {
		if (machine.getId() == null || machine.getId() < 1)
			return false;
		try {
			String query = "UPDATE physical_machine pm SET"; 
			int parameters = 0;
			if (machine.getStatus() != null) {
				parameters++;
				query += " pm.state = ? ";
			}
			if (machine.getLastReport() != null) query += (parameters++ > 0 ? "," : "") + " pm.last_report = ? ";
			if (machine.getFreeSpace() != null) query += (parameters++ > 0 ? "," : "") + " pm.free_space = ? ";
			if (machine.getVersion() != null) query += (parameters++ > 0 ? "," : "") + " pm.agent_version = ? ";
			if (machine.getLogName() != null) query += (parameters++ > 0 ? "," : "") + " pm.last_log = ? ";
			if (parameters > 0) {
				query += "WHERE pm.id = ? AND pm.id > 0;";
				PreparedStatement ps = con.prepareStatement(query);
				int id = 1;
				if (machine.getStatus() != null) ps.setString(id++, machine.getStatus().name());
				if (machine.getLastReport() != null) ps.setTimestamp(id++, new Timestamp(machine.getLastReport().getTime()));
				if (machine.getFreeSpace() != null) ps.setLong(id++, machine.getFreeSpace());
			    if (machine.getVersion() != null) ps.setString(id++, machine.getVersion());
			    if (machine.getLogName() != null) ps.setString(id++, machine.getLogName());
				ps.setLong(id, machine.getId());
				System.out.println(ps.toString() + "\n change " + ps.executeUpdate() + " lines");
				try {
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();	
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	
	/**
	 * Updates a physical machine entity on database based in hostname
	 * @param host name in network for physical machine
	 * @param hostUser user in physical machine
	 * @param ip where message is coming
	 * @param con Database Connection
	 * @param freeSpace quantity in bytes about free space in data folder in physical machine
	 * @param dataSpace quantity in bytes about total space in data folder in physical machine
	 * @param version current agent version
	 * @return true in case physical machines could be updated, false in case not
	 * TODO: add validation of ip 
	 */
	public static boolean updatePhysicalMachine(String host, String hostUser, String ip, Long freeSpace, Long dataSpace, String version, Connection con) {
		try {
			String query = "UPDATE physical_machine pm "
					+ "SET pm.with_user = ?, pm.state = CASE WHEN pm.state = \'" + PhysicalMachineStateEnum.OFF.name()
					+ "\' THEN  \'"+PhysicalMachineStateEnum.ON.name() + "\' ELSE pm.state END, pm.last_report = CURRENT_TIMESTAMP "
					+ (dataSpace != null ? ", pm.data_space = ?" : "")
					+ (freeSpace != null ? ", pm.free_space = ?" : "")
					+ (version != null ? ", pm.agent_version = ?" : "")
					+ " WHERE pm.name = ? AND pm.ip_id = (SELECT id FROM ip AS i WHERE i.ip = ?)"; PreparedStatement ps = con.prepareStatement(query);
			int pos = 1;
			ps.setBoolean(pos++, (hostUser != null && !hostUser.isEmpty() && !(hostUser.replace(">","").replace(" ","")).equals("null")));
			if (dataSpace != null) ps.setLong(pos++, dataSpace);
			if (freeSpace != null) ps.setLong(pos++, freeSpace);
			if (version != null) ps.setString(pos++, version);
			ps.setString(pos++, host.toUpperCase());
			ps.setString(pos++, ip);
			ps.executeUpdate();
			try {
				ps.close();
			} catch (Exception e) {
				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	
	/**
	 * Returns physical machine with the same hostname 
	 * @param name to search physicalmachine
	 * @param con Database Connection
	 * @return PhysicalMachine or null
	 */
	public static PhysicalMachineEntity getPhysicalMachineByHostName(String name, Connection con){
		try {
			PreparedStatement ps = con.prepareStatement(
					"SELECT pm.id, i.ip, pm.state, pm.last_report, pm.name "
					+ "FROM physical_machine pm "
					+ "INNER JOIN ip i "
					+ "ON pm.ip_id = i.id "
					+ "WHERE pm.name = ?;");
			ps.setString(1, name);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();	
			PhysicalMachineEntity machine = null;
			if (rs.next())
				machine = new PhysicalMachineEntity(rs.getLong(1), 
						rs.getString(2),
						new java.util.Date(rs.getTimestamp(4).getTime()), 
						PhysicalMachineStateEnum.getEnum(rs.getString(3)),
						rs.getString(5));
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();	
			}
			return machine;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}

}
