package uniandes.unacloud.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.losandes.utils.UnaCloudConstants;

import unacloud.share.enums.UserRestrictionEnum;
import unacloud.share.enums.UserStateEnum;
import uniandes.unacloud.db.entities.UserEntity;
import unacloud.share.db.RepositoryManager;

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
	public static UserEntity getUser(Long id,Connection con){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT u.id, u.username, u.status FROM user u WHERE u.id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();	
			UserEntity user = null;
			if(rs.next())user = new UserEntity(rs.getLong(1),rs.getString(2), UserStateEnum.getEnum(rs.getString(3)));
			try{rs.close();ps.close();}catch(Exception e){}
			return user;
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
	public static UserEntity getUserWithRepository(Long id,Connection con){
		try {
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
			try{rs.close();ps.close();}catch(Exception e){}
			user.setRepository(RepositoryManager.getRepositoryByName(repository==null?UnaCloudConstants.MAIN_REPOSITORY:repository,con));
			
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
	public static void deleteUser(UserEntity user,Connection con){
		if(user.getId()==null||user.getId()<1)return;
		try {
			String query = "delete from user where status = ? and id = ? and id > 0;";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, UserStateEnum.DISABLE.getName());
			ps.setLong(2, user.getId());
			System.out.println("Delete User "+ps.executeUpdate()+" lines");
			try{ps.close();}catch(Exception e){}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
