package model.story_representation.story_element.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.story_representation.story_element.StoryElement;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;

public class Event extends StoryElement{
	
	private Map<String, Noun> doers;
	private Map<String, Predicate> predicates;
	private Location location;	
	private float polarity;
	private List<String> concepts;
	
	public Event() {
		this.doers = new HashMap<String, Noun>();
		this.predicates = new HashMap<String, Predicate>();
		this.location = null;
		this.polarity = 0;
		this.concepts = null;
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
	
	public void addPredicate(Predicate predicate) {
		this.predicates.put(predicate.getAction(), predicate);
	}
	
	public Predicate getPredicate(String action) {
		return this.predicates.get(action);
	}
	
	public Map<String, Predicate> getManyPredicates() {
		return this.predicates;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public void setPolarity(float polarity) {
		this.polarity = polarity;
	}
	
	public float getPolarity() {
		return this.polarity;
	}
	
	public boolean isValidEvent() {
		if(this.doers.size() >= 1 && this.predicates.size() >= 1) {
			return true;
		}
		return false;
//		return true;
	}
	
	public void setConcept(List<String> concepts) {
		this.concepts = concepts;
	}
	
	public List<String> getConcepts() {
		return this.concepts;
	}
	
	public List<Noun> getAllNounsInEvent() {
		Set<Noun> nouns = new HashSet();
		nouns.addAll(this.doers.values());
		for(Predicate predicate: this.getManyPredicates().values()) {
			nouns.addAll(predicate.getDirectObjects().values());
			nouns.addAll(predicate.getReceivers().values());
		}
		if(this.location != null) {
			nouns.add(location);
		}
		return new ArrayList(nouns);
	}
	
	public List<Noun> getAllNounsInEventBasedOnRelation(String relation) {
		Set<Noun> nouns = new HashSet();
		for(Noun noun: this.doers.values()) {
			if(noun.getAttribute(relation) != null) {
				nouns.add(noun);
			}
			if(noun.getReference(relation) != null) {
				nouns.add(noun);
			}
		}
		
		for(Predicate predicate: this.predicates.values()) {
			for(Noun noun: predicate.getReceivers().values()) {
				if(noun.getAttribute(relation) != null) {
					nouns.add(noun);
				}
				if(noun.getReference(relation) != null) {
					nouns.add(noun);
				}
			}
			for(Noun noun: predicate.getDirectObjects().values()) {
				if(noun.getAttribute(relation) != null) {
					nouns.add(noun);
				}
				if(noun.getReference(relation) != null) {
					nouns.add(noun);
				}
			}
		}
		
		if(this.location != null) {
			if(this.location.getAttribute(relation) != null)
				nouns.add(location);
			if(this.location.getReference(relation) != null)
				nouns.add(location);
		}
		
		return new ArrayList(nouns);
	}
}

