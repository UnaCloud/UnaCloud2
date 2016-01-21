package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import unacloud.entities.VirtualMachineImage;
import unacloud.enums.VirtualMachineImageEnum;

/**
 * Generic class used to process queries and updates on VirtualMachineImage entity 
 * @author Cesar
 *
 */
public class VirtualImageManager {
	
	/**
	 * Return a VirtualMachineImage entity requested by id
	 * @param id physical machine id
	 * @return physical machine entity
	 */
	public static VirtualMachineImage getVirtualMachine(Long id, VirtualMachineImageEnum state){
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT vm.id, vm.user, vm.password FROM virtual_machine_image vm WHERE vm.state == '"+state.name()+"' and pm.id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next())return new VirtualMachineImage(rs.getLong(1), rs.getString(2), rs.getString(3), state);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * Update a virtual machine image entity on database.
	 * @param machine
	 * @return
	 */
	public static boolean setVirtualMachine(VirtualMachineImage image){
		if(image.getId()==null||image.getId()<1)return false;
		try {
			String query = "update virtual_machine_image vm set vm.state = ? where vm.id = ? and pm.id > 0;";
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, image.getState().name());
			ps.setLong(2, image.getId());
			System.out.println("Change "+ps.executeUpdate()+" lines");
			return true;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
}
