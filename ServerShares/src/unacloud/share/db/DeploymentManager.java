package unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import unacloud.share.enums.VirtualMachineExecutionStateEnum;

import unacloud.share.entities.DeployedImageEntity;
import unacloud.share.entities.DeploymentEntity;
import unacloud.share.entities.NetInterfaceEntity;
import unacloud.share.entities.PhysicalMachineEntity;
import unacloud.share.entities.VirtualMachineExecutionEntity;
import unacloud.share.entities.VirtualMachineImageEntity;
import unacloud.share.enums.DeploymentStateEnum;
import unacloud.share.enums.PhysicalMachineStateEnum;
import unacloud.share.enums.VirtualMachineImageEnum;

/**
 * Class used to process queries and updates on Deployment entities
 * @author Cesar
 *
 */

public class DeploymentManager {
	
	/**
	 * Queries and returns a Deployment request by id in parameters
	 * @param id Deployment Database ID
	 * @return
	 */
	public static DeploymentEntity getDeployment(Long id, Connection con){
		try {
			DeploymentEntity deploy = null;
			PreparedStatement ps = con.prepareStatement("SELECT dp.id, dp.start_time, dp.stop_time, dp.status FROM deployment dp WHERE dp.status = ? and dp.id = ?;");
			ps.setString(1, DeploymentStateEnum.ACTIVE.name());
			ps.setLong(2, id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next()){
				deploy = new DeploymentEntity();
				deploy.setId(rs.getLong(1));
				deploy.setStartTime(rs.getDate(2));
				deploy.setStopTime(rs.getDate(3));
				deploy.setState(DeploymentStateEnum.ACTIVE);
			}
			try{rs.close();ps.close();}catch(Exception e){}
			if(deploy!=null){
				ps = con.prepareStatement("SELECT vme.id, hp.cores, hp.ram, vme.start_time, vme.stop_time, vme.status, vme.execution_node_id, vme.name, vmi.id, vmi.user, vmi.password, vmi.state, vme.message, vmi.token"
						+ "FROM virtual_machine_execution vme INNER JOIN hardware_profile hp ON vme.hardware_profile_id = hp.id INNER JOIN deployed_image dp ON dp.id = vme.deploy_image_id "
						+ "INNER JOIN virtual_machine_image vmi ON dp.image_id = vmi.id WHERE dp.deployment_id = ? AND vme.status = ?;");
				ps.setLong(1, id);
				ps.setString(2, VirtualMachineExecutionStateEnum.QUEQUED.name());
				rs = ps.executeQuery();	
				TreeMap<Long, DeployedImageEntity> executions = new TreeMap<Long, DeployedImageEntity>();
				while(rs.next()){
					PhysicalMachineEntity pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(7), PhysicalMachineStateEnum.ON, con);
					if(pm==null)setVirtualMachineExecution(new VirtualMachineExecutionEntity(rs.getLong(1), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null,"Communication error"),con);
					else{
						VirtualMachineExecutionEntity vme = new VirtualMachineExecutionEntity(rs.getLong(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), pm, VirtualMachineExecutionStateEnum.getEnum(rs.getString(6)),rs.getString(8),rs.getString(13));
						if(executions.get(rs.getLong(9))==null)
							executions.put(rs.getLong(9), new DeployedImageEntity(new VirtualMachineImageEntity(rs.getLong(9), rs.getString(10), rs.getString(11), VirtualMachineImageEnum.getEnum(rs.getString(12)), rs.getString(14)),new ArrayList<VirtualMachineExecutionEntity>()));
						executions.get(rs.getLong(9)).getExecutions().add(vme);						
					}
				}
				for(DeployedImageEntity image:deploy.getImages()){
					for(VirtualMachineExecutionEntity execution: image.getExecutions()){
						execution.getInterfaces().addAll(getInterfaces(execution,con));
					}
				}
				deploy.getImages().addAll(executions.values());				
			}
			try{rs.close();ps.close();}catch(Exception e){}
			return deploy;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Update a virtual machine execution entity on database.
	 * @param machine
	 * @return
	 */
	public static boolean setVirtualMachineExecution(VirtualMachineExecutionEntity execution,Connection con){
		if(execution.getId()==null||execution.getId()<1)return false;
		try {
			String query = "update virtual_machine_execution vme set "; 
			int start = 0;
			int stop = 0;
			int state = 0;
			int message = 0;
			if(execution.getStartTime()!=null){query+=" vme.start_time = ? ";start = 1;}
			if(execution.getStopTime()!=null){query+=(start>0?",":"")+" vme.stop_time = ? ";stop=start+1;};
			if(execution.getState()!=null){query+=(start>0||stop>0?",":"")+" vme.status= ? ";state=stop>0?stop+1:start+1;};
			if(execution.getMessage()!=null){query+=(start>0||stop>0||state>0?",":"")+" vme.message= ? ";message=state>0?state+1:stop>0?stop+1:start+1;};
			if(state>0||stop>0||start>0||message>0){
				query += "where vme.id = ? and vme.id > 0;";
				PreparedStatement ps = con.prepareStatement(query);
				int id = 1;
				if(start>0){ps.setDate(start, new java.sql.Date(execution.getStartTime().getTime()));id++;};
				if(stop>0){ps.setDate(stop, new java.sql.Date(execution.getStopTime().getTime()));id++;};
				if(state>0){ps.setString(state, execution.getState().name());id++;}
				if(message>0){ps.setString(message, execution.getMessage());id++;}
				ps.setLong(id, execution.getId());
				System.out.println("Change "+ps.executeUpdate()+" lines");
				try{ps.close();}catch(Exception e){}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	/**
	 * Return a list of configured interfaces for a VirtualMachineExecution
	 * @param execution
	 * @return
	 */
	public static List<NetInterfaceEntity> getInterfaces(VirtualMachineExecutionEntity execution, Connection con){
		try {
			List<NetInterfaceEntity> list = new ArrayList<NetInterfaceEntity>();		
			String query = "SELECT ni.id, ni.name, i.ip, ipl.mask FROM net_interface ni INNER JOIN ip i ON ni.ip_id = i.id INNER JOIN ippool ipl ON i.ip_pool_id = ipl.id WHERE ni.virtual_execution_id = ? ;";
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setLong(1, execution.getId());
			ResultSet rs = ps.executeQuery();		
			while(rs.next())list.add(new NetInterfaceEntity(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			try{rs.close();ps.close();}catch(Exception e){}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Return a list of deployed virtual machine executions requested by parameter ids
	 * @param ids
	 * @param state
	 * @return
	 */
	public static List<VirtualMachineExecutionEntity> getExecutions(Long[]ids, VirtualMachineExecutionStateEnum state, Connection con){
		try {			
			StringBuilder builder = new StringBuilder();
			for(@SuppressWarnings("unused") Long id: ids){
				builder.append("?,");
			}
			String query = "SELECT vme.id, hp.cores, hp.ram, vme.start_time, vme.stop_time, vme.status, vme.execution_node_id, vme.name, vme.message "
						+ "FROM virtual_machine_execution vme INNER JOIN hardware_profile hp ON vme.hardware_profile_id = hp.id "
						+ "WHERE vme.status = ? AND vme.id in ("+builder.deleteCharAt( builder.length() -1 ).toString()+");";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, state.name());
			int index = 2;
			for(Long idvme: ids){
				ps.setLong(index++, idvme);
			}
			ResultSet rs = ps.executeQuery();
			List<VirtualMachineExecutionEntity> executions = new ArrayList<VirtualMachineExecutionEntity>();
			while(rs.next()){
				PhysicalMachineEntity pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(7), PhysicalMachineStateEnum.ON,con);
				if(pm==null){
					if(state.equals(VirtualMachineExecutionStateEnum.DEPLOYED))				
						setVirtualMachineExecution(new VirtualMachineExecutionEntity(rs.getLong(1), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.RECONNECTING, null, "Lost connection in server"),con);					
					if(state.equals(VirtualMachineExecutionStateEnum.QUEQUED))				
						setVirtualMachineExecution(new VirtualMachineExecutionEntity(rs.getLong(1), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.FAILED, null, "Communication error"),con);	
				}else{
					VirtualMachineExecutionEntity vme = new VirtualMachineExecutionEntity(rs.getLong(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), pm, VirtualMachineExecutionStateEnum.getEnum(rs.getString(6)),rs.getString(8), rs.getString(9));
					executions.add(vme);		
				}
			}		
			try{rs.close();ps.close();}catch(Exception e){}
			return executions;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Return a virtual machine executions based in id and state sent by params
	 * @param id
	 * @param state
	 * @return
	 */
	public static VirtualMachineExecutionEntity getExecution(Long id, VirtualMachineExecutionStateEnum state, Connection con){
		try {			
			String query = "SELECT vme.id, hp.cores, hp.ram, vme.start_time, vme.stop_time, vme.status, vme.execution_node_id, vme.name, vme.message "
						+ "FROM virtual_machine_execution vme INNER JOIN hardware_profile hp ON vme.hardware_profile_id = hp.id "
						+ "WHERE vme.status = ? AND vme.id =?);";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, state.name());
			ps.setLong(2, id);
			ResultSet rs = ps.executeQuery();	
			VirtualMachineExecutionEntity execution = null;
			if(rs.next()){
				PhysicalMachineEntity pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(7), PhysicalMachineStateEnum.ON,con);
				if(pm==null){
					if(state.equals(VirtualMachineExecutionStateEnum.DEPLOYED))				
						setVirtualMachineExecution(new VirtualMachineExecutionEntity(rs.getLong(1), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.RECONNECTING, null, "Lost connection in server"),con);					
					if(state.equals(VirtualMachineExecutionStateEnum.QUEQUED))				
						setVirtualMachineExecution(new VirtualMachineExecutionEntity(rs.getLong(1), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.FAILED, null, "Communication error"),con);	
				}else{
					execution = new VirtualMachineExecutionEntity(rs.getLong(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), pm, VirtualMachineExecutionStateEnum.getEnum(rs.getString(6)),rs.getString(8), rs.getString(9));
				}
			}	
			try{rs.close();ps.close();}catch(Exception e){}
			return execution;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
