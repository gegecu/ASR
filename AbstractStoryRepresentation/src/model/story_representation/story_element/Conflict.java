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

	public Conflict(Clause clause, String expectedResolutionConcept) {
		this.clause = clause;
		//System.out.println("conflict created.." + clause.getConcepts().get(0) + " neg:" + clause.isNegated());
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
//	private void checkNegation(){
//		//System.out.println("checking negation");
//		if(clause instanceof Event){
//			isNegation = ((Event) clause).getVerb().isNegated();
//			//System.out.println("neg: " + isNegation);
//		}
//		else if (clause instanceof Description){
//			Map<String,List<String>> attr = ((Description) clause).getAttributes();
//			Map<String,Map<String, Noun>> ref =((Description) clause).getReferences();
//			
//			if(attr.containsKey("NotHasProperty") || ref.containsKey("NotIsA")){
//				isNegation = true;
//			}
//		}
//	}
	//for negation resolution purposes
	public Boolean isNegation() {
		//System.out.println("isneg: " + isNegation);
		return clause.isNegated();
	}

}
