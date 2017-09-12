package uniandes.unacloud.share.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Class used to create connection with a database. Uses Apache DBCP to use pool connection
 * This class can be used only to connect with mysql databases
 * @author CesarF
 *
 */
public class DatabaseConnection {
	
	private String host;
	
	private String username ;
	
	private String password ;
	
	private BasicDataSource dataSource;
	
	public DatabaseConnection() {
		
	}
	
	/**
	 * Sets values in data source connection pool
	 */
	private void setConnection(int poolFitSize) {
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");		
        dataSource.setUrl(host);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(poolFitSize);
        dataSource.setMaxWaitMillis(1000*10);
        dataSource.setMaxIdle(poolFitSize);
        dataSource.setMinIdle(poolFitSize);
	}
	
	/**
	 * Set variables to be used in connection.
	 * @param db
	 * @param port
	 * @param ip
	 * @param username
	 * @param password
	 */
	public void connect(String db, int port, String ip, String username, String password, int poolFitSize) {
		this.host = "jdbc:mysql://" + ip + ":" + port + "/" + db + "?useUnicode=yes&characterEncoding=UTF-8";
		this.username = username;
		this.password = password;
		System.out.println("Create connection to: " + this.host);
		setConnection(poolFitSize);
	}
		
	/**
	 * Return connection to pool
	 * @return database connection
	 * @throws SQLException in case connection 
	 */
	public Connection getConnection() throws SQLException{
		return dataSource.getConnection();
	}

}
