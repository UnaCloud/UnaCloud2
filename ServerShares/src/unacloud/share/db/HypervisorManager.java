package unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import unacloud.share.entities.HypervisorEntity;

/**
 * Class used to execute query, update and delete processes in database for Hypervisor Entity. 
 * @author CesarF
 *
 */
public class HypervisorManager {

	/**
	 * Returns the list all of hypervisors in database
	 * @return all hypervisors
	 */
	public static List<HypervisorEntity> getAllHypervisors(Connection con){
		try {
			List<HypervisorEntity> list = new ArrayList<HypervisorEntity>();		
			String query = "SELECT hv.id, hv.hypervisor_version, hv.name, hv.main_extension, hv.files_extensions FROM hypervisor hv;";
			PreparedStatement ps = con.prepareStatement(query);		
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			while(rs.next())list.add(new HypervisorEntity(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
			try{rs.close();ps.close();}catch(Exception e){}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
