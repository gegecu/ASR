package model.knowledge_base.topic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;

import model.knowledge_base.MySQLConnector;

/**
 * Database accessor for specific topic data related queries
 */
public class SpecificTopicDAO {

	/**
	 * Returns list of strings from the `specifictopis` table filtered by
	 * groupName
	 * 
	 * @param groupName
	 *            the name of group
	 * @return list of specific topic data strings
	 */
	public static String[] getTopics(String groupName) {

		String query = "SELECT `topic` "
				+ "FROM `specific_topics` as `st`, `specific_topic_group` as `stg` "
				+ "WHERE `stg`.`id` = `st`.`group_id` AND `stg`.`name` = ?";

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
