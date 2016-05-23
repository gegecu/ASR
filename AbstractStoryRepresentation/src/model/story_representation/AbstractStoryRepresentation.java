package model.story_representation;

import java.util.ArrayList;
import java.util.HashMap;
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

		//		String expectedResolutionConcept = null;
		//
		//		for (Clause clause : clauses) {
		//			if (this.conflict == null) {
		//				if (clause.getPolarity() <= -0.2
		//						&& (expectedResolutionConcept = ResolutionFinder
		//								.findExpectedResolutionConcept(
		//										clause)) != null) {
		//					this.conflict = new Conflict(clause,
		//							expectedResolutionConcept);
		//				}
		//			} else {
		//				if ((clause.getPolarity() < this.conflict.getPolarity())
		//						&& ((expectedResolutionConcept = ResolutionFinder
		//								.findExpectedResolutionConcept(
		//										clause)) != null)) {
		//					this.conflict = new Conflict(clause,
		//							expectedResolutionConcept);
		//				}
		//			}
		//		}

		for (Clause clause : clauses) {
			if (this.conflict == null) {
				if (clause.getPolarity() <= -0.2) {
					this.conflict = new Conflict(clause, null);
				}
			} else {
				if ((clause.getPolarity() < this.conflict.getPolarity())) {
					this.conflict = new Conflict(clause, null);
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
		doersInConflict
				.addAll(this.conflict.getClause().getManyDoers().values());

		for (Clause clause : clauses) {
			//if(clause.getConcepts().contains(this.conflict.getExpectedResolutionConcept())) {
			if (hasValidResolutionConcept(clause.getConcepts())) {
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

	/** trial for BFS approach **/
	private boolean hasValidResolutionConcept(List<String> concepts) {
		for (String concept : concepts) {
			for (String conflict : getConflict().getClause().getConcepts()) {
				if (checkValidResolution(conflict, concept)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkValidResolution(String conflict, String resolution) {
		return ConceptNetDAO.getFourHops(conflict, resolution);
	}

	public void addEvent(StorySentence storySentence) {

		List<StorySentence> storySentences = this.storySentences
				.get(partOfStory);

		if (storySentences == null) {
			storySentences = new ArrayList<StorySentence>();
		}

		storySentences.add(storySentence);
		this.storySentences.put(partOfStory, storySentences);

		switch (partOfStory) {
			case "start" :
				this.setConflict();
				break;
			case "middle" :
				//
				break;
			case "end" :
				if (this.conflict != null) {
					this.setResolution();
				}
				break;
		}

	}

	public StorySentence getCurrentStorySentence() {
		List<StorySentence> storySentences = this.storySentences
				.get(partOfStory);
		return storySentences != null
				? storySentences.get(storySentences.size() - 1)
				: null;
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

	public void reset() {
		storySentences.clear();
		nouns.clear();
	}

}
