package model.knowledge_base;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class MySQLConnector {
	private String url= "jdbc:mysql://localhost:3306/";
	private String dbName = "alice";
	private String driver = "com.mysql.jdbc.Driver";
	private String userName = "root";
	private String password = "1234";
	private Connection connection;
    private static MySQLConnector db = new MySQLConnector();
    
    private MySQLConnector() {
        try {
            Class.forName(driver).newInstance();

        }
        catch (InstantiationException e1) {

        } catch (IllegalAccessException e2) {
        	
        } catch (ClassNotFoundException e3) {
        	
        }
    }
    
    public static MySQLConnector getInstance() {
    	return db;
    }
    
    public Connection getConnection() {
        try {
			this.connection = (Connection)DriverManager.getConnection(url+dbName,userName,password);
			return connection;
		} catch (SQLException e) {
		}
    	return null;
    }
    
    
    
    
}
