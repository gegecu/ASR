package model.knowledge_base.template;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;

import model.knowledge_base.MySQLConnector;

/**
 * Database accessor for template related queries
 */
public class TemplateDAO {

	/**
	 * Returns list of strings from the `templates` table filtered by groupName
	 * 
	 * @param groupName
	 *            the name of group
	 * @return list of template strings
	 */
	public static String[] getTemplates(String groupName) {

		String query = "SELECT `template` "
				+ "FROM `templates` as `t`, `template_group` as `tg` "
				+ "WHERE `tg`.`id` = `t`.`group_id` AND `tg`.`name` = ?";

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
