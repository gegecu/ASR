package model.story_representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Predicate;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.States;

public class AbstractStoryRepresentation {

	private String user;

	private Map<String, List<StorySentence>> storySentences;

	private Map<String, Noun> nouns;

	private StorySentence conflict;

	private StorySentence resolution;

	private String expectedResolutionConcept;

	private Checklist checklist;

	private String partOfStory;

	public AbstractStoryRepresentation() {
		this.user = "Geraldine";
		this.storySentences = new HashMap<String, List<StorySentence>>();
		this.nouns = new HashMap<String, Noun>();
		this.conflict = null;
		this.checklist = new Checklist(this);
		this.partOfStory = "start";
		this.expectedResolutionConcept = null;
	}

	public void setConflict() {

		StorySentence possibleConflict = this.getCurrentStorySentence();

		if (possibleConflict != null) {
			if (possibleConflict.getPolarity() <= -0.2) {
				if (this.setExpectedResolution(possibleConflict)) {
					// System.out.println("a");
					this.conflict = possibleConflict;
				}
			}
		}
	}

	public StorySentence getConflict() {
		return this.conflict;
	}

	public void setResolution() {
		// possibleResolution
		StorySentence possibleResolution = this.getCurrentStorySentence();

		if (((StorySentence) possibleResolution).getConcepts().contains(this.expectedResolutionConcept)) {
			List<Character> charsInResolution = new ArrayList<>();
			for (Predicate predicate : ((StorySentence) possibleResolution).getManyPredicates().values()) {
				for (Noun doer : predicate.getManyDoers().values()) {
					if (doer instanceof Character) {
						charsInResolution.add((Character) doer);
					}
				}

				for (Noun receiver : predicate.getReceivers().values()) {
					if (receiver instanceof Character) {
						charsInResolution.add((Character) receiver);
					}
				}
				for (Noun dobj : predicate.getDirectObjects().values()) {
					if (dobj instanceof Character) {
						charsInResolution.add((Character) dobj);
					}
				}
			}

			List<Character> charsInConflict = new ArrayList<>();

			for (Predicate predicate : ((StorySentence) possibleResolution).getManyPredicates().values()) {

				for (Noun doer : predicate.getManyDoers().values()) {
					if (doer instanceof Character) {
						charsInResolution.add((Character) doer);
					}
				}

				for (Noun receiver : predicate.getReceivers().values()) {
					if (receiver instanceof Character) {
						charsInConflict.add((Character) receiver);
					}
				}
				for (Noun dobj : predicate.getDirectObjects().values()) {
					if (dobj instanceof Character) {
						charsInConflict.add((Character) dobj);
					}
				}
			}

			charsInResolution.retainAll(charsInConflict);
			if (charsInResolution.size() > 0) {
				this.resolution = possibleResolution;
			}
		}
	}

	public StorySentence getResolution() {
		return this.resolution;
	}

	public void addEvent(StorySentence event) {

		List<StorySentence> events = this.storySentences.get(partOfStory);

		if (events == null) {
			events = new ArrayList<>();
		}

		// if (this.getCurrentStorySentence() != null && event.getLocation() ==
		// null)
		// event.setLocation(this.getCurrentStorySentence().getLocation());

		events.add(event);
		this.storySentences.put(partOfStory, events);

		System.out.println("add");

		if (this.partOfStory.equals("start")) {
			this.setConflict();
		} else if (this.partOfStory.equals("end")) {
			this.setResolution();
		}

	}

	public StorySentence getCurrentStorySentence() {
		List<StorySentence> events = this.storySentences.get(partOfStory);
		if (events == null) {
			return null;
		}
		return events.get(events.size() - 1);
	}

	public Map<String, List<StorySentence>> getManyStorySentences() {
		return this.storySentences;
	}

	public List<StorySentence> getManyStorySentencesBasedOnPart(String partOfStory) {
		// System.out.println(partOfStory);
		return this.storySentences.get(partOfStory);
	}

	public List<StorySentence> getManyStorySentencesBasedOnCurrentPart() {
		return this.storySentences.get(this.partOfStory);
	}

	public void addNoun(String key, Noun noun) {
		this.nouns.put(key, noun);
	}

	public Noun getNoun(String key) {
		if (this.nouns.containsKey(key)) {
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

	private boolean setExpectedResolution(StorySentence conflict) {
		// System.out.println("a");

		if (conflict.getConcepts().isEmpty()) {
			return false;
		}

		for (String concept : conflict.getConcepts()) {
			List<String> path = ConceptNetDAO.getExpectedResolution(concept);

			if (!path.isEmpty()) {
				expectedResolutionConcept = path.get(path.size() - 1);
				return true;
			}
		}
		return false;

	}

	public String getExpectedResolution() {
		return this.expectedResolutionConcept;
	}

	public void setUser(String name) {
		this.user = name;
	}

	public String getUser() {
		return this.user;
	}

	// public List<Noun> getAllNounsBasedOnRelation(String relation) {
	// StorySentence event = this.getCurrentStorySentence();
	// List<Noun> nouns = null;
	//
	// if (event != null) {
	// nouns = event.getAllNounsInEventBasedOnRelation(relation);
	// } else {
	// Set<Noun> temp = new HashSet();
	// for (Noun noun : this.nouns.values()) {
	// if (noun.getAttribute(relation) != null) {
	// temp.add(noun);
	// }
	// if (noun.getReference(relation) != null) {
	// temp.add(noun);
	// }
	// }
	// nouns = new ArrayList(temp);
	// }
	//
	// return nouns;
	// }
	//
	// public List<Noun> getAllNounsInCurrentEvent() {
	// StorySentence event = getCurrentEvent();
	// List<Noun> nouns = null;
	// if (event != null) {
	// nouns = event.getAllNounsInEvent();
	// } else {
	// nouns = new ArrayList(getManyNouns().values());
	// }
	//
	// return nouns;
	// }

}