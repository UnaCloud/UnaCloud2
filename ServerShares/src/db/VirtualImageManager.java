package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import unacloud.entities.VirtualMachineImageEntity;
import unacloud.enums.VirtualMachineImageEnum;

/**
 * Generic class used to process queries and updates on VirtualMachineImage entity 
 * @author Cesar
 *
 */
public class VirtualImageManager {
	
	/**
	 * Returns a VirtualMachineImage entity requested by id
	 * @param id physical machine id
	 * @return physical machine entity
	 */
	public static VirtualMachineImageEntity getVirtualMachine(Long id, VirtualMachineImageEnum state){
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT vm.id, vm.user, vm.password, vm.token FROM virtual_machine_image vm WHERE vm.state = ? and vm.id = ?;");
			ps.setString(1, state.name());
			ps.setLong(2, id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next())return new VirtualMachineImageEntity(rs.getLong(1), rs.getString(2), rs.getString(3), state, rs.getString(4));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * Updates a virtual machine image entity on database.
	 * @param machine
	 * @return
	 */
	public static boolean setVirtualMachine(VirtualMachineImageEntity image){
		if(image.getId()==null||image.getId()<1)return false;
		try {
			String query = "update virtual_machine_image vm set vm.state = ? where vm.id = ? and vm.id > 0;";
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
	
	/**
	 * Deletes a virtual machine image based in state and id
	 * @param image
	 * @return
	 */
	public static boolean deleteVirtualMachineImage(VirtualMachineImageEntity image){
		if(image.getId()==null||image.getId()<1)return false;
		try {
			String query = "delete virtual_machine_image where state = ? and vm.id = ? and vm.id > 0;";
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, image.getState().name());
			ps.setLong(2, image.getId());
			System.out.println("Delete "+ps.executeUpdate()+" lines");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
