package model.knowledge_base;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class MySQLConnector {

	private String url = "jdbc:mysql://localhost:3306/";
	private String dbName = "alice?autoReconnect=true&useSSL=false";
	private String driver = "com.mysql.jdbc.Driver";
	private String userName = "root";
	private String password = "";
	private Connection connection;
	private static MySQLConnector db = new MySQLConnector();

	private MySQLConnector() {
		try {
			Class.forName(driver).newInstance();
		} catch (InstantiationException e1) {
		} catch (IllegalAccessException e2) {
		} catch (ClassNotFoundException e3) {
		}
	}

	public static MySQLConnector getInstance() {
		return db;
	}

	public Connection getConnection() throws SQLException {
		this.connection = (Connection) DriverManager.getConnection(url + dbName,
				userName, password);
		return connection;
	}

}
