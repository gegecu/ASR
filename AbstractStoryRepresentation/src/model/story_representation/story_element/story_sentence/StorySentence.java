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

	public void addDescription(String attributeId, Description description) {
		this.description.put(attributeId, description);
	}

	public Description getDescription(String attributeId) {
		return this.description.get(attributeId);
	}

	public Map<String, Description> getManyDescriptions() {
		return this.description;
	}

	public void addEvent(String id, Event event) {
		this.events.put(id, event);
	}

	public Event getEvent(String id) {
		return this.events.get(id);
	}

	public Map<String, Event> getManyEvents() {
		return this.events;
	}

	public boolean hasValidEvent() {
		if (this.events.size() >= 1) {
			return true;
		}
		return false;
	}

	public int getEventsCount() {
		return this.events.size();
	}

	/**
	 * 
	 * 
	 * @return list of surface texts of nouns
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
