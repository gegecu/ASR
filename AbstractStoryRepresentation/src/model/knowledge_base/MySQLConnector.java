package model.knowledge_base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.Connection;

/**
 * MySQL connection creator
 */
public class MySQLConnector {

	/**
	 * Used to store MySQL Driver Class Path
	 */
	private static String driver = "com.mysql.jdbc.Driver";
	/**
	 * Used to store MySQL connection database url
	 */
	private static String url;
	/**
	 * Used to store MySQL connection database name
	 */
	private static String dbName;
	/**
	 * Used to store MySQL connection username
	 */
	private static String userName;
	/**
	 * Used to store MySQL connection password
	 */
	private static String password;
	/**
	 * Singleton MySQLConnector instance
	 */
	private static MySQLConnector db;

	private MySQLConnector() {

	}

	static {
		try {

			Class.forName(driver).newInstance();

			Properties props = new Properties();
			FileInputStream in = new FileInputStream("files/mysql.properties");
			props.load(in);
			in.close();

			url = props.getProperty("mysql.url");
			dbName = props.getProperty("mysql.database_name");
			userName = props.getProperty("mysql.username");
			password = props.getProperty("mysql.password");

			db = new MySQLConnector();

		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the singleton MySQLConnector instance
	 * 
	 * @return singleton MySQLConnector instance
	 */
	public static MySQLConnector getInstance() {
		return db;
	}

	/**
	 * Returns A new MySQL Connection instance everytime
	 * 
	 * @return MySQL Connection instance
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return (Connection) DriverManager.getConnection(url + dbName, userName,
				password);
	}

}
