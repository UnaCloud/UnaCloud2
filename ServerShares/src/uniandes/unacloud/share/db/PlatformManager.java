package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.share.db.entities.PlatformEntity;

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
	public static List<PlatformEntity> getAll(Connection con) {
		try {
			List<PlatformEntity> list = new ArrayList<PlatformEntity>();		
			String query = "SELECT hv.id, hv.platform_version, hv.name, hv.main_extension, hv.files_extensions, hv.class_platform FROM platform hv;";
			PreparedStatement ps = con.prepareStatement(query);		
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			while (rs.next())
				list.add(new PlatformEntity(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
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
	 * Gets a platform searched by id
	 * @param id
	 * @param con
	 * @return platform
	 */
	public static PlatformEntity getPlatform(int id, Connection con) {
		try {
			PreparedStatement ps = con.prepareStatement("SELECT re.id, re.name, re.platform_version, re.main_extension, re.files_extensions, re.class_platform FROM platform re WHERE re.id = ?;");
			ps.setLong(1, id);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();	
			PlatformEntity repo = null;
			if (rs.next())
				repo = new PlatformEntity(rs.getLong(1), rs.getString(3), rs.getString(2), rs.getString(4), rs.getString(5), rs.getString(6));
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				
			}
			return repo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
