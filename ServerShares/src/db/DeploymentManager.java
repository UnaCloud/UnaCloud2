package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import unacloud.entities.DeployedImage;
import unacloud.entities.Deployment;
import unacloud.entities.NetInterface;
import unacloud.entities.PhysicalMachine;
import unacloud.entities.VirtualMachineExecution;
import unacloud.entities.VirtualMachineImage;
import unacloud.enums.DeploymentStateEnum;
import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.enums.VirtualMachineImageEnum;

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
	public static Deployment getDeployment(Long id){
		try {
			Deployment deploy = null;
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT dp.id, dp.start_time, dp.stop_time, dp.status FROM deployment dp WHERE dp.status == ? and dp.id = ?;");
			ps.setString(1, DeploymentStateEnum.ACTIVE.name());
			ps.setLong(2, id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next()){
				deploy = new Deployment();
				deploy.setId(rs.getLong(1));
				deploy.setStartTime(rs.getDate(2));
				deploy.setStopTime(rs.getDate(3));
				deploy.setState(DeploymentStateEnum.ACTIVE);
			}
			rs.close();
			ps.close();
			if(deploy!=null){
				ps = con.prepareStatement("SELECT vme.id, hp.cores, hp.ram, vme.start_time, vme.stop_time, vme.status, vme.execution_node_id, vme.name, vmi.id, vmi.user, vmi.password, vmi.state"
						+ "FROM virtual_machine_execution vme INNER JOIN hardware_profile hp ON vme.hardware_profile_id = hp.id INNER JOIN deployed_image dp ON dp.id = vme.deploy_image_id "
						+ "INNER JOIN virtual_machine_image vmi ON dp.image_id = vmi.id WHERE dp.deployment_id = ? AND vme.status = ?;");
				ps.setLong(1, id);
				ps.setString(2, VirtualMachineExecutionStateEnum.QUEQUED.name());
				rs = ps.executeQuery();	
				TreeMap<Long, DeployedImage> executions = new TreeMap<Long, DeployedImage>();
				while(rs.next()){
					PhysicalMachine pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(7), PhysicalMachineStateEnum.ON);
					if(pm==null)setVirtualMachineExecution(new VirtualMachineExecution(rs.getLong(1), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null));
					else{
						VirtualMachineExecution vme = new VirtualMachineExecution(rs.getLong(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), pm, VirtualMachineExecutionStateEnum.getEnum(rs.getString(6)),rs.getString(8));
						if(executions.get(rs.getLong(9))==null)
							executions.put(rs.getLong(9), new DeployedImage(new VirtualMachineImage(rs.getLong(9), rs.getString(10), rs.getString(11), VirtualMachineImageEnum.getEnum(rs.getString(12))),new ArrayList<VirtualMachineExecution>()));
						executions.get(rs.getLong(9)).getExecutions().add(vme);						
					}
				}
				for(DeployedImage image:deploy.getImages()){
					for(VirtualMachineExecution execution: image.getExecutions()){
						execution.getInterfaces().addAll(getInterfaces(execution));
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
	 * Update a virtual machine execution entity on database.
	 * @param machine
	 * @return
	 */
	public static boolean setVirtualMachineExecution(VirtualMachineExecution execution){
		if(execution.getId()==null||execution.getId()<1)return false;
		try {
			String query = "update virtual_machine_execution vme "; 
			int start = 0;
			int stop = 0;
			int state = 0;
			if(execution.getStartTime()!=null){query+=" set vme.start_time = ? ";start = 1;}
			if(execution.getStopTime()!=null){query+=(start>0?",":"")+" set vme.stop_time = ? ";stop=start+1;};
			if(execution.getState()!=null){query+=(start>0||stop>0?",":"")+" set vme.status= ? ";state=stop+1;};
			if(state>0||stop>0||start>0){
				query += "where vme.id = ? and vme.id > 0;";
				Connection con = DatabaseConnection.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(query);
				int id = 0;
				if(start>0){ps.setDate(start, new java.sql.Date(execution.getStartTime().getTime()));id++;};
				if(stop>0){ps.setDate(stop, new java.sql.Date(execution.getStopTime().getTime()));id++;};
				if(state>0){ps.setString(state, execution.getState().name());id++;}
				ps.setLong(id, execution.getId());
				System.out.println("Change "+ps.executeUpdate()+" lines");
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
	public static List<NetInterface> getInterfaces(VirtualMachineExecution execution){
		try {
			List<NetInterface> list = new ArrayList<NetInterface>();
			Connection con = DatabaseConnection.getInstance().getConnection();			
			String query = "SELECT ni.id, ni.name, i.ip, ipl.mask FROM net_interface ni INNER JOIN ip i ON ni.ip_id = i.id INNER JOIN ippool ipl ON i.ip_pool_id = ipl.id WHERE ni.virtual_execution_id == ? ;";
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setLong(1, execution.getId());
			ResultSet rs = ps.executeQuery();		
			while(rs.next())list.add(new NetInterface(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
