package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import uniandes.unacloud.share.db.entities.DeployedImageEntity;
import uniandes.unacloud.share.db.entities.DeploymentEntity;
import uniandes.unacloud.share.db.entities.ExecutionEntity;
import uniandes.unacloud.share.db.entities.ImageEntity;
import uniandes.unacloud.share.db.entities.NetInterfaceEntity;
import uniandes.unacloud.share.db.entities.PhysicalMachineEntity;
import uniandes.unacloud.share.enums.DeploymentStateEnum;
import uniandes.unacloud.share.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Class used to process queries and updates on Deployment entities
 * @author CesarF
 *
 */

public class DeploymentManager {
	
	/**
	 * Queries and returns a Deployment request by id in parameters
	 * @param id Deployment Database ID
	 * @param con Database Connection
	 * @return Deployment entity, could be null
	 */
	public static DeploymentEntity getDeployment(Long id, Connection con, int t) {
		try {
			DeploymentEntity deploy = null;
			PreparedStatement ps = con.prepareStatement(
					"SELECT dp.id, dp.start_time, dp.stop_time, dp.status "
					+ "FROM deployment dp "
					+ "WHERE dp.status = ? and dp.id = ?;");
			ps.setString(1, DeploymentStateEnum.ACTIVE.name());
			ps.setLong(2, id);
			ResultSet rs = ps.executeQuery();	
			System.out.println(ps.toString());
			if (rs.next()) {
				deploy = new DeploymentEntity();
				deploy.setId(rs.getLong(1));
				deploy.setStartTime(new java.util.Date(rs.getTimestamp(2).getTime()));
				deploy.setStopTime(new java.util.Date(rs.getTimestamp(3).getTime()));
				deploy.setState(DeploymentStateEnum.ACTIVE);
			}
			try {
				rs.close();
				ps.close();
			} catch(Exception e) {
				
			}
			if (deploy != null) {
				ps = con.prepareStatement(
						"SELECT vme.id, hp.cores, hp.ram, vme.start_time, vme.stop_time, vme.execution_node_id, vme.name, vmi.id, vmi.user, vmi.password, vmi.state, vme.message, vmi.token "
						+ "FROM execution vme "
							+ "INNER JOIN hardware_profile hp "
								+ "ON vme.hardware_profile_id = hp.id "
							+ "INNER JOIN deployed_image dp "
								+ "ON dp.id = vme.deploy_image_id "
							+ "INNER JOIN image vmi "
								+ "ON dp.image_id = vmi.id "
							+ "INNER JOIN execution_state exes "
								+ "ON vme.state_id = exes.id "
						+ "WHERE dp.deployment_id = ? AND exes.state = ?;");
				ps.setLong(1, id);
				ps.setString(2, ExecutionStateEnum.REQUESTED.name());
				rs = ps.executeQuery();	
				System.out.println(ps.toString());
				TreeMap<Long, DeployedImageEntity> executions = new TreeMap<Long, DeployedImageEntity>();
				while (rs.next()) {
					PhysicalMachineEntity pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(6), PhysicalMachineStateEnum.ON, con);
					if (pm == null)
						//ExecutionManager.updateExecution(rs.getLong(1), pm.g, message, status, con)
						//ExecutionManager.setExecution(new ExecutionEntity(rs.getLong(1), 0, 0, null, new Date(), null, ExecutionStateEnum.FAILED, null, "Communication error"), con);
					else {
						ExecutionEntity vme = new ExecutionEntity(rs.getLong(1), 
								rs.getInt(2), 
								rs.getInt(3),
								new java.util.Date(rs.getTimestamp(4).getTime()), 
								new java.util.Date(rs.getTimestamp(5).getTime()), 
								pm, 
								rs.getString(7),
								rs.getString(13));
						if (executions.get(rs.getLong(8)) == null)
							executions.put(rs.getLong(8), 
									new DeployedImageEntity(new ImageEntity(rs.getLong(8), rs.getString(9), rs.getString(10), ImageEnum.getEnum(rs.getString(11)), rs.getString(13)), 
											new ArrayList<ExecutionEntity>()));
						executions.get(rs.getLong(8)).getExecutions().add(vme);						
					}
				}
				try {
					rs.close();
					ps.close();
				} catch (Exception e) {
					
				}
				deploy.setImages(new ArrayList<DeployedImageEntity>());
				for (DeployedImageEntity image : executions.values()) {
					for (ExecutionEntity execution : image.getExecutions()) {
						execution.getInterfaces().addAll(getInterfaces(execution,con));
					}
				}
				deploy.getImages().addAll(executions.values());				
			}			
			return deploy;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * Returns a list of configured interfaces for an Execution
	 * @param execution to find net interfaces
	 * @param con Database connection
	 * @return list of net interfaces configured for image
	 */
	public static List<NetInterfaceEntity> getInterfaces(ExecutionEntity execution, Connection con) {
		try {
			List<NetInterfaceEntity> list = new ArrayList<NetInterfaceEntity>();		
			String query = "SELECT ni.id, ni.name, i.ip, ipl.mask "
							+ "FROM net_interface ni "
								+ "INNER JOIN ip i ON ni.ip_id = i.id "
								+ "INNER JOIN ippool ipl ON i.ip_pool_id = ipl.id "
							+ "WHERE ni.execution_id = ? ;";
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setLong(1, execution.getId());
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			while (rs.next())
				list.add(new NetInterfaceEntity(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Returns a list of deployed executions requested by parameter ids
	 * @param ids list of ids to be requested
	 * @param state in case of null value return all execution in array without filter
	 * @param con Database connection
	 * @return list of execution
	 */
	public static List<ExecutionEntity> getExecutions(Long[]ids, ExecutionStateEnum state, boolean withInterfaces, Connection con, int t) {
		try {			
			StringBuilder builder = new StringBuilder();
			for(@SuppressWarnings("unused") Long id: ids) {
				builder.append("?,");
			}
			String query = "SELECT vme.id, hp.cores, hp.ram, vme.start_time, vme.stop_time, vme.status, vme.execution_node_id, vme.name, vme.message "
							+ "FROM execution vme "
								+ "INNER JOIN hardware_profile hp ON vme.hardware_profile_id = hp.id "
							+ "WHERE vme.id IN (" + builder.deleteCharAt( builder.length() -1 ).toString() + ")" + (state != null ? " AND vme.status = ?;" : ";");
			PreparedStatement ps = con.prepareStatement(query);
		
			int index = 1;
			for (Long idvme: ids) {
				ps.setLong(index, idvme);
				index++;
			}
			if (state != null) 
				ps.setString(index, state.name());
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			List<ExecutionEntity> executions = new ArrayList<ExecutionEntity>();
			while (rs.next()) {
				PhysicalMachineEntity pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(7), PhysicalMachineStateEnum.ON,con);
				if (pm == null) {
					state = ExecutionStateEnum.getEnum(rs.getString(6));
					if(state.equals(ExecutionStateEnum.DEPLOYED))				
						setExecution(new ExecutionEntity(rs.getLong(1), 0, 0, null, null, null, ExecutionStateEnum.RECONNECTING, null, "Connection lost in server"), con);					
					if(state.equals(ExecutionStateEnum.REQUESTED))				
						setExecution(new ExecutionEntity(rs.getLong(1), 0, 0, null, null, null, ExecutionStateEnum.FAILED, null, "Communication error"), con);	
				} else {
					ExecutionEntity vme = new ExecutionEntity(
							rs.getLong(1), 
							rs.getInt(2), 
							rs.getInt(3), 
							new java.util.Date(rs.getTimestamp(4).getTime()),
							new java.util.Date(rs.getTimestamp(5).getTime()),
							pm, 
							ExecutionStateEnum.getEnum(rs.getString(6)),
							rs.getString(8), 
							rs.getString(9));
					executions.add(vme);		
				}
			}		
			if (withInterfaces)
				for (ExecutionEntity execution : executions) 
					execution.getInterfaces().addAll(getInterfaces(execution,con));			
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				
			}
			return executions;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns an execution requested by id and state
	 * @param id for execution
	 * @param state of execution
	 * @param con Database Connection
	 * @return Execution object, could be null
	 */
	public static ExecutionEntity getExecution(Long id, ExecutionStateEnum state, Connection con, int t) {
		try {			
			String query = "SELECT vme.id, hp.cores, hp.ram, vme.start_time, vme.stop_time, vme.status, vme.execution_node_id, vme.name, vme.message "
							+ "FROM execution vme "
								+ "INNER JOIN hardware_profile hp ON vme.hardware_profile_id = hp.id "
							+ "WHERE vme.status = ? AND vme.id = ?;";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, state.name());
			ps.setLong(2, id);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();	
			ExecutionEntity execution = null;
			
			if (rs.next()) {
				PhysicalMachineEntity pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(7), PhysicalMachineStateEnum.ON, con);
				if (pm == null) {
					if(state.equals(ExecutionStateEnum.DEPLOYED))				
						setExecution(new ExecutionEntity(rs.getLong(1), 0, 0, null, null, null, ExecutionStateEnum.RECONNECTING, null, "Connection lost in server"), con);					
					if(state.equals(ExecutionStateEnum.REQUESTED))				
						setExecution(new ExecutionEntity(rs.getLong(1), 0, 0, null, null, null, ExecutionStateEnum.FAILED, null, "Communication error"), con);	
				} else {
					execution = new ExecutionEntity(
							rs.getLong(1), 
							rs.getInt(2), 
							rs.getInt(3), 
							new java.util.Date(rs.getTimestamp(4).getTime()), 
							new java.util.Date(rs.getTimestamp(5).getTime()), 
							pm, 
							ExecutionStateEnum.getEnum(rs.getString(6)),
							rs.getString(8), 
							rs.getString(9));
				}
			}	
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				
			}
			return execution;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Updates states for IP in database
	 * @param executionId execution to modify IP
	 * @param con database connection
	 * @param ipstate State of IP
	 * @return true in case update was success, false in case not
	 */
	public static boolean breakFreeInterfaces(Long executionId, Connection con, IPEnum ipstate) {
		try {
			String update = "UPDATE ip SET state = ? WHERE id in (SELECT ip_id FROM net_interface WHERE execution_id = ?) AND id > 0"; 
			PreparedStatement ps = con.prepareStatement(update);
			ps.setString(1, ipstate.name());
			ps.setLong(2, executionId);
			System.out.println("Update: "+ps.executeUpdate());
			try {
				ps.close();
			} catch (Exception e) {
				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}

}
