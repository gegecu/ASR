package model.knowledge_base.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;

import model.knowledge_base.MySQLConnector;

/**
 * Database accessor for data related queries
 */
public class DataDAO {

	/**
	 * Returns list of strings from the `data` table filtered by groupName
	 * 
	 * @param groupName
	 *            the name of group
	 * @return list of data strings
	 */
	public static String[] getData(String groupName) {

		String query = "SELECT `data` "
				+ "FROM `data` as `d`, `data_group` as `dg` "
				+ "WHERE `dg`.`id` = `d`.`group_id` AND `dg`.`name` = ?";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<>();

		try {
			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, groupName);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result.toArray(new String[0]);

	}

}
