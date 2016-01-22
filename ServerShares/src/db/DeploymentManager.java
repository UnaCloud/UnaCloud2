package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeMap;

import unacloud.entities.DeployedImage;
import unacloud.entities.Deployment;
import unacloud.entities.PhysicalMachine;
import unacloud.entities.VirtualMachineExecution;
import unacloud.entities.VirtualMachineImage;
import unacloud.enums.DeploymentStateEnum;
import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.enums.VirtualMachineImageEnum;

public class DeploymentManager {
	
	/**
	 * Queries and returns a Deployment reuqest by id in parameters
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
				ps = con.prepareStatement("SELECT vme.id, hp.cores, hp.ram, vme.start_time, vme.stop_time, vme.status, vme.execution_node_id, vmi.id, vmi.user, vmi.password, vmi.state "
						+ "FROM virtual_machine_execution vme INNER JOIN hardware_profile hp ON vme.hardware_profile_id = hp.id INNER JOIN deployed_image dp ON dp.id = vme.deploy_image_id "
						+ "INNER JOIN virtual_machine_image vmi ON dp.image_id = vmi.id WHERE dp.deployment_id = ? AND vme.status = ?;");
				ps.setLong(1, id);
				ps.setString(2, VirtualMachineExecutionStateEnum.QUEQUED.name());
				rs = ps.executeQuery();	
				TreeMap<Long, DeployedImage> executions = new TreeMap<Long, DeployedImage>();
				while(rs.next()){
					PhysicalMachine pm = PhysicalMachineManager.getPhysicalMachine(rs.getLong(7), PhysicalMachineStateEnum.ON);
					if(pm==null)setVirtualMachineExecution(new VirtualMachineExecution(rs.getLong(1), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.FAILED));
					else{
						VirtualMachineExecution vme = new VirtualMachineExecution(rs.getLong(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), pm, VirtualMachineExecutionStateEnum.getEnum(rs.getString(6)));
						if(executions.get(rs.getLong(8))==null)
							executions.put(rs.getLong(8), new DeployedImage(new VirtualMachineImage(rs.getLong(8), rs.getString(9), rs.getString(10), VirtualMachineImageEnum.getEnum(rs.getString(11))),new ArrayList<VirtualMachineExecution>()));
						executions.get(rs.getLong(8)).getExecutions().add(vme);						
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
//		if(execution.getId()==null||execution.getId()<1)return false;
//		try {
//			String query = "update virtual_machine_execution vme "; 
//			int status = 0;
//			int report = 0;
//			if(machine.getStatus()!=null){query+=" set pm.state = ? ";status = 1;}
//			if(machine.getLastReport()!=null){query+=(status>0?",":"")+" set pm.last_report = ? ";report=status+1;};
//			if(status>0||report>0){
//				query += "where pm.id = ? and pm.id > 0;";
//				Connection con = DatabaseConnection.getInstance().getConnection();
//				PreparedStatement ps = con.prepareStatement(query);
//				int id = 0;
//				if(status>0){ps.setString(status, machine.getStatus().name());id++;};
//				if(report>0){ps.setDate(report, new java.sql.Date(machine.getLastReport().getTime()));id++;};
//				ps.setLong(id, machine.getId());
//				System.out.println("Change "+ps.executeUpdate()+" lines");
//				return true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();			
//		}		
		return false;
	}

}
