package model.knowledge_base.senticnet;

/**
 * Builds two lists of concepts: verb type concepts and adjective type concepts
 */
public class ConceptParser {

	public String createConceptAsVerb(String verb) {
		return(verb);
	}
	public String createConceptWithDirectObject(String verb, String dobj){
		return(verb + " " + dobj);
	}
	
	/** creates a concept in the format 'verb + to + location' **/
	public String createConceptAsInfinitive(String verb, String location) {
		return(verb + " to " + location);
	}
	
	/** creates a concept in the format 'be + adjective' **/
	public String createConceptAsPredicativeAdjective(String adj) {
		return("be " + adj);
	}

	public String createConceptAsAdjective(String adj) {
		return(adj);
	}
	
	public String createConceptAsRole(String role) {
		return(role);
	}
	
}
