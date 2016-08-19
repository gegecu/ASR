package model.knowledge_base.conceptnet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;

import model.knowledge_base.MySQLConnector;

/**
 * Database accessor for concept related queries
 */
public class ConceptNetDAO {

	public ConceptNetDAO() {
		super();
	}

	/**
	 * Checks if the concept relation exists with the start word, relation, and
	 * end word in the knowledge database
	 * 
	 * @param start
	 *            the start word of the concept relation
	 * @param relation
	 *            the relation of the concept relation
	 * @param end
	 *            the end word of the concept relation
	 * @return returns true if the concept relation exists
	 */
	public static boolean conceptExists(String start, String relation,
			String end) {

		String query = "select `conceptsFrom`.`concept`, `relations`.`relation`, `conceptsTo`.`concept` "
				+ "from (select `id`, `concept` from `concepts`) as `conceptsFrom`,`relations` as `relations`, `concept_relations` as `concept_relations` "
				+ "left join `concepts` as `conceptsTo` on `concept_relations`.`toID` = `conceptsTo`.`id` "
				+ " where `concept_relations`.`fromID` = `conceptsFrom`.`id` and `concept_relations`.`relationID` = `relations`.`id` "
				+ "and `conceptsFrom`.`concept` = ? and `relations`.`relation` = ? and `conceptsTo`.`concept` = ?";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, start);
			ps.setString(2, relation);
			ps.setString(3, end);
			rs = ps.executeQuery();

			if (rs.next()) {
				result = true;
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
	 * Returns list of concept relations having the end param as the end word
	 * and the relation param as the relation
	 * 
	 * @param end
	 *            the end word of the concept relation
	 * @param relation
	 *            the relation of the concept relation
	 * @return list of concept relations having the end param as the end word
	 *         and the relation param as the relation
	 */
	public static List<Concept> getConceptsFrom(String end, String relation) {

		String query = "select `concept_relations`.`id`, `conceptsFrom`.`concept`, `conceptsFromPOS`.`partOfSpeech`,  `relations`.`relation`,  `conceptsTo`.`concept`, `conceptsToPOS`.`partOfSpeech` "
				+ "FROM (SELECT  `id`, `concept`, `posID` FROM `concepts`) AS `conceptsFrom` LEFT JOIN `part_of_speeches` AS `conceptsFromPOS` ON `conceptsFrom`.`posID` = `conceptsFromPOS`.`id`, "
				+ "`concept_relations` AS `concept_relations` LEFT JOIN `concepts` AS `conceptsTo` ON `concept_relations`.`toID` = `conceptsTo`.`id` "
				+ "LEFT JOIN `relations` AS `relations` ON `concept_relations`.`relationID` = `relations`.`id` "
				+ "LEFT JOIN `part_of_speeches` AS `conceptsToPOS` ON `conceptsTo`.`posID` = `conceptsToPOS`.`id` "
				+ "WHERE `concept_relations`.`fromID` = `conceptsFrom`.`id` "
				+ "and `conceptsTo`.`concept` = ? and `relations`.`relation` = ?";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Concept> concepts = null;

		try {
			concepts = new ArrayList<Concept>();
			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, end);
			ps.setString(2, relation);
			rs = ps.executeQuery();

			while (rs.next()) {
				concepts.add(new Concept(rs.getInt(1), rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6)));
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

		return concepts;

	}

	/**
	 * Returns list of concept relations having the start param as the start
	 * word and the relation param as the relation
	 * 
	 * @param start
	 *            the start word of the concept relation
	 * @param relation
	 *            the relation of the concept relation
	 * @return list of concept relations having the start param as the start
	 *         word and the relation param as the relation
	 */
	public static List<Concept> getConceptsTo(String start, String relation) {

		String query = "select `concept_relations`.`id`, `conceptsFrom`.`concept`, `conceptsFromPOS`.`partOfSpeech`,  `relations`.`relation`,  `conceptsTo`.`concept`, `conceptsToPOS`.`partOfSpeech` "
				+ "FROM (SELECT  `id`, `concept`, `posID` FROM `concepts`) AS `conceptsFrom` LEFT JOIN `part_of_speeches` AS `conceptsFromPOS` ON `conceptsFrom`.`posID` = `conceptsFromPOS`.`id`, "
				+ "`concept_relations` AS `concept_relations` LEFT JOIN `concepts` AS `conceptsTo` ON `concept_relations`.`toID` = `conceptsTo`.`id` "
				+ "LEFT JOIN `relations` AS `relations` ON `concept_relations`.`relationID` = `relations`.`id` "
				+ "LEFT JOIN `part_of_speeches` AS `conceptsToPOS` ON `conceptsTo`.`posID` = `conceptsToPOS`.`id` "
				+ "WHERE `concept_relations`.`fromID` = `conceptsFrom`.`id` "
				+ "and `conceptsFrom`.`concept` = ? and `relations`.`relation` = ?";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Concept> concepts = null;

		try {
			concepts = new ArrayList<Concept>();
			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, start);
			ps.setString(2, relation);
			rs = ps.executeQuery();

			while (rs.next()) {
				concepts.add(new Concept(rs.getInt(1), rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6)));
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

		return concepts;

	}

	/**
	 * Checks if the conflict word has relations in the knowledge database
	 * 
	 * @param conflict
	 *            the word to be checked
	 * @return returns true if the word has relations in the knowledge database
	 */
	public static boolean checkResolutionExists(String conflict) {
		String query = "SELECT c1.concept AS lev1, c2.concept as lev2, c3.concept as lev3 "
				+ "FROM concept_relations AS t1 "
				+ "LEFT JOIN concepts AS c1 ON c1.id = t1.toID "
				+ "LEFT JOIN concept_relations AS t2 ON t2.fromID = t1.toID "
				+ "LEFT JOIN concepts AS c2 ON c2.id = t2.toID "
				+ "LEFT JOIN concept_relations AS t3 ON t3.fromID = t2.toID "
				+ "LEFT JOIN concepts AS c3 ON c3.id = t3.toID "
				+ "WHERE t1.fromID in (SELECT id FROM CONCEPTS WHERE concept = ?)";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean resolutionExist = false;

		try {

			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, conflict);
			rs = ps.executeQuery();

			if (rs.next()) {
				resolutionExist = true;
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

		return resolutionExist;

	}

	/**
	 * Checks if the conflict word has related to the resolution word within 4
	 * hops
	 * 
	 * @param conflict
	 *            the word to be checked
	 * @param possibleResolution
	 *            the resolution to be checked
	 * @return returns true if the conflict word has related to the resolution
	 *         word within 4 hops
	 */
	public static boolean checkFourHops(String conflict,
			String possibleResolution) {

		// changed to t1.fromID in (Select ...) from ( id = ... limit 1)
		String query = "SELECT c1.concept AS lev1, c2.concept as lev2, c3.concept as lev3 "
				+ "FROM concept_relations AS t1 "
				+ "LEFT JOIN concepts AS c1 ON c1.id = t1.toID "
				+ "LEFT JOIN concept_relations AS t2 ON t2.fromID = t1.toID "
				+ "LEFT JOIN concepts AS c2 ON c2.id = t2.toID "
				+ "LEFT JOIN concept_relations AS t3 ON t3.fromID = t2.toID "
				+ "LEFT JOIN concepts AS c3 ON c3.id = t3.toID "
				+ "WHERE t1.fromID in (SELECT id FROM CONCEPTS WHERE concept = ?)";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean pathExists = false;

		try {

			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, conflict);
			rs = ps.executeQuery();

			outer : while (rs.next()) {

				String[] nodesInRow = {rs.getString(1), rs.getString(2),
						rs.getString(3)};

				for (int i = 0; i < nodesInRow.length; i++) {
					if (nodesInRow[i] != null
							&& nodesInRow[i].equals(possibleResolution)) {
						pathExists = true;
						break outer;
					}
				}

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

		return pathExists;

	}

}
