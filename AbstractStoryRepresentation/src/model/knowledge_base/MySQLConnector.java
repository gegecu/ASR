package model.knowledge_base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.Connection;

public class MySQLConnector {

	private static String driver = "com.mysql.jdbc.Driver";
	private static String url;
	private static String dbName;
	private static String userName;
	private static String password;
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

	public static MySQLConnector getInstance() {
		return db;
	}

	public Connection getConnection() throws SQLException {
		return (Connection) DriverManager.getConnection(url + dbName, userName,
				password);
	}

}
