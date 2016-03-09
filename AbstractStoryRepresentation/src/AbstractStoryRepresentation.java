
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import noun.Location;
import noun.Noun;

public class AbstractStoryRepresentation {
	
	private List<Event> events;
	
	private Map<String, Noun> nouns;
	
	private Event conflict;
	
	private Event resolution;
	
	private Checklist checklist;
	
	public AbstractStoryRepresentation() {
		this.events = new ArrayList<Event>();
		this.nouns = new HashMap<String, Noun>();
		this.conflict = null;
		this.checklist = new Checklist(this);
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
		this.events.add(event);
		
		this.nouns.putAll(event.getManyDoers());
		this.nouns.putAll(event.getManyReceivers());
		this.nouns.putAll(event.getManyDirectObjects());
		
		Location location = event.getLocation();
		if(location != null) {
			this.nouns.put(location.getId(), location);
		}
		
		
	}
	
	public Event getEvent(int index) {
		if(!this.events.isEmpty() && this.events.get(index) == null) {
			return this.events.get(0);
		}
		else {
			return this.events.get(index);
		}
	}
	
	public Event getCurrentEvent() {
		return this.events.get(this.events.size()-1);
	}
	
	public List<Event> getManyEvents() {
		return this.events;
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
	
}