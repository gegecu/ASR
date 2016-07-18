package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.story_representation.story_element.noun.Noun;

public class StorySentence {

	private Map<String, Event> predicates;
	private Map<String, Description> description;

	public StorySentence() {
		this.predicates = new HashMap<String, Event>();
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

	public void addEvent(String id, Event predicate) {
		this.predicates.put(id, predicate);
	}

	public Event getEvent(String id) {
		return this.predicates.get(id);
	}

	public Map<String, Event> getManyEvents() {
		return this.predicates;
	}

	public boolean isValidEvent() {
		if (this.predicates.size() >= 1) {
			return true;
		}
		return false;
	}

	public int getEventsCount() {
		return this.predicates.size();
	}

	//unsure
	public List<String> getAllNounsInStorySentence() {

		Set<String> nounId = new HashSet<String>();
		for (Event predicate : this.getManyEvents().values()) {
			nounId.addAll(predicate.getManyDoers().keySet());
			nounId.addAll(predicate.getDirectObjects().keySet());
			nounId.addAll(predicate.getReceivers().keySet());
			nounId.addAll(predicate.getLocations().keySet());
		}

		for (Description d : this.description.values()) {
			nounId.addAll(d.getManyDoers().keySet());
			for(Map<String, Noun> temp : d.getReferences().values()) {
				nounId.addAll(temp.keySet());
			}
//			for (List<Noun> referred : d.getReferences().values()) {
//				for (Noun n : referred) {
//					nounId.add(n.getId());
//				}
//			}
		}

		return new ArrayList<String>(nounId);

	}

}
