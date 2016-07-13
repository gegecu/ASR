package model.story_representation.story_element;

import model.story_representation.story_element.story_sentence.Clause;

public class SpecialClause {
	
	private Clause clause;
	private String mainConcept;
	private float polarity;
	
	public SpecialClause(Clause clause, String mainConcept, float polarity) {
		this.clause = clause;
		this.mainConcept = mainConcept;
		this.polarity = polarity;
	}

	public Clause getClause() {
		return clause;
	}

	public void setClause(Clause clause) {
		this.clause = clause;
	}

	public String getMainConcept() {
		return mainConcept;
	}

	public void setMainConcept(String mainConcept) {
		this.mainConcept = mainConcept;
	}
	
	public void setPolarity(float polarity) {
		this.polarity = polarity;
	}
	
	public float getPolarity() {
		return this.polarity;
	}
	
}
