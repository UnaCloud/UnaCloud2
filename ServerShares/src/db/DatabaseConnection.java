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
public class DatabaseConnection {
	
	private String host;
	private String username ;
	private String password ;
	
	private BasicDataSource dataSource;
	public static DatabaseConnection instance;
	
	public DatabaseConnection(){
	}
	
	public static DatabaseConnection getInstance(){
		if(instance==null)instance = new DatabaseConnection();
		return instance;
	}
	
	/**
	 * Sets values in data source connection pool
	 */
	private void setConnection() {

		System.out.println("hola");
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		
        dataSource.setUrl(host);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
	}
	/**
	 * Set variables to be used in connection.
	 * @param db
	 * @param port
	 * @param ip
	 * @param username
	 * @param password
	 */
	public void connect(String db, int port, String ip, String username, String password){
		this.host = "jdbc:mysql://"+ip+":"+port+"/"+db+"?useUnicode=yes&characterEncoding=UTF-8";
		this.username = username;
		this.password = password;
		System.out.println(host);
		setConnection();
	}
		
	/**
	 * Return connection to pool
	 * @return
	 * @throws SQLException in case connection 
	 */
	public Connection getConnection() throws SQLException{
		return dataSource.getConnection();
	}

}
