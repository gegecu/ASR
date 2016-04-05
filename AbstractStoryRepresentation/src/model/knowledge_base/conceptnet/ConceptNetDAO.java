package model.knowledge_base.conceptnet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.knowledge_base.MySQLConnector;

import com.mysql.jdbc.Connection;

public class ConceptNetDAO{

	public ConceptNetDAO() {
		super();
	}
	
	public static boolean checkSRL(String start, String relation, String end) {
		String query = 
				"select `conceptsFrom`.`concept`, `relations`.`relation`, `conceptsTo`.`concept` " +
				"from (select `id`, `concept` from `concepts`) as `conceptsFrom`,`relations` as `relations`, `concept_relations` as `concept_relations` " + 
				"left join `concepts` as `conceptsTo` on `concept_relations`.`toID` = `conceptsTo`.`id` " + 
				" where `concept_relations`.`fromID` = `conceptsFrom`.`id` and `concept_relations`.`relationID` = `relations`.`id` " +
				"and `conceptsFrom`.`concept` = ? and `relations`.`relation` = ? and `conceptsTo`.`concept` = ?";
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, start);
			ps.setString(2, relation);
			ps.setString(3, end);
			rs = ps.executeQuery();
			
			if(rs.next()){
				return true;
			}
			
		} catch (SQLException e) {
		   e.printStackTrace();
		}finally {
			try {
				if(ps != null)
					ps.close();
				if(connection != null)
					connection.close();
				if(rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;	
	}
	
	public static List<String> getExpectedResolution(String start) {
		String query = "call four_hops(?)";
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> concepts = null;
		
		try {
			concepts = new ArrayList<String>();
			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, start);
			rs = ps.executeQuery();
			
			while(rs.next()){
				concepts.add(rs.getString(5));
			}
			
		} catch (SQLException e) {
		   e.printStackTrace();
		}finally {
			try {
				if(ps != null)
					ps.close();
				if(connection != null)
					connection.close();
				if(rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return concepts;
	}
	
	public static List<Concept> getConceptFrom(String end, String relation) {

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
			
			while(rs.next()){
				concepts.add(new Concept(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
			}
			
		} catch (SQLException e) {
		   e.printStackTrace();
		}finally {
			try {
				if(ps != null)
					ps.close();
				if(connection != null)
					connection.close();
				if(rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return concepts;
	}
	
	public static List<Concept> getConceptTo(String start, String relation) {
		
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
			
			while(rs.next()){
				concepts.add(new Concept(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
			}
			
		} catch (SQLException e) {
		   e.printStackTrace();
		}finally {
			try {
				if(ps != null)
					ps.close();
				if(connection != null)
					connection.close();
				if(rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return concepts;
	}
	
public static Boolean getFourHops(String conflict, String possibleResolution) {
		
		String query = "SELECT c1.concept AS lev1, c2.concept as lev2, c3.concept as lev3 "
				+ "FROM concept_relations AS t1 "
				+ "LEFT JOIN concepts AS c1 ON c1.id = t1.toID "
				+ "LEFT JOIN concept_relations AS t2 ON t2.fromID = t1.toID "
				+ "LEFT JOIN concepts AS c2 ON c2.id = t2.toID "
				+ "LEFT JOIN concept_relations AS t3 ON t3.fromID = t2.toID "
				+ "LEFT JOIN concepts AS c3 ON c3.id = t3.toID "
				+ "WHERE t1.fromID = (SELECT id FROM CONCEPTS WHERE concept = ? LIMIT 1)";
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Concept> concepts = null;
		Boolean pathExists = false;
		
		try {
			concepts = new ArrayList<Concept>();
			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, conflict);
			rs = ps.executeQuery();
			
			while(rs.next() && !pathExists){
				String[] nodesInRow = {rs.getString(1),rs.getString(2),rs.getString(3)};
				//System.out.println("nodes:" + rs.getString(1) + rs.getString(2) + rs.getString(3));
				for (int i = 0; i<nodesInRow.length; i++){
					if(nodesInRow[i] != null && nodesInRow[i].equals(possibleResolution)){
						pathExists = true;
						break;
					}
				}
				
			}
			
		} catch (SQLException e) {
		   e.printStackTrace();
		}finally {
			try {
				if(ps != null)
					ps.close();
				if(connection != null)
					connection.close();
				if(rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return pathExists;
	}
}
