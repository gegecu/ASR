package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;

public class StorySentence {

	private Map<Integer, Predicate> predicates;
	private Map<String, Description> description;
//	private Map<String, List<Location>> locations;
	private float polarity;
	private List<String> adjectiveConcepts;
	private List<String> verbConcepts;

	public StorySentence() {
		this.predicates = new HashMap<Integer, Predicate>();
		//this.locations = new HashMap<String, List<Location>>();
		this.polarity = 0;
		this.adjectiveConcepts = null;
		this.verbConcepts = null;
		this.description = new HashMap<String, Description>();
	}

	public void addAttribute(String nounId, String key, String value) {
		
		Description description = this.description.get(nounId);
		if(description == null) {
			description = new Description();
		}
		description.addAttribute(key, value);
		this.description.put(nounId, description);
	}

	public void addReferences(String nounId, String key, Noun value) {
		Description description = this.description.get(nounId);
		if(description == null) {
			description = new Description();
		}
		description.addReference(key, value);
		this.description.put(nounId, description);
	}

	public Description getDescription(String nounId) {
		return this.description.get(nounId);
	}
	
	public Map<String, Description> getManyDescriptions() {
		return this.description;
	}

	public void addPredicate(Integer id, Predicate predicate) {
		this.predicates.put(id, predicate);
	}

	public Predicate getPredicate(Integer id) {
		return this.predicates.get(id);
	}

	public Map<Integer, Predicate> getManyPredicates() {
		return this.predicates;
	}

//	public void addLocation(String nounId, Location location) {
//		List<Location> locations = this.locations.get(nounId);
//		if(locations == null) {
//			locations = new ArrayList();
//		}
//		locations.add(location);
//		this.locations.put(nounId, locations);
//	}
//
//	public List<Location> getLocations(String nounId) {
//		return this.locations.get(nounId);
//	}
//	
//	public Map<String, List<Location>> getManyLocations() {
//		return this.locations;
//	}

	public void setPolarity(float polarity) {
		this.polarity = polarity;
	}

	public float getPolarity() {
		return this.polarity;
	}

	public boolean isValidEvent() {
		if (this.predicates.size() >= 1) {
			return true;
		}
		return false;
		// return true;
	}

	public void setAdjectiveConcepts(List<String> concepts) {
		this.adjectiveConcepts = concepts;
	}

	public void setVerbConcepts(List<String> concepts) {
		this.verbConcepts = concepts;
	}

	public List<String> getConcepts(){
		List<String> concepts = new ArrayList<String>();
		concepts.addAll(adjectiveConcepts);
		concepts.addAll(verbConcepts);
		if(concepts.isEmpty())
			return null;
		return concepts;
	}
	
	public List<String> getAllNounsInStorySentence() {
		Set<String> nounId = new HashSet();
		for (Predicate predicate : this.getManyPredicates().values()) {
			nounId.addAll(predicate.getManyDoers().keySet());
			nounId.addAll(predicate.getDirectObjects().keySet());
			nounId.addAll(predicate.getReceivers().keySet());
		}
		
		nounId.addAll(this.description.keySet());
		for(Description d: this.description.values()) {
			for(List<Noun> referred: d.getReferences().values()) {
				for(Noun n: referred) {
					nounId.add(n.getId());
				}
			}
		}
		
		return new ArrayList(nounId);
	}
//
//	public List<Noun> getAllNounsInEventBasedOnRelation(String relation) {
//		Set<Noun> nouns = new HashSet();
//		for (Noun noun : this.doers.values()) {
//			if (noun.getAttribute(relation) != null) {
//				nouns.add(noun);
//			}
//			if (noun.getReference(relation) != null) {
//				nouns.add(noun);
//			}
//		}
//
//		for (Predicate predicate : this.predicates.values()) {
//			for (Noun noun : predicate.getReceivers().values()) {
//				if (noun.getAttribute(relation) != null) {
//					nouns.add(noun);
//				}
//				if (noun.getReference(relation) != null) {
//					nouns.add(noun);
//				}
//			}
//			for (Noun noun : predicate.getDirectObjects().values()) {
//				if (noun.getAttribute(relation) != null) {
//					nouns.add(noun);
//				}
//				if (noun.getReference(relation) != null) {
//					nouns.add(noun);
//				}
//			}
//		}
//		
//		for(Location location: this.locations) {
//			if (location.getAttribute(relation) != null)
//				nouns.add(location);
//			if (location.getReference(relation) != null)
//				nouns.add(location);
//		}
//		
//		return new ArrayList(nouns);
//	}
}
