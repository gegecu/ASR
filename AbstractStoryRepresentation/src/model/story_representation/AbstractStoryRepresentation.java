package model.story_representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.story_representation.noun.Location;
import model.story_representation.noun.Noun;
import model.story_representation.story_sentence.Event;
import model.story_representation.story_sentence.State;
import model.story_representation.story_sentence.StorySentence;

public class AbstractStoryRepresentation {
	
	private Map<String, List<StorySentence>> storySentences;
	
	private Map<String, Noun> nouns;
	
	private StorySentence conflict;
	
	private StorySentence resolution;
	
	private Checklist checklist;
	
	private String partOfStory;
	
	public AbstractStoryRepresentation() {
		this.storySentences = new HashMap<String, List<StorySentence>>();
		this.nouns = new HashMap<String, Noun>();
		this.conflict = null;
		this.checklist = new Checklist(this);
		this.partOfStory = "start";
	}
	
	public void setConflict(StorySentence conflict) {
		
		if(this.conflict == null) {
			this.conflict = conflict;
		}
		
		else {
			if(conflict.getPolarity() <= this.conflict.getPolarity()) {
				this.conflict = conflict;
			}
		}
	}
	
	public StorySentence getConflict() {
		return this.conflict;
	}
	
	public void setResolution(Event resolution) {
		this.resolution = resolution;
	}
	
	public StorySentence getResolution() {
		return this.resolution;
	}
	
	public void addStorySentence(StorySentence storySentence) {
		
		List<StorySentence> storySentences = this.storySentences.get(this.partOfStory);
		
		if(storySentences == null) {
			storySentences = new ArrayList();
		}
			
		if(this.getCurrentStorySentence() != null && storySentence.getLocation() == null) 
			storySentence.setLocation(this.getCurrentStorySentence().getLocation());
			
		storySentences.add(storySentence);
		this.storySentences.put(partOfStory, storySentences);

		if(storySentence.getPolarity() < 0)
			this.setConflict(storySentence);
		
		
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
	
	public StorySentence getCurrentStorySentence() {
		List<StorySentence> storySentences = this.storySentences.get(partOfStory);
		if(storySentences == null) {
			return null;
		}
		return storySentences.get(storySentences.size()-1);
	}
	
	public Event getLatestEvent() {
		List<StorySentence> storySentences = this.storySentences.get(partOfStory);
		if(storySentences == null) {
			return null;
		}
		else {
			for(int i = storySentences.size() - 1; i >= 0; i--) {
				if(storySentences.get(i) instanceof Event) {
					return (Event) storySentences.get(i);
				}
			}
		}
		return null;
	}
	
	public State getLatestState() {
		List<StorySentence> storySentences = this.storySentences.get(partOfStory);
		if(storySentences == null) {
			return null;
		}
		else {
			for(int i = storySentences.size() - 1; i >= 0; i--) {
				if(storySentences.get(i) instanceof State) {
					return (State) storySentences.get(i);
				}
			}
		}
		return null;
	}
	
	public Map<String, List<StorySentence>> getManyStorySentences() {
		return this.storySentences;
	}
	
	public List<StorySentence> getManyStorySentencesBasedOnPart(String partOfStory) {
		return this.storySentences.get(partOfStory);
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