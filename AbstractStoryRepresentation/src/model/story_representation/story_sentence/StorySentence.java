package model.story_representation.story_sentence;

import java.util.ArrayList;
import java.util.List;

import model.story_representation.noun.Location;

public abstract class StorySentence {

	private float polarity;
	private List<String> concepts;
	private Location location;
	
	public StorySentence() {
		this.polarity = 0;
		this.concepts = null;
	}
	
	public void setPolarity(float polarity) {
		this.polarity = polarity;
	}
	
	public float getPolarity() {
		return this.polarity;
	}
	
	public void setConcepts(List<String> concepts) {
		this.concepts = concepts;
	}
	
	public List<String> getConcepts() {
		return this.concepts;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return this.location;
	}
}
