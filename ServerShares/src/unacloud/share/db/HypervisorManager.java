package unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import unacloud.share.entities.HypervisorEntity;

/**
 * Generic class used to query and update User entity in database
 * @author Cesar
 *
 */
public class HypervisorManager {

	/**
	 * Returns the list all of hypervisors in database
	 * @return
	 */
	public static List<HypervisorEntity> getAllHypervisors(){
		try {
			List<HypervisorEntity> list = new ArrayList<HypervisorEntity>();
			Connection con = DatabaseConnection.getInstance().getConnection();			
			String query = "SELECT hv.id, hv.hypervisor_version, hv.name, hv.main_extension , hv.files_extensions FROM hypervisor hv;";
			PreparedStatement ps = con.prepareStatement(query);		
			ResultSet rs = ps.executeQuery();		
			while(rs.next())list.add(new HypervisorEntity(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
