package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.story_representation.story_element.noun.Noun;

/**
 * Used to store extracted story sentence information
 */
public class StorySentence {

	/**
	 * stores the events extracted
	 */
	private Map<String, Event> events;
	/**
	 * stores the descriptions extracted
	 */
	private Map<String, Description> description;

	/**
	 * initialize the variables
	 */
	public StorySentence() {
		this.events = new HashMap<String, Event>();
		this.description = new HashMap<String, Description>();
	}

	/**
	 * Adds the attributeId, description to description
	 * 
	 * @param attributeId
	 *            the key to use
	 * @param description
	 *            the description to add
	 */
	public void addDescription(String attributeId, Description description) {
		this.description.put(attributeId, description);
	}

	/**
	 * @param attributeId
	 *            the key to use
	 * @return Description from description using attributeId as the key
	 */
	public Description getDescription(String attributeId) {
		return this.description.get(attributeId);
	}

	/**
	 * @return the description
	 */
	public Map<String, Description> getManyDescriptions() {
		return this.description;
	}

	/**
	 * Adds the id, event to events
	 * 
	 * @param id
	 *            the key to use
	 * @param event
	 *            the event to add
	 */
	public void addEvent(String id, Event event) {
		this.events.put(id, event);
	}

	/**
	 * @param id
	 *            the key to use
	 * @return Event from events using id as the key
	 */
	public Event getEvent(String id) {
		return this.events.get(id);
	}

	/**
	 * @return the events
	 */
	public Map<String, Event> getManyEvents() {
		return this.events;
	}

	/**
	 * @return returns true if events size > 0
	 */
	public boolean hasValidEvent() {
		if (this.events.size() >= 1) {
			return true;
		}
		return false;
	}

	/**
	 * @return the events size
	 */
	public int getEventsCount() {
		return this.events.size();
	}

	/**
	 * Returns list of position ids of nouns in the story sentence
	 * 
	 * @return list of position ids of nouns
	 */
	public List<String> getAllNouns() {

		Set<String> nounId = new HashSet<String>();
		for (Event event : this.getManyEvents().values()) {
			nounId.addAll(event.getManyDoers().keySet());
			nounId.addAll(event.getDirectObjects().keySet());
			nounId.addAll(event.getReceivers().keySet());
			nounId.addAll(event.getLocations().keySet());
		}

		for (Description d : this.description.values()) {
			nounId.addAll(d.getManyDoers().keySet());
			for (Map<String, Noun> temp : d.getReferences().values()) {
				nounId.addAll(temp.keySet());
			}
		}

		return new ArrayList<String>(nounId);

	}

}
