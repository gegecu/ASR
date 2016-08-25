package model.knowledge_base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.Connection;

import view.mode.dialog.OkDialog;

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

	private static String dir = "mysql.properties";

	private MySQLConnector() {

	}

	static {
		try {

			Class.forName(driver).newInstance();

			Properties props = new Properties();

			if (!new File(dir).exists()) {

				OutputStream output = new FileOutputStream(dir);

				props.setProperty("mysql.url", "jdbc:mysql://localhost:3306/");
				props.setProperty("mysql.database_name",
						"alice?autoReconnect=true&useSSL=false");
				props.setProperty("mysql.username", "root");
				props.setProperty("mysql.password", "1234");

				props.store(output, null);

				new OkDialog("MySQL Properties Not Found",
						"Edit the mysql.properties file to the settings of the MySQL Server")
								.setVisible(true);

				System.exit(0);

			}

			FileInputStream in = new FileInputStream(dir);
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
