package model.story_representation.story_element;

import java.util.List;
import java.util.Map;

import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;

public class Conflict {

	private Clause clause;
	private String expectedResolutionConcept;
	private Boolean isNegation;

	public Conflict(Clause clause, String expectedResolutionConcept) {
		this.clause = clause;
		this.expectedResolutionConcept = expectedResolutionConcept;
		this.isNegation = false;
		checkNegation();
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
	private void checkNegation(){
		if(clause instanceof Event){
			isNegation = ((Event) clause).getVerb().isNegated();
		}
		else if (clause instanceof Description){
			Map<String,List<String>> attr = ((Description) clause).getAttributes();
			Map<String,Map<String, Noun>> ref =((Description) clause).getReferences();
			
			if(attr.containsKey("notHasProperty") || ref.containsKey("notIsA")){
				isNegation = true;
			}
		}
	}
	//for negation resolution purposes
	public Boolean isNegation() {
		return this.isNegation;
	}

}
