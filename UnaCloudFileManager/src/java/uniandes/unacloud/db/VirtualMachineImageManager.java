package uniandes.unacloud.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.losandes.utils.Constants;

import unacloud.enums.VirtualMachineImageEnum;
import uniandes.unacloud.db.entities.Repository;
import uniandes.unacloud.db.entities.VirtualImageFile;
import db.DatabaseConnection;
import db.VirtualImageManager;

public class VirtualMachineImageManager extends VirtualImageManager{
	
	/**
	 * Method used to return a virtual machine image entity with information about file and repository
	 * @return
	 */
	public static VirtualImageFile getVirtualImageWithFile(Long id, VirtualMachineImageEnum state){
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT vm.id, vm.fixed_disk_size, vm.is_public, vm.main_file, vm.repository_id, vm.token FROM virtual_machine_image vm WHERE vm.state == ? and vm.id = ?;");
			ps.setString(1, state.name());
			ps.setLong(2, id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next()){
				return new VirtualImageFile(rs.getLong(1), state, rs.getString(6), getRepository(rs.getLong(5)), rs.getBoolean(3), rs.getLong(2), rs.getString(4));
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Return the main repository in system
	 * @return
	 */
	public static Repository getMainRepository(){
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT re.id, re.name, re.capacity, re.path FROM repository re WHERE re.name == ?;");
			ps.setString(1, Constants.MAIN_REPOSITORY);
			ResultSet rs = ps.executeQuery();			
			if(rs.next())return new Repository(rs.getLong(1), rs.getString(2), rs.getInt(3), rs.getString(4));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Return a repository entity from database
	 * @param id
	 * @return
	 */
	public static Repository getRepository(Long id){
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT re.id, re.name, re.capacity, re.path FROM repository re WHERE re.id == ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next())return new Repository(rs.getLong(1), rs.getString(2), rs.getInt(3), rs.getString(4));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
