package model.story_representation.story_element;

import model.story_representation.story_element.story_sentence.Clause;

public class Conflict {

	private Clause clause;
	private String expectedResolutionConcept;

	public Conflict(Clause clause, String expectedResolutionConcept) {
		this.clause = clause;
		this.expectedResolutionConcept = expectedResolutionConcept;
	}

	public Clause getClause() {
		return clause;
	}

	public void setClause(Clause clause) {
		this.clause = clause;
	}

	public String getExpectedResolutionConcept() {
		return expectedResolutionConcept;
	}

	public void setExpectedResolutionConcept(String expectedResolutionConcept) {
		this.expectedResolutionConcept = expectedResolutionConcept;
	}

	public float getPolarity() {
		return this.clause.getPolarity();
	}

}
