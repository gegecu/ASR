package model.story_representation.story_element;

import model.story_representation.story_element.story_sentence.Clause;

/**
 * Used to store clause with polarity
 */
public class SpecialClause {

	/**
	 * The clause of the sentence
	 */
	private Clause clause;
	/**
	 * Main concept of the clause
	 */
	private String mainConcept;
	/**
	 * Polarity of the main concept
	 */
	private float polarity;

	/**
	 * @param clause
	 *            the clause to set
	 * @param mainConcept
	 *            the mainConcept to set
	 * @param polarity
	 *            the polarity to set
	 */
	public SpecialClause(Clause clause, String mainConcept, float polarity) {
		this.clause = clause;
		this.mainConcept = mainConcept;
		this.polarity = polarity;
	}

	/**
	 * @return the clause
	 */
	public Clause getClause() {
		return clause;
	}

	/**
	 * @param clause
	 *            the clause to set
	 */
	public void setClause(Clause clause) {
		this.clause = clause;
	}

	/**
	 * @return the mainConcept
	 */
	public String getMainConcept() {
		return mainConcept;
	}

	/**
	 * @param mainConcept
	 *            the mainConcept to set
	 */
	public void setMainConcept(String mainConcept) {
		this.mainConcept = mainConcept;
	}

	/**
	 * @return the polarity
	 */
	public float getPolarity() {
		return polarity;
	}

	/**
	 * @param polarity
	 *            the polarity to set
	 */
	public void setPolarity(float polarity) {
		this.polarity = polarity;
	}

}
