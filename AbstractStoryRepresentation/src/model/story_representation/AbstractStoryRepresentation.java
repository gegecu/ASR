package model.story_representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.story_representation.noun.Location;
import model.story_representation.noun.Noun;

public class AbstractStoryRepresentation {
	
	private Map<String, List<Event>> events;
	
	private Map<String, Noun> nouns;
	
	private Event conflict;
	
	private Event resolution;
	
	private Checklist checklist;
	
	private String partOfStory;
	
	public AbstractStoryRepresentation() {
		this.events = new HashMap<String, List<Event>>();
		this.nouns = new HashMap<String, Noun>();
		this.conflict = null;
		this.checklist = new Checklist(this);
		this.partOfStory = "start";
	}
	
	public void setConflict(Event conflict) {
		
		if(this.conflict == null) {
			this.conflict = conflict;
		}
		
		else {
			if(conflict.getPolarity() <= this.conflict.getPolarity()) {
				this.conflict = conflict;
			}
		}
	}
	
	public Event getConflict() {
		return this.conflict;
	}
	
	public void setResolution(Event resolution) {
		this.resolution = resolution;
	}
	
	public Event getResolution() {
		return this.resolution;
	}
	
	public void addEvent(Event event) {
		
		List<Event> events = this.events.get(partOfStory);
		
		if(events == null) {
			events = new ArrayList();
		}
		
		if(event.isValidEvent()) {
			
			if(this.getCurrentEvent() != null && event.getLocation() == null) 
				event.setLocation(this.getCurrentEvent().getLocation());
			
			events.add(event);
			this.events.put(partOfStory, events);

			if(event.getPolarity() < 0)
				this.setConflict(event);
		}
		
//		this.nouns.putAll(event.getManyDoers());
//		this.nouns.putAll(event.getManyReceivers());
//		this.nouns.putAll(event.getManyDirectObjects());
//		
//		Location location = event.getLocation();
//		if(location != null) {
//			this.nouns.put(location.getId(), location);
//		}
		
		
	}
	
//	public Event getEvent(int index) {
//		if(!this.events.isEmpty() && this.events.get(index) == null) {
//			return this.events.get(0);
//		}
//		else {
//			return this.events.get(index);
//		}
//	}
	
	public Event getCurrentEvent() {
		List<Event> events = this.events.get(partOfStory);
		if(events == null) {
			return null;
		}
		return events.get(events.size()-1);
	}
	
	public Map<String, List<Event>> getManyEvents() {
		return this.events;
	}
	
	public List<Event> getManyEventsBasedOnPart(String partOfStory) {
		return this.events.get(partOfStory);
	}
	
	public void addNoun(String key, Noun noun) {
		this.nouns.put(key, noun);
	}
	
	public Noun getNoun(String key) {
		if(this.nouns.containsKey(key)) {
			return this.nouns.get(key);
		}
		return null;
	}
	
	public Map<String, Noun> getManyNouns() {
		return this.nouns;
	}
	
	public Checklist getCheckList() {
		return this.checklist;
	}

	public String getPartOfStory() {
		return partOfStory;
	}

	public void setPartOfStory(String partOfStory) {
		this.partOfStory = partOfStory;
	}
	
	
	
}