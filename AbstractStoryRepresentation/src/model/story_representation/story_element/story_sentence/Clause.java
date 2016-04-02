package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.story_representation.story_element.noun.Noun;

public abstract class Clause {

	protected float polarity;
	protected List<String> concepts;
	protected Map<String, Noun> doers;
	
	public Clause() {
		this.polarity = 0;
		this.concepts = new ArrayList<String>();
		this.doers = new HashMap<String, Noun>();
	}
	
	public void setPolarity(float polarity) {
		this.polarity = polarity;
	}

	public float getPolarity() {
		return this.polarity;
	}
	
	public void addDoer(String nounId, Noun noun) {
		this.doers.put(nounId, noun);
	}
	
	public Map<String, Noun> getManyDoers() {
		return this.doers;
	}
	
	public Noun getDoer(String nounId) {
		return this.doers.get(nounId);
	}
	
	public List<String> getConcepts() {
		return this.concepts;
	}
	
	public void addConcept(String concept) {
		this.concepts.remove(concept);
		this.concepts.add(concept);
	}
}
