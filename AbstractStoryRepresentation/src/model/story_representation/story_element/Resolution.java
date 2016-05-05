package model.story_representation.story_element;

import model.story_representation.story_element.story_sentence.Clause;

public class Resolution {

	private Clause clause;

	public Resolution(Clause clause) {
		this.clause = clause;
	}

	public Clause getClause() {
		return clause;
	}

	public void setClause(Clause clause) {
		this.clause = clause;
	}

}
