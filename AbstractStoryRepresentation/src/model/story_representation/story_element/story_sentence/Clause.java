package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.story_representation.story_element.noun.Noun;

public abstract class Clause {

	protected Set<String> concepts;
	protected Map<String, Noun> doers;
	protected boolean isNegated;

	public Clause() {
		this.concepts = new HashSet<String>();
		this.doers = new HashMap<String, Noun>();
		isNegated = false;
	}
	
	public boolean isNegated(){
		return this.isNegated;
	}
	
	public void setNegated(boolean isNegated) {
		this.isNegated = isNegated;
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

	public Set<String> getConcepts() {
		return this.concepts;
	}

	public void addConcept(String concept) {
		this.concepts.remove(concept);
		this.concepts.add(concept);
	}

}
