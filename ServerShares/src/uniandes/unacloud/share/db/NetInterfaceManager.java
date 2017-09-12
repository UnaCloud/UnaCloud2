package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.share.db.entities.ExecutionEntity;
import uniandes.unacloud.share.db.entities.NetInterfaceEntity;

/**
 * Class responsible to manage NetInterface by JDBC
 * @author CesarF
 *
 */
public class NetInterfaceManager {

	/**
	 * Returns a list of configured interfaces for an Execution
	 * @param execution to find net interfaces
	 * @param con Database connection
	 * @return list of net interfaces configured for image
	 */
	public static List<NetInterfaceEntity> getInterfaces(ExecutionEntity execution, Connection con) {
		try {
			List<NetInterfaceEntity> list = new ArrayList<NetInterfaceEntity>();		
			String query = "SELECT ni.id, ni.name, i.ip, ipl.mask "
							+ "FROM net_interface ni "
								+ "INNER JOIN ip i ON ni.ip_id = i.id "
								+ "INNER JOIN ippool ipl ON i.ip_pool_id = ipl.id "
							+ "WHERE ni.execution_id = ? ;";
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setLong(1, execution.getId());
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			while (rs.next())
				list.add(new NetInterfaceEntity(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4)));
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
}
