package model.knowledge_base.senticnet;

/**
 * Builds two lists of concepts: verb type concepts and adjective type concepts
 */
public class ConceptParser {

	/**
	 * @param verb
	 *            the verb to use
	 * @return Returns the verb as a concept (Verb)
	 */
	public String createConceptAsVerb(String verb) {
		return (verb);
	}

	/**
	 * @param verb
	 *            the verb to use
	 * @param dobj
	 *            the direct object to use
	 * @return Returns the verb with direct object as a concept (Verb + " " +
	 *         Dobj)
	 */
	public String createConceptWithDirectObject(String verb, String dobj) {
		return (verb + " " + dobj);
	}

	/**
	 * @param verb
	 *            the verb to use
	 * @param dobj
	 *            the direct object to use
	 * @return Returns the verb with direct object as a negated concept ("not "
	 *         + Verb + " " + Dobj)
	 */
	public String createNegationVerbWithDirectObject(String verb, String dobj) {
		return ("not " + verb + " " + dobj);
	}

	/**
	 * @param verb
	 *            the verb to use
	 * @return Returns the verb as a negated concept ("not " + verb)
	 */
	public String createNegationVerb(String verb) {
		return ("not " + verb);
	}

	/**
	 * @param verb
	 *            the verb to use
	 * @param location
	 *            the location to use
	 * @return Returns the verb with location as a concept (Verb + " to " +
	 *         Dobj)
	 */
	public String createConceptAsInfinitive(String verb, String location) {
		return (verb + " to " + location);
	}

	/**
	 * @param verb
	 *            the verb to use
	 * @param prep
	 *            the prepositional phrase to use
	 * @param location
	 *            the location to use
	 * @return Returns the verb + preposition + location as a concept (Verb +
	 *         " " + Prep + " " + Location)
	 */
	public String createConceptAsPrepPhrase(String verb, String prep,
			String location) {
		return (verb + " " + prep + " " + location);
	}

	/**
	 * @param adj
	 *            the adjective to use
	 * @return Returns the be + adjective as a concept ("be " + Adj)
	 */
	public String createConceptAsPredicativeAdjective(String adj) {
		return ("be " + adj);
	}

	/**
	 * @param adj
	 *            the adjective to use
	 * @return Returns the adjective as a concept (Adj)
	 */
	public String createConceptAsAdjective(String adj) {
		return (adj);
	}

	/**
	 * @param adj
	 *            the adjective to use
	 * @return Returns the be + adjective as a negated concept ("not be " + Adj)
	 */
	public String createConceptAsPredicativeAdjectiveNegated(String adj) {
		return ("not be " + adj);
	}

	/**
	 * @param adj
	 *            the adjective to use
	 * @return Returns the adjective as a negated concept ("not " + Adj)
	 */
	public String createConceptAsAdjectiveNegated(String adj) {
		return ("not " + adj);
	}

	/**
	 * @param role
	 *            the role to use
	 * @return Returns the role as a concept (Role)
	 */
	public String createConceptAsRole(String role) {
		return (role);
	}

	/**
	 * @param role
	 *            the role to use
	 * @return Returns the role as a negated concept (Role)
	 */
	public String createConceptAsRoleNegation(String role) {
		return ("not " + role);
	}

	/**
	 * @param verb
	 *            the verb to use
	 * @param location
	 *            the location to use
	 * @return Returns the verb with location as a negated concept ("not " +
	 *         Verb + " to " + Dobj)
	 */
	public String createNegationVerbWithLocation(String verb, String location) {
		return ("not " + verb + " to " + location);
	}

}
