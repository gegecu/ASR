package model.story_representation.story_sentence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.story_representation.Predicate;
import model.story_representation.noun.Location;
import model.story_representation.noun.Noun;

public class Event extends StorySentence{
	
	private Map<String, Noun> doers;
	private Map<String, Predicate> predicates;
		
	
	public Event() {
		super();
		this.doers = new HashMap<String, Noun>();
		this.predicates = new HashMap<String, Predicate>();
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
	
	public boolean isValidEvent() {
		if(this.doers.size() >= 1 && this.predicates.size() >= 1) {
			return true;
		}
		return false;
//		return true;
	}
	
}
