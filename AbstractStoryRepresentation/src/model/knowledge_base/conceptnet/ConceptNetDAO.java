package model.knowledge_base.conceptnet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.knowledge_base.MySQLConnector;
import model.story_representation.Event;

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
			concepts = new ArrayList();
			connection = MySQLConnector.getInstance().getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, start);
			rs = ps.executeQuery();
			
			while(rs.next()){
				//concepts.add(rs.getString(5));
			}
			concepts.add("nightmare");
			
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
}
