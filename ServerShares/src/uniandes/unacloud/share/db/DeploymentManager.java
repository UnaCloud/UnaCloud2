package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.share.db.entities.DeployedImageEntity;
import uniandes.unacloud.share.db.entities.DeploymentEntity;
import uniandes.unacloud.share.db.entities.ExecutionEntity;
import uniandes.unacloud.share.db.entities.ImageEntity;
import uniandes.unacloud.share.db.entities.NetInterfaceEntity;
import uniandes.unacloud.share.db.entities.PhysicalMachineEntity;
import uniandes.unacloud.share.enums.DeploymentStateEnum;
import uniandes.unacloud.share.enums.ExecutionStateEnum;
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
	public static DeploymentEntity getDeployment(Long id, Connection con) {
		try {
			DeploymentEntity deploy = null;
			PreparedStatement ps = con.prepareStatement(
					"SELECT dp.id, dp.start_time, dp.status "
					+ "FROM deployment dp "
					+ "WHERE dp.status = ? AND dp.id = ?;");
			ps.setString(1, DeploymentStateEnum.ACTIVE.name());
			ps.setLong(2, id);
			ResultSet rs = ps.executeQuery();	
			System.out.println(ps.toString());
			if (rs.next()) {
				deploy = new DeploymentEntity();
				deploy.setId(rs.getLong(1));
				deploy.setStartTime(new java.util.Date(rs.getTimestamp(2).getTime()));
				deploy.setState(DeploymentStateEnum.ACTIVE);
			}
			try {
				rs.close();
				ps.close();
			} catch(Exception e) {
				
			}
			if (deploy != null) {
				ps = con.prepareStatement(
						"SELECT vme.id, hp.cores, hp.ram, vme.duration, vme.execution_node_id, vme.name, vmi.id, vmi.user, vmi.password, vmi.state, vme.message, vmi.token "
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
					PhysicalMachineEntity pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(5), PhysicalMachineStateEnum.ON, con);
					if (pm == null){
						ExecutionEntity exe = new ExecutionEntity(rs.getLong(1), 0, 0, null, null, ExecutionProcessEnum.FAIL, null, "Communication error");
						ExecutionManager.updateExecution(exe, ExecutionStateEnum.REQUESTED, con);
					}
					else {
						ExecutionEntity vme = new ExecutionEntity(
								rs.getLong(1), 
								rs.getInt(2), 
								rs.getInt(3),
								rs.getLong(4), 
								pm, 
								rs.getString(6),
								rs.getString(11));
						if (executions.get(rs.getLong(7)) == null)
							executions.put(rs.getLong(7), 
									new DeployedImageEntity(
											new ImageEntity(
													rs.getLong(7), 
													rs.getString(8), 
													rs.getString(9), 
													ImageEnum.getEnum(rs.getString(10)), 
													rs.getString(12)), 
											new ArrayList<ExecutionEntity>()));
						executions.get(rs.getLong(7)).getExecutions().add(vme);						
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
	public static List<ExecutionEntity> getExecutions(Long[]ids, boolean withInterfaces, ExecutionStateEnum[] states, Connection con) {
		try {			
			StringBuilder builder = new StringBuilder();
			for (@SuppressWarnings("unused") Long id: ids)
				builder.append("?,");
			StringBuilder builderS = new StringBuilder();
			if (states != null) 
				for(@SuppressWarnings("unused") ExecutionStateEnum id: states)
					builderS.append("?,");
			
			String query = "SELECT vme.id, hp.cores, hp.ram, vme.duration, ex.state, vme.execution_node_id, vme.name, vme.message "
							+ "FROM execution vme "
								+ "INNER JOIN hardware_profile hp ON vme.hardware_profile_id = hp.id "
								+ "INNER JOIN execution_state ex ON ex.id = vme.state_id "
							+ "WHERE vme.id IN (" + builder.deleteCharAt( builder.length() -1 ).toString() + ") ";
			if (states != null && states.length > 0)
				query += " AND exe.state IN (" + builder.deleteCharAt( builderS.length() -1 ).toString() + ")";
			query += ";";
			PreparedStatement ps = con.prepareStatement(query);
		
			int index = 1;
			for (Long idvme: ids)
				ps.setLong(index++, idvme);
			if (states != null) 
				for (ExecutionStateEnum state: states)
					ps.setString(index++, state.name());
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			List<ExecutionEntity> executions = new ArrayList<ExecutionEntity>();
			while (rs.next()) {
				PhysicalMachineEntity pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(6), PhysicalMachineStateEnum.ON, con);
				if (pm == null) {
					ExecutionStateEnum state = ExecutionStateEnum.getEnum(rs.getString(5));
					if(state.equals(ExecutionStateEnum.DEPLOYED)) {
						ExecutionEntity exe = new ExecutionEntity(rs.getLong(1), 0, 0, null, null, ExecutionProcessEnum.FAIL, null, "Connection lost in server");
						ExecutionManager.updateExecution(exe, ExecutionStateEnum.DEPLOYED, con);
					}			
					if(state.equals(ExecutionStateEnum.REQUESTED))	{
						ExecutionEntity exe = new ExecutionEntity(rs.getLong(1), 0, 0, null, null, ExecutionProcessEnum.FAIL, null, "Communication error");
						ExecutionManager.updateExecution(exe, ExecutionStateEnum.REQUESTED, con);
					}					
				} 
				else {
					ExecutionEntity vme = new ExecutionEntity(
							rs.getLong(1), 
							rs.getInt(2), 
							rs.getInt(3), 
							rs.getLong(4), 
							pm, 
							rs.getString(7), 
							rs.getString(8));
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
	public static ExecutionEntity getExecution(Long id, ExecutionStateEnum state, Connection con) {
		try {			
			String query = "SELECT vme.id, hp.cores, hp.ram, vme.duration, exe.state, vme.execution_node_id, vme.name, vme.message "
							+ "FROM execution vme "
								+ "INNER JOIN hardware_profile hp ON vme.hardware_profile_id = hp.id "
								+ "INNER JOIN execution_state ex ON ex.id = vme.state_id "
							+ "WHERE exe.state = ? AND vme.id = ?;";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, state.name());
			ps.setLong(2, id);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();	
			ExecutionEntity execution = null;
			
			if (rs.next()) {
				PhysicalMachineEntity pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(6), PhysicalMachineStateEnum.ON, con);
				if (pm == null) {
					if (state.equals(ExecutionStateEnum.DEPLOYED)) {
						ExecutionEntity exe = new ExecutionEntity(rs.getLong(1), 0, 0, null, null, ExecutionProcessEnum.FAIL, null, "Connection lost in server");
						ExecutionManager.updateExecution(exe, ExecutionStateEnum.DEPLOYED, con);
					}			
					if (state.equals(ExecutionStateEnum.REQUESTED))	{
						ExecutionEntity exe = new ExecutionEntity(rs.getLong(1), 0, 0, null, null, ExecutionProcessEnum.FAIL, null, "Communication error");
						ExecutionManager.updateExecution(exe, ExecutionStateEnum.REQUESTED, con);
					}
				} 
				else {
					execution = new ExecutionEntity(
							rs.getLong(1), 
							rs.getInt(2), 
							rs.getInt(3), 
							rs.getLong(4),  
							pm, 
							rs.getString(7), 
							rs.getString(8));
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

}
