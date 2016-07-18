package model.knowledge_base.senticnet;

/**
 * Builds two lists of concepts: verb type concepts and adjective type concepts
 */
public class ConceptParser {

	public String createConceptAsVerb(String verb) {
		return (verb);
	}

	public String createConceptWithDirectObject(String verb, String dobj) {
		return (verb + " " + dobj);
	}

	public String createNegationVerbWithDirectObject(String verb, String dobj) {
		return ("not " + verb + " " + dobj);
	}

	public String createNegationVerb(String verb) {
		return ("not " + verb);
	}

	/** creates a concept in the format 'verb + to + location' **/
	public String createConceptAsInfinitive(String verb, String location) {
		return (verb + " to " + location);
	}
	public String createConceptAsPrepPhrase(String verb, String prep,
			String location) {
		return (verb + " " + prep + " " + location);
	}
	/** creates a concept in the format 'be + adjective' **/
	public String createConceptAsPredicativeAdjective(String adj) {
		return ("be " + adj);
	}

	public String createConceptAsAdjective(String adj) {
		return (adj);
	}

	/** creates a concept in the format 'be + adjective' **/
	public String createConceptAsPredicativeAdjectiveNegated(String adj) {
		return ("not be " + adj);
	}

	public String createConceptAsAdjectiveNegated(String adj) {
		return ("not " + adj);
	}

	public String createConceptAsRole(String role) {
		return (role);
	}

	public String createConceptAsRoleNegation(String role) {
		return ("not " + role);
	}

	public String createNegationVerbWithLocation(String verb, String key) {
		return ("not " + verb + " to " + key);
	}

}
