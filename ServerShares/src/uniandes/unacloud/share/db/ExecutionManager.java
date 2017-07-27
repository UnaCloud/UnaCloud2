package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.share.db.entities.ExecutionEntity;
import uniandes.unacloud.share.enums.ExecutionStateEnum;

public class ExecutionManager {

	/**
	 * Updates an execution entity on database.
	 * @param execution to be modified
	 * @param con Database connection
	 * @return true in case execution was updated, false in case not
	 */
	public static boolean setExecution(ExecutionEntity execution, Connection con, int t) {
		if (execution.getId() == null || execution.getId() < 1) 
			return false;
		try {
			String query = "UPDATE execution vme SET " + 
			(execution.getStartTime() != null? ", vme.start_time = ? " : "") +
			(execution.getStopTime() != null? ", vme.stop_time = ? " : "") +
			(execution.getState() != null? ", vme.state = ? " : "") +
			(execution.getMessage() != null? ", vme.message = ? " : "");
			if (query.contains(",")) {
				query = query.replaceFirst(",", "");
				query += "WHERE vme.id = ? AND vme.id > 0;";
				PreparedStatement ps = con.prepareStatement(query);
				int id = 1;
				if (execution.getStartTime() != null) ps.setTimestamp(id++, new Timestamp(execution.getStartTime().getTime()));
				if (execution.getStopTime() != null) ps.setTimestamp(id++,  new Timestamp(execution.getStopTime().getTime()));
				if (execution.getState() != null) ps.setString(id++, execution.getState().name());
				if (execution.getMessage() != null) ps.setString(id++, execution.getMessage());
				ps.setLong(id, execution.getId());
				System.out.println(ps.toString() + " change " + ps.executeUpdate() + " lines");				
				try {
					ps.close();
				} catch(Exception e) {
					
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	
	/**
	 * Updates status from of execution
	 * @param id execution 
	 * @param host unique in net	
	 * @param message description
	 * @param status in agent
	 * @param con connection to database
	 * @return true in case execution could be updated, false in case not
	 */
	//TODO 
	public static boolean updateExecution(Long id, String host, String message, ExecutionProcessEnum status, Connection con) {
		try {
			String query = null;
			if (status == ExecutionProcessEnum.FAIL)
				query = "UPDATE execution vm JOIN execution_state exest ON vm.state_id = exest.id "
						+ "SET vm.message = exest.control_message, vm.last_report = CURRENT_TIMESTAMP, vm.state_id = exest.next_control_id"
						+ "WHERE vm.id = ? "
							+ "AND vm.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?)"
							+ "AND exest.next_control_id IS NOT NULL;"; 
			if (status == ExecutionProcessEnum.REQUEST)
				query = "UPDATE execution vm JOIN execution_state exest ON vm.state_id = exest.id "
						+ "SET vm.message = ?, vm.last_report = CURRENT_TIMESTAMP, vm.state_id = exest.next_requested_id "
						+ "WHERE vm.id = ? "
							+ "AND vm.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?)"
							+ "AND exest.next_requested_id IS NOT NULL;"; 
			if (status == ExecutionProcessEnum.SUCCESS)
				query = "UPDATE execution vm JOIN execution_state exest ON vm.state_id = exest.id "
						+ "SET vm.message = ?, vm.last_report = CURRENT_TIMESTAMP, vm.state_id = exest.next_id "
						+ "WHERE vm.id = ? "
							+ "AND vm.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?)"
							+ "AND exest.next_id IS NOT NULL;"; 
			PreparedStatement ps = con.prepareStatement(query);	
			int count = 1;
			if (status != ExecutionProcessEnum.FAIL) ps.setString(count++, message);
			ps.setLong(count++, id);
			ps.setString(count++, host);
			System.out.println(ps.toString() + " changes " + ps.executeUpdate() + " lines ");
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
	 * Updates all executions by name and id in array
	 * @param ids executions 
	 * @param host which reports
	 * @param con Database connection
	 * @return list of executions which should be stopped in agents
	 */
	public static List<Long> updateExecutions(Long[]ids, String host, Connection con) {
		if(ids == null || ids.length == 0)
			return null;
		try {
			StringBuilder builder = new StringBuilder();
			for (@SuppressWarnings("unused") Long pm : ids)
				builder.append("?,");
			
			builder = builder.deleteCharAt( builder.length() -1 );
			List<Long> idsToStop = new ArrayList<Long>();
			String query = "SELECT vm.id FROM execution vm WHERE vm.id in (" + builder.toString() + ") AND (vm.state = \'" + ExecutionStateEnum.FAILED.name() + "\' OR vm.state = \'" + ExecutionStateEnum.FINISHED.name() + "\' OR vm.state = \'" + ExecutionStateEnum.FINISHING.name() + "\')";
			PreparedStatement ps = con.prepareStatement(query);
			int index = 1;
			for (Long idvme : ids) {
				ps.setLong(index, idvme);
				index++;
			}
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				idsToStop.add(rs.getLong(1));
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				
			}			
			String update = "UPDATE execution vm SET vm.last_report = CURRENT_TIMESTAMP WHERE vm.id in (" + builder.toString() + ") AND vm.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?)";
			PreparedStatement ps2 = con.prepareStatement(update);
			index = 1;
			for (Long idvme : ids) {
				ps2.setLong(index, idvme);
				index++;
			}
			ps2.setString(index, host);
			System.out.println(ps2.toString() + " changes " + ps2.executeUpdate() + " lines ");
			try {				
				ps2.close();
			} catch (Exception e) {
				
			}
			return idsToStop;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
}
