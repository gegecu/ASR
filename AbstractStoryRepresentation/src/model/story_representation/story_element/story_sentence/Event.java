package model.story_representation.story_element.story_sentence;

import java.util.HashMap;
import java.util.Map;

import model.story_representation.story_element.noun.Noun;

public class Event extends Clause {

	private Map<String, Noun> receivers;
	private Map<String, Noun> directObjects;
	private Map<String, Noun> locations;
	private Verb verb;

	public Event(String action) {
		this.receivers = new HashMap<String, Noun>();
		this.directObjects = new HashMap<String, Noun>();
		this.locations = new HashMap<String, Noun>();
		this.verb = new Verb(action);
	}

	public Verb getVerb() {
		return verb;
	}

	public void setVerb(Verb verb) {
		this.verb = verb;
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
	public void addLocation (String id, Noun noun){
		this.locations.put(id, noun);
	}
	public Map<String,Noun> getLocations(){
		return this.locations;
	}
	public Noun getLocation(String id){
		return this.locations.get(id);
	}

}
