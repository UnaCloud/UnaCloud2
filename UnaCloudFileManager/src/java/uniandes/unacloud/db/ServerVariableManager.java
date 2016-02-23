package uniandes.unacloud.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import unacloud.share.enums.ServerVariableTypeEnum;
import uniandes.unacloud.db.entities.ServerVariableEntity;

public class ServerVariableManager {

	/**
	 * Return a list of virtual machines owned by user
	 * @param userId
	 * @return
	 */
	//TODO improve query to repository, use hash map
	public static List<ServerVariableEntity> getAllVariablesForAgent(Connection con){
		try {
			List<ServerVariableEntity> list = new ArrayList<ServerVariableEntity>();	
			String query = "SELECT sv.name, sv.list, sv.server_variable_type, sm.is_public FROM server_variable sv WHERE sv.server_only = 0;";
			PreparedStatement ps = con.prepareStatement(query);		
			ResultSet rs = ps.executeQuery();		
			while(rs.next())list.add(new ServerVariableEntity(rs.getString(1), rs.getString(2), ServerVariableTypeEnum.getEnum(rs.getString(3)), rs.getBoolean(4)));
			try{rs.close();ps.close();}catch(Exception e){}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns a unique variable request by name
	 * @param con
	 * @param name
	 * @return
	 */
	public static ServerVariableEntity getVariable(Connection con, String name){
	
		try {
			ServerVariableEntity variable = null;
			String query = "SELECT sv.name, sv.list, sv.server_variable_type, sm.is_public FROM server_variable sv WHERE sv.name = ?;";
			PreparedStatement ps = con.prepareStatement(query);		
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();		
			if(rs.next())variable = new ServerVariableEntity(rs.getString(1), rs.getString(2), ServerVariableTypeEnum.getEnum(rs.getString(3)), rs.getBoolean(4));
			try{rs.close();ps.close();}catch(Exception e){}
			return variable;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
