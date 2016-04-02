package model.story_representation.story_element.story_sentence;

import java.util.HashMap;
import java.util.Map;

import model.story_representation.story_element.noun.Noun;

public class Event extends Clause {
	private String action;
	private Map<String, Noun> receivers;
	private Map<String, Noun> directObjects;
	
	public Event(String action) {
		this.action = action;
		this.receivers = new HashMap<String, Noun>();
		this.directObjects = new HashMap<String, Noun>();
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
