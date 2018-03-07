package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import uniandes.unacloud.share.db.entities.RepositoryEntity;

/**
 * Class used to execute query, update and delete processes in database for Repository Entity. 
 * @author CesarF
 *
 */
public class StorageManager {

	
	/**
	 * Returns a repository searched by name
	 * @param name of repository
	 * @param con Database Connection
	 * @return repository entity
	 */
	public static RepositoryEntity getRepositoryByName(String name, Connection con){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT re.id, re.name, re.capacity, re.path FROM repository re WHERE re.name = ?;");
			ps.setString(1,name);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			RepositoryEntity repo = null;
			if (rs.next()) 
				repo =  new RepositoryEntity(rs.getLong(1), rs.getString(2), rs.getInt(3), rs.getString(4));
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
	
	/**
	 * Returns a repository entity from database
	 * @param id from repository
	 * @param con Database Connection
	 * @return repository entity
	 */
	public static RepositoryEntity getRepository(Long id,Connection con){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT re.id, re.name, re.capacity, re.path FROM repository re WHERE re.id = ?;");
			ps.setLong(1, id);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();	
			RepositoryEntity repo = null;
			if (rs.next())
				repo = new RepositoryEntity(rs.getLong(1), rs.getString(2), rs.getInt(3), rs.getString(4));
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
