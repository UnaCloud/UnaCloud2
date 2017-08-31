package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.share.db.entities.ExecutionEntity;
import uniandes.unacloud.share.enums.ExecutionStateEnum;

/**
 * Responsible to execute SQL commands in database to update or select Execution entities
 * This class use JDBC
 * @author CesarF
 *
 */
public class ExecutionManager {

	/**
	 * Updates an execution entity on database.
	 * @param execution to be modified
	 * @param currentState to avoid changes if state has changed. 
	 * @param con Database connection
	 * @return true in case execution was updated, false in case not
	 */
	public static boolean updateExecution(ExecutionEntity execution, ExecutionStateEnum currentState, Connection con) {
		if (execution.getId() == null || execution.getId() < 1) 
			return false;
		try {
			String query = 
					"UPDATE execution vme "
					+ "JOIN execution_state exest "
					+ "ON vme.state_id = exest.id " 
					+ "SET "+
			(execution.getDuration() != null? ", vme.duration = ? " : "") +
			(execution.getMessage() != null? ", vme.message = ? " : "");
			if (execution.getState() != null) {
				String state = null;
				if (query.contains(",")) 
					query = query.replaceFirst(",", "");
				if (execution.getState() == ExecutionProcessEnum.FAIL)
					state = "exest.next_control_id";
				else if (execution.getState() == ExecutionProcessEnum.REQUEST)
					state = "exest.next_requested_id";
				else if (execution.getState() == ExecutionProcessEnum.SUCCESS) 
					state = "exest.next_id";
				query = ", vme.state_id = " + state
						+ " WHERE vme.id = ? ";						
				if (execution.getNode() != null && execution.getNode().getHost() != null) 
					query += "AND vme.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?) ";
				if (currentState != null)
					query += "AND exest.state == \'" + currentState.name() + "\' ";
				query += "AND vme.id > 0 "
						+ "AND " + state + " IS NOT NULL;"; 
				PreparedStatement ps = con.prepareStatement(query);
				int id = 1;
				if (execution.getDuration() != null)
					ps.setLong(id++, execution.getDuration());
				if (execution.getMessage() != null)
					ps.setString(id++, execution.getMessage());
				ps.setLong(id, execution.getId());
				if (execution.getNode() != null && execution.getNode().getHost() != null) 
					ps.setString(id++, execution.getNode().getHost());
				
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
	 * Updates all executions by name and id in array and return which are in finishing process.
	 * Method used to update report
	 * @param ids executions 
	 * @param host which reports
	 * @param con Database connection
	 * @return list of executions which should be stopped in agents
	 */
	public static List<Long> updateExecutions( String host, Long[]ids, Connection con) {
		if(ids == null || ids.length == 0)
			return null;
		try {
			StringBuilder builder = new StringBuilder();
			for (@SuppressWarnings("unused") Long pm : ids)
				builder.append("?,");
			
			builder = builder.deleteCharAt( builder.length() -1 );
			List<Long> idsToStop = new ArrayList<Long>();
			String query = 
					"SELECT vm.id "
					+ "FROM execution vm INNER JOIN execution_state exe ON exe.id = vm.state_id "
					+ "WHERE vm.id in (" + builder.toString() + ") "
							+ "AND (exe.state = \'" + ExecutionStateEnum.FAILED.name() + "\' "
									+ "OR exe.state = \'" + ExecutionStateEnum.FINISHED.name() + "\' "
											+ "OR exe.state = \'" + ExecutionStateEnum.FINISHING.name() + "\')";
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
			String update = 
					"UPDATE execution vm "
					+ "SET vm.last_report = CURRENT_TIMESTAMP "
					+ "WHERE vm.id IN (" + builder.toString() + ") "
							+ "AND vm.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?)";
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
