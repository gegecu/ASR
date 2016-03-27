package model.story_representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.noun.Location;
import model.story_representation.noun.Noun;
import model.story_representation.noun.Character;
import model.utility.States;

public class AbstractStoryRepresentation {
	
	private Map<String, List<Event>> events;
	
	private Map<String, Noun> nouns;
	
	private StoryElement conflict;
	
	private StoryElement resolution;
	
	private String expectedResolutionConcept;
	
	private Checklist checklist;
	
	private String partOfStory;
	
	public AbstractStoryRepresentation() {
		this.events = new HashMap<String, List<Event>>();
		this.nouns = new HashMap<String, Noun>();
		this.conflict = null;
		this.checklist = new Checklist(this);
		this.partOfStory = "start";
		this.expectedResolutionConcept = null;
	}
	
	
	public void setConflict() {
		
		Event possibleConflict = this.getCurrentEvent();
		if(this.conflict == null) {
			
			//check event
			if(((Event)possibleConflict).getPolarity() <= -0.5) {
				this.setExpectedResolution(possibleConflict);
				if(this.expectedResolutionConcept != null) {
					this.conflict = possibleConflict;
				}
			}

			//if still null
			if(this.conflict == null) {
				for(Noun noun: this.nouns.values()) {
					if(noun instanceof Character) {
						if(((Character) noun).getFirstConflictState() != null) {
							this.conflict = noun;
						}
					}
				}	
			}
		}
		
		else {
			//check current event
			if(possibleConflict instanceof Event && this.conflict instanceof Event) {
				if(((Event)possibleConflict).getPolarity() < ((Event)this.conflict).getPolarity()) {
					this.setExpectedResolution(possibleConflict);
					if(this.expectedResolutionConcept != null) {
						this.conflict = possibleConflict;
					}
				}
			}
			else if(conflict instanceof Character && this.conflict instanceof Character) {
				
			}
		}
	}
	
	public StoryElement getConflict() {
		return this.conflict;
	}
	
	public void setResolution() {
		//possibleResolution
		Event possibleResolution = this.getCurrentEvent();
		
		if(possibleResolution instanceof Event && this.conflict instanceof Event) {
			if(((Event)possibleResolution).getConcepts().contains(this.expectedResolutionConcept)) {
				List<Character> charsInResolution = new ArrayList();
				for(Noun doer: ((Event)possibleResolution).getManyDoers().values()) {
					if(doer instanceof Character) {
						charsInResolution.add((Character)doer);
					}
				}
				for(Predicate predicate: ((Event)possibleResolution).getManyPredicates().values()) {
					for(Noun receiver: predicate.getReceivers().values()) {
						if(receiver instanceof Character) {
							charsInResolution.add((Character)receiver);
						}
					}
					for(Noun dobj: predicate.getDirectObjects().values()) {
						if(dobj instanceof Character) {
							charsInResolution.add((Character)dobj);
						}
					}
				}
				
				List<Character> charsInConflict = new ArrayList();
				for(Noun doer: ((Event)possibleResolution).getManyDoers().values()) {
					if(doer instanceof Character) {
						charsInConflict.add((Character)doer);
					}
				}
				for(Predicate predicate: ((Event)possibleResolution).getManyPredicates().values()) {
					for(Noun receiver: predicate.getReceivers().values()) {
						if(receiver instanceof Character) {
							charsInConflict.add((Character)receiver);
						}
					}
					for(Noun dobj: predicate.getDirectObjects().values()) {
						if(dobj instanceof Character) {
							charsInConflict.add((Character)dobj);
						}
					}
				}
				
				//if there is at least 1 char in conflict that is mentioned in resolution
				charsInResolution.retainAll(charsInConflict);
				if(charsInResolution.size() > 0) {
					this.resolution = possibleResolution;
				}
				
			}
		}
		
		else if (this.conflict instanceof Character) {
			if(States.CONFLICT_RESOLUTION.get(((Character) conflict).getFirstConflictState()).equals(((Character) conflict).getCurrentState())) {
				this.resolution = conflict;
			}
		}

	}
	
	public StoryElement getResolution() {
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
			
		}
		
		if(this.partOfStory.equals("start"))
			this.setConflict();
		
		else if(this.partOfStory.equals("end")) {
			this.setResolution();
		}

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
		System.out.println(partOfStory);
		return this.events.get(partOfStory);
	}
	
	public List<Event> getManyEventsBasedOnCurrentPart() {
		return this.events.get(this.partOfStory);
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
	
	private void setExpectedResolution(Event conflict) {

		if(conflict instanceof Event) {
			while(this.expectedResolutionConcept == null) {
			
			//System.out.println(this.conflict.getConcepts());
			
			if(conflict.getConcepts().isEmpty()) {
				break;
			}
			
			for(String concept: conflict.getConcepts()) {
				List<String> path = ConceptNetDAO.getExpectedResolution(concept);
				
				if(!path.isEmpty()) {
					expectedResolutionConcept = path.get(path.size()-1);
					break;
				}
			}
			//if after getting all conflict concepts and database found nothing. just stop. think of another way
			break;
		
			}
		}
	}
	
	public String getExpectedResolution() {
		return this.expectedResolutionConcept;
	}
	
}