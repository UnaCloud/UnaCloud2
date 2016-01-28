package uniandes.unacloud.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.losandes.utils.Constants;

import unacloud.enums.UserRestrictionEnum;
import unacloud.enums.UserStateEnum;
import uniandes.unacloud.db.entities.UserEntity;
import db.DatabaseConnection;
import db.RepositoryManager;

/**
 * Generic class used to query and update User entity in database
 * @author Cesar
 *
 */
public class UserManager {
	
	/**
	 * Returns an User entity request by param id
	 * @param id from user
	 * @return User
	 */
	public static UserEntity getUser(Long id){
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT u.id, u.username, u.status FROM user u WHERE u.id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next())return new UserEntity(rs.getLong(1),rs.getString(2), UserStateEnum.getEnum(rs.getString(3)));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns an user entity with allow repository
	 * @param id
	 * @return
	 */
	public static UserEntity getUserWithRepository(Long id){
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT u.id, u.username, usr.val, u.status FROM user u LEFT JOIN (SELECT uur.user_restrictions_id as id, ur.value as val from user_user_restriction uur INNER JOIN user_restriction ur ON uur.user_restriction_id = ur.id WHERE ur.name = ? AND uur.user_restrictions_id = ?) usr ON u.id = usr.id WHERE u.id = ? ;");
			ps.setString(1, UserRestrictionEnum.REPOSITORY.name());
			ps.setLong(2, id);
			ps.setLong(3, id);
			ResultSet rs = ps.executeQuery();
			String repository = null;
			UserEntity user = null;
			if(rs.next()){
				user= new UserEntity(rs.getLong(1),rs.getString(2), UserStateEnum.getEnum(rs.getString(4)));
				repository = rs.getString(3);
			}
			user.setRepository(RepositoryManager.getRepositoryByName(repository==null?Constants.MAIN_REPOSITORY:repository));
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Deletes a user and all clusters, images, external accounts, user restrictions where he is owner
	 * @param user
	 */
	public static void deleteUser(UserEntity user){
		
	}

}
