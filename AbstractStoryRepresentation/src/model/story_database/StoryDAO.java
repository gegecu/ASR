package model.story_database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;

import model.knowledge_base.MySQLConnector;

public class StoryDAO {

	/**
	 * @param story
	 *            needs story title and story text
	 * @return boolean if save is successful or not
	 */
	public static boolean saveStory(Story story) {

		String query = "INSERT INTO `stories` (`title`, `text`) values (?, ?)";

		Connection connection = null;
		PreparedStatement ps = null;
		boolean result = true;

		try {

			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, story.getStoryTitle());
			ps.setString(2, story.getStoryBody());

			ps.executeUpdate();

		} catch (SQLException e) {
			result = false;
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;

	}

	/**
	 * Does not show up in the main menu, because it is 'deleted'
	 * 
	 * @param story
	 *            needs story title and story text
	 * @return boolean if save is successful or not
	 */
	public static boolean saveUnfinishedStory(Story story) {

		String query = "INSERT INTO `stories` (`title`, `text`, `deleted`) values (?, ?, true)";

		Connection connection = null;
		PreparedStatement ps = null;
		boolean result = true;

		try {

			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, story.getStoryTitle());
			ps.setString(2, story.getStoryBody());

			ps.executeUpdate();

		} catch (SQLException e) {
			result = false;
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;

	}

	/**
	 * @return list of stories (story id and story title, story text not
	 *         included) (does not include "deleted" stories)
	 */
	public static List<Story> getSavedStories() {

		String query = "SELECT `id`, `title` FROM `stories` WHERE `deleted` = false ORDER BY `id` DESC";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Story> result = new ArrayList<>();

		try {

			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {
				result.add(new Story(rs.getInt(1), rs.getString(2)));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;

	}

	/**
	 * @param storyId
	 *            id of story to be deleted
	 * @return boolean if delete is successful or not
	 */
	public static boolean deleteStory(int storyId) {

		String query = "UPDATE `stories` SET `deleted` = true WHERE `id` = ? ";

		Connection connection = null;
		PreparedStatement ps = null;
		boolean result = true;

		try {

			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, storyId);

			ps.executeUpdate();

		} catch (SQLException e) {
			result = false;
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;

	}

	/**
	 * @param storyId
	 *            id of story to be retrieved
	 * @return string (the story text of the story id)
	 */
	public static Story getStory(int storyId) {

		String query = "SELECT `id`, `title`, `text` FROM `stories` WHERE `id` = ?";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Story result = null;

		try {

			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, storyId);

			rs = ps.executeQuery();

			if (rs.next()) {
				result = new Story(rs.getInt(1), rs.getString(2),
						rs.getString(3));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;

	}

}
