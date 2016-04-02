package model.knowledge_base.senticnet;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds two lists of concepts: verb type concepts and adjective type concepts
 */
public class ManualConceptParser {
//	private List<String> adjectiveConcepts;
//	private List<String> verbConcepts;

	public ManualConceptParser() {
//		adjectiveConcepts = new ArrayList<String>();
//		verbConcepts = new ArrayList<String>();
	}

//	public List<String> getAdjectiveConcepts() {
//		return adjectiveConcepts;
//	}
//	
//	public List<String> getVerbConcepts() {
//		return verbConcepts;
//	}

	public String createConceptAsVerb(String verb) {
		return(verb);
	}
	public String createConceptWithDirectObject(String verb, String dobj){
		//System.out.println(verb + " " + dobj);
		return(verb + " " + dobj);
	}
	
//	public void createConceptAsNoun(String noun) {
//		concepts.add(noun);
//	}

	/** creates a concept in the format 'verb + to + location' **/
	public String createConceptAsInfinitive(String verb, String location) {
		return(verb + " to " + location);
	}

//	/** creates all possible concepts from an adjective **/
//	public String createConceptsFromAdjective(String adj){
//		createConceptAsPredicativeAdjective(adj);
//		createConceptAsAdjective(adj);
//	}
	
	/** creates a concept in the format 'be + adjective' **/
	public String createConceptAsPredicativeAdjective(String adj) {
		return("be " + adj);
	}


	public String createConceptAsAdjective(String adj) {
		return(adj);
	}
}
