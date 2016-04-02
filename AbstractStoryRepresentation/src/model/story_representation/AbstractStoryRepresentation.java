package model.story_representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.story_representation.story_element.Conflict;
import model.story_representation.story_element.Resolution;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.ResolutionFinder;

public class AbstractStoryRepresentation {

	private Map<String, List<StorySentence>> storySentences;

	private Map<String, Noun> nouns;

	private Conflict conflict;

	private Resolution resolution;

	private String partOfStory;

	public AbstractStoryRepresentation() {
		this.storySentences = new HashMap<String, List<StorySentence>>();
		this.nouns = new HashMap<String, Noun>();
		this.conflict = null;
		this.partOfStory = "start";
	}

	public void setConflict() {
		StorySentence possibleConflict = this.getCurrentStorySentence();
		
		List<Clause> clauses = new ArrayList<Clause>();
		clauses.addAll(possibleConflict.getManyPredicates().values());
		clauses.addAll(possibleConflict.getManyDescriptions().values());
		
		String expectedResolutionConcept = null;
		
		for(Clause clause: clauses) {
			if(this.conflict == null) {
				if(clause.getPolarity() <= -0.2 && (expectedResolutionConcept = ResolutionFinder.findExpectedResolutionConcept(clause)) != null) {
					this.conflict = new Conflict(clause, expectedResolutionConcept);
				}
			}
			else {
				if((clause.getPolarity() < this.conflict.getPolarity()) && ((expectedResolutionConcept = ResolutionFinder.findExpectedResolutionConcept(clause)) != null)) {
					this.conflict = new Conflict(clause, expectedResolutionConcept);
				}
			}
		}
	}

	public Conflict getConflict() {
		return this.conflict;
	}

	public void setResolution() {
		StorySentence possibleResolution = this.getCurrentStorySentence();
		
		List<Clause> clauses = new ArrayList<Clause>();
		clauses.addAll(possibleResolution.getManyPredicates().values());
		clauses.addAll(possibleResolution.getManyDescriptions().values());
		
		List<Noun> doersInConflict = new ArrayList<Noun>();
		doersInConflict.addAll(this.conflict.getClause().getManyDoers().values());
		
		for(Clause clause: clauses) {
			if(clause.getConcepts().contains(this.conflict.getExpectedResolutionConcept())) {
				List<Noun> doersInResolution = new ArrayList<Noun>();
				doersInResolution.addAll(clause.getManyDoers().values());

				doersInResolution.retainAll(doersInConflict);
				if (doersInResolution.size() > 0) {
					this.resolution = new Resolution(clause);
					return;
				}
			}
		}
	}

	public Resolution getResolution() {
		return this.resolution;
	}

	public void addEvent(StorySentence storySentence) {

		List<StorySentence> storySentences = this.storySentences.get(partOfStory);

		if (storySentences == null) {
			storySentences = new ArrayList<StorySentence>();
		}

		storySentences.add(storySentence);
		this.storySentences.put(partOfStory, storySentences);


		if (this.partOfStory.equals("start")) {
			this.setConflict();
		} else if (this.partOfStory.equals("end")) {
			this.setResolution();
		}

	}

	public StorySentence getCurrentStorySentence() {
		List<StorySentence> storySentences = this.storySentences.get(partOfStory);
		if (storySentences == null) {
			return null;
		}
		return storySentences.get(storySentences.size() - 1);
	}

	public Map<String, List<StorySentence>> getManyStorySentences() {
		return this.storySentences;
	}

	public List<StorySentence> getManyStorySentencesBasedOnPart(String partOfStory) {
		return this.storySentences.get(partOfStory);
	}

	public List<StorySentence> getManyStorySentencesBasedOnCurrentPart() {
		return this.storySentences.get(this.partOfStory);
	}

	public void addNoun(String key, Noun noun) {
		this.nouns.put(key, noun);
	}

	public Noun getNoun(String key) {
		return this.nouns.get(key);
	}

	public Map<String, Noun> getManyNouns() {
		return this.nouns;
	}

	public String getPartOfStory() {
		return partOfStory;
	}

	public void setPartOfStory(String partOfStory) {
		this.partOfStory = partOfStory;
	}

	
}
