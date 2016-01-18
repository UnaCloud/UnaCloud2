package db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Class used to create connection with a database. Uses Apache DBCP to use pool connection
 * This class can be used to connect with mysql databases
 * @author Cesar
 *
 */
public final class DatabaseConnection {
	
	private String host;
	private String username ;
	private String password ;
	
	private static final BasicDataSource dataSource = new BasicDataSource();
	
	/**
	 * Sets values in data source connection pool
	 */
	private void setConnection() {
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(host);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
	}
	
	public void connect(String db, int port, String ip, String username, String password){
		this.host = "jdbc:mysql://"+ip+":"+port+"/"+db+"?useUnicode=yes&characterEncoding=UTF-8";
		this.username = username;
		this.password = password;
		setConnection();
	}
	
	/**
	 * Return connection to pool
	 * @return
	 * @throws SQLException in case connection 
	 */
	public static Connection connection() throws SQLException{
		return dataSource.getConnection();
	}

}
