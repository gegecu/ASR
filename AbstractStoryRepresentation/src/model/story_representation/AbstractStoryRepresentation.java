package model.story_representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.story_element.Conflict;
import model.story_representation.story_element.Resolution;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.StorySentence;

public class AbstractStoryRepresentation {

	private Map<String, List<StorySentence>> storySentences;

	private Map<String, Noun> nouns;

	private Conflict conflict;

	private Resolution resolution;

	private String partOfStory;

	public AbstractStoryRepresentation() {
		this.storySentences = new LinkedHashMap<String, List<StorySentence>>();
		this.nouns = new HashMap<String, Noun>();
		this.conflict = null;
		this.partOfStory = "start";
	}

	public Conflict getConflict() {
		return this.conflict;
	}

	public Resolution getResolution() {
		return this.resolution;
	}

	public void addEvent(StorySentence storySentence) {

		List<StorySentence> storySentences = this.storySentences
				.get(partOfStory);

		if (storySentences == null) {
			storySentences = new ArrayList<StorySentence>();
			this.storySentences.put(partOfStory, storySentences);
		}

		storySentences.add(storySentence);

		switch (partOfStory) {
			case "start" :
				//
				break;
			case "middle" :
				//
				break;
			case "end" :
				break;
		}

	}

	public StorySentence getCurrentStorySentence() {
		List<StorySentence> storySentences = this.storySentences.get(partOfStory);
		
		if(storySentences == null) {
			if(partOfStory.equals("start")) {
				return null;
			}
			else if(partOfStory.equals("middle")) {
				storySentences = this.storySentences.get("start");
			}
			else if(partOfStory.equals("end")) {
				storySentences = this.storySentences.get("middle");
			}
		}

		return storySentences.get(storySentences.size()-1);
//		return storySentences != null? storySentences.get(storySentences.size() - 1): null;
	}

	public Map<String, List<StorySentence>> getStorySentencesMap() {
		return this.storySentences;
	}

	public List<StorySentence> getStorySentencesBasedOnPart(
			String partOfStory) {
		return this.storySentences.get(partOfStory);
	}

	public List<StorySentence> getStorySentencesBasedOnCurrentPart() {
		return this.storySentences.get(this.partOfStory);
	}

	public void addNoun(String key, Noun noun) {
		this.nouns.put(key, noun);
	}

	public Noun getNoun(String key) {
		return this.nouns.get(key);
	}

	public Map<String, Noun> getNounMap() {
		return this.nouns;
	}

	public String getCurrentPartOfStory() {
		return partOfStory;
	}

	public void setPartOfStory(String partOfStory) {
		this.partOfStory = partOfStory;
	}

	public void setConflict(Conflict conflict) {
		this.conflict = conflict;
	}

	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

}
