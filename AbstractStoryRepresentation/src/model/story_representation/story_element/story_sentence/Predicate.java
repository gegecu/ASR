package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.story_representation.story_element.noun.Noun;

public class Predicate { //clause
	
	private Map<String, Noun> doers;
	private String action;
	private Set<String> adverbs;
	private Map<String, Noun> receivers;
	private Map<String, Noun> directObjects;
	private List<String> adjectiveConcepts;
	private List<String> verbConcepts;

	public Predicate(String action) {
		this.adverbs = new HashSet();
		this.doers = new HashMap<String, Noun>();
		this.action = action;
		this.adjectiveConcepts = new ArrayList<String>();
		this.verbConcepts = new ArrayList<String>();;
		this.receivers = new HashMap<String, Noun>();
		this.directObjects = new HashMap<String, Noun>();
	}
	
	/** code for concepts **/
	public void addAdjectiveConcept(String concept){
		adjectiveConcepts.add(concept);
	}
	public void addVerbConcept(String concept){
		verbConcepts.add(concept);
	}
	public List<String> getAdjectiveConcepts(){
		return this.adjectiveConcepts;
	}
	public List<String> getVerbConcepts(){
		return this.verbConcepts;
	}
	public List<String> getConcepts(){
		List<String> concepts = new ArrayList<String>();
		concepts.addAll(adjectiveConcepts);
		concepts.addAll(verbConcepts);
		return concepts;
	}
	public void addAdverb(String adverb) {
		this.adverbs.add(adverb);
	}
	
	public List<String> getAdverbs() {
		return new ArrayList(this.adverbs);
	}
	
	public void addDoer(String id, Noun noun) {
		this.doers.put(id, noun);
	}

	public Noun getDoer(String id) {
		return this.doers.get(id);
	}

	public Map<String, Noun> getManyDoers() {
		return this.doers;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Map<String, Noun> getReceivers() {
		return this.receivers;
	}
	
	public Noun getReceiver(String id) {
		return this.receivers.get(id);
	}
	
	public void addReceiver(String id, Noun noun) {
		this.receivers.put(id, noun);
	}
	
	public Map<String, Noun> getDirectObjects() {
		return this.directObjects;
	}
	
	public Noun getDirectObject(String id) {
		return this.directObjects.get(id);
	}
	
	public void addDirectObject(String id, Noun noun) {
		this.directObjects.put(id, noun);
	}

}
