package model.story_representation.story_element.story_sentence;

import java.util.HashMap;
import java.util.Map;

import model.story_representation.story_element.noun.Noun;

/**
 * Used to store event clause related information
 */
public class Event extends Clause {

	/**
	 * The receiver nouns extracted
	 */
	private Map<String, Noun> receivers;
	/**
	 * The direct object nouns extracted
	 */
	private Map<String, Noun> directObjects;
	/**
	 * The location nouns extracted
	 */
	private Map<String, Noun> locations;
	/**
	 * The verb extracted
	 */
	private Verb verb;

	/**
	 * initializes the variables
	 * 
	 * @param action
	 *            the action to set
	 */
	public Event(String action) {
		this.receivers = new HashMap<String, Noun>();
		this.directObjects = new HashMap<String, Noun>();
		this.locations = new HashMap<String, Noun>();
		this.verb = new Verb(action);
	}

	/**
	 * @return the verb
	 */
	public Verb getVerb() {
		return verb;
	}

	/**
	 * @param verb
	 *            the verb to set
	 */
	public void setVerb(Verb verb) {
		this.verb = verb;
	}

	/**
	 * @return the receivers
	 */
	public Map<String, Noun> getReceivers() {
		return this.receivers;
	}

	/**
	 * @param id
	 *            the key to use
	 * @return the noun object from receivers using id param as key
	 */
	public Noun getReceiver(String id) {
		return this.receivers.get(id);
	}

	/**
	 * @param id
	 *            the key to use
	 * @param noun
	 *            the noun object to add to receivers
	 */
	public void addReceiver(String id, Noun noun) {
		this.receivers.put(id, noun);
	}

	/**
	 * @return the directObjects
	 */
	public Map<String, Noun> getDirectObjects() {
		return this.directObjects;
	}

	/**
	 * @param id
	 *            the key to use
	 * @return the noun object from directObjects using id param as key
	 */
	public Noun getDirectObject(String id) {
		return this.directObjects.get(id);
	}

	/**
	 * @param id
	 *            the key to use
	 * @param noun
	 *            the noun object to add to directObjects
	 */
	public void addDirectObject(String id, Noun noun) {
		this.directObjects.put(id, noun);
	}

	/**
	 * @param id
	 *            the key to use
	 * @param noun
	 *            the noun object from locations using id param as key
	 */
	public void addLocation(String id, Noun noun) {
		this.locations.put(id, noun);
	}

	/**
	 * @return the locations
	 */
	public Map<String, Noun> getLocations() {
		return this.locations;
	}

	/**
	 * @param id
	 *            the key to use
	 * @return the noun object from locations using id param as key
	 */
	public Noun getLocation(String id) {
		return this.locations.get(id);
	}

}
