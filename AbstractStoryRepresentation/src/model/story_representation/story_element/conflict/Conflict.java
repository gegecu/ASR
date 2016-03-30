package model.story_representation.story_element.conflict;

import java.util.ArrayList;
import java.util.List;

import model.story_representation.story_element.noun.Character;

public class Conflict {

	private String expectedResolution;
	private Character involved;
	private List<String> concepts;

	public Conflict() {
		concepts = new ArrayList<>();
	}

	public String getExpectedResolution() {
		return expectedResolution;
	}

	public void setExpectedResolution(String expectedResolution) {
		this.expectedResolution = expectedResolution;
	}

	public Character getInvolved() {
		return involved;
	}

	public void setInvolved(Character involved) {
		this.involved = involved;
	}

	public List<String> getConcepts() {
		return concepts;
	}

	public void addConcept(String concept) {
		concepts.add(concept);
	}

}
