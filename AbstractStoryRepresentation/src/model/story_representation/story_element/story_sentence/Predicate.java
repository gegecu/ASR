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
	
	//private Map<String, List<String>> concepts;

	public Predicate(String action) {
		this.adverbs = new HashSet();
		this.doers = new HashMap<String, Noun>();
		this.action = action;
		this.receivers = new HashMap<String, Noun>();
		this.directObjects = new HashMap<String, Noun>();
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
