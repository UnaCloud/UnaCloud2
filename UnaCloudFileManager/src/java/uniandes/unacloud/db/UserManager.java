package uniandes.unacloud.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import uniandes.unacloud.db.entities.User;

import db.DatabaseConnection;

/**
 * Generic class used to query and update User entity in database
 * @author Cesar
 *
 */
public class UserManager {
	
	/**
	 * Return a User entity request by param id
	 * @param id from user
	 * @return User
	 */
	public static User getUser(Long id){
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT u.id, u.username FROM user u WHERE u.id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next())return new User(rs.getLong(1),rs.getString(2));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

}
