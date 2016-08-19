package model.story_representation.story_element.story_sentence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.story_representation.story_element.noun.Noun;

/**
 * Used to store extracted clause information
 */
public abstract class Clause {

	/**
	 * Stores the concepts extracted 
	 */
	protected Set<String> concepts;
	/**
	 * Stores the noun doers extracted
	 */
	protected Map<String, Noun> doers;
	/**
	 * Stores if the clause is negated
	 */
	protected boolean isNegated;

	public Clause() {
		this.concepts = new HashSet<String>();
		this.doers = new HashMap<String, Noun>();
		isNegated = false;
	}

	/**
	 * @return the isNegated
	 */
	public boolean isNegated() {
		return this.isNegated;
	}

	/**
	 * @param isNegated
	 *            the isNegated to set
	 */
	public void setNegated(boolean isNegated) {
		this.isNegated = isNegated;
	}

	/**
	 * @param nounId
	 *            the nounId to add
	 * @param noun
	 *            the noun to add
	 */
	public void addDoer(String nounId, Noun noun) {
		this.doers.put(nounId, noun);
	}

	/**
	 * @return the doers
	 */
	public Map<String, Noun> getManyDoers() {
		return this.doers;
	}

	/**
	 * @param nounId
	 *            key to use on doers map
	 * @return noun from the doers map using nounId as key
	 */
	public Noun getDoer(String nounId) {
		return this.doers.get(nounId);
	}

	/**
	 * @return the concepts
	 */
	public Set<String> getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concept
	 *            the concept to add
	 */
	public void addConcept(String concept) {
		this.concepts.remove(concept);
		this.concepts.add(concept);
	}

}
