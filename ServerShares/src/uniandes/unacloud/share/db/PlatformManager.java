package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.share.entities.PlatformEntity;

/**
 * Class used to execute query, update and delete processes in database for Platform Entity. 
 * @author CesarF
 *
 */
public class PlatformManager {

	/**
	 * Returns the list all of platforms in database
	 * @return all platforms
	 */
	public static List<PlatformEntity> getAll(Connection con){
		try {
			List<PlatformEntity> list = new ArrayList<PlatformEntity>();		
			String query = "SELECT hv.id, hv.hypervisor_version, hv.name, hv.main_extension, hv.files_extensions FROM hypervisor hv;";
			PreparedStatement ps = con.prepareStatement(query);		
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			while(rs.next())list.add(new PlatformEntity(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
			try{rs.close();ps.close();}catch(Exception e){}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
