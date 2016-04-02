package model.text_generation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.knowledge_base.conceptnet.Concept;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.Randomizer;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.phrasespec.VPPhraseSpec;

public class StorySegmentGenerator extends TextGeneration {

	private String[] atLocationStorySegment = { "There is <start> in <end>.", "<end> has <start>.",
			"<doer> saw <start> in <end>." };

	private String[] hasAStorySegment = { "<start> has <end>." };

	private String[] isAStorySegment = { "<start> is <end>." };

	private String[] hasPropertyStorySegment = { "<start> can be <end>." };

	private String[] causesNoun = { "<start> produces <end>." };

	private String[] causesVerb = { "<doer> <end>." };

	private String[] causesAdjective = { "<doer> became <end>." };

	private Set<Integer> history;

	public StorySegmentGenerator(AbstractStoryRepresentation asr) {
		super(asr);
		this.history = new HashSet<>();
	}

	@Override
	public String generateText() {

		Set<String> response = new HashSet<>();

		if (asr.getPartOfStory().equals("start")) {

			String atLocation = atLocation();
			if (atLocation != null) {
				response.add(atLocation);
			}

			String hasA = hasA();
			if (hasA != null) {
				response.add(hasA);
			}

			String isA = isA();
			if (isA != null) {
				response.add(isA);
			}

			String hasProperty = hasProperty();
			if (hasProperty != null) {
				response.add(hasProperty);
			}

		} else {

			String causes = causes();
			if (causes != null) {
				response.add(causes);
			}

		}

		if (!response.isEmpty()) {
			int random = Randomizer.random(1, response.size());
			return (String) response.toArray()[random - 1];
		} else {
			return null;
		}

	}

	private String atLocation() {

		StorySentence storySentence = asr.getCurrentStorySentence();

		if (storySentence != null) {

			Location location = Utilities.getLocation(storySentence, asr);

			if (location != null) {

				String[] words = location.getId().split(" ");
				String word = words[words.length - 1];
				List<Concept> concepts = ConceptNetDAO.getConceptFrom(word, "AtLocation");

				List<Noun> doers = Utilities.getDoers(storySentence);
				String characters = this.wordsConjunction(doers);

				if (concepts != null) {

					while (!concepts.isEmpty()) {

						int randomConcept = Randomizer.random(1, concepts.size());
						Concept concept = concepts.remove(randomConcept - 1);

						if (history.contains(concept.getId())) {
							continue;
						}

						int randomSentence = Randomizer.random(1, this.atLocationStorySegment.length);
						if (characters.isEmpty()) {
							randomSentence = Randomizer.random(1, this.atLocationStorySegment.length - 1);
						}

						String storySegment = this.atLocationStorySegment[randomSentence - 1];
						String start = concept.getStart();
						start = concept.getStartPOS().equals("proper noun")
								? (start.substring(0, 1).toUpperCase() + start.substring(1))
								: this.determinerFixer(start);
						String end = (location.getIsCommon() ? "the " : "") + location.getId();

						storySegment = storySegment.replace("<start>", start);
						storySegment = storySegment.replace("<doer>", characters);
						storySegment = storySegment.replace("<end>", end);
						storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);

						this.history.add(concept.getId());

						return storySegment;

					}
				}
			}
		}

		return null;

	}

	private String hasA() {

		List<String> listOfNouns = asr.getCurrentStorySentence().getAllNounsInStorySentence();

		while (!listOfNouns.isEmpty()) {

			int randomNoun = Randomizer.random(1, listOfNouns.size());
			Noun noun = asr.getNoun(listOfNouns.remove(randomNoun - 1));
			List<Concept> concepts = null;

			if (noun instanceof Character && !noun.getIsCommon()) {
				concepts = ConceptNetDAO.getConceptTo("person", "HasA");
			} else {
				concepts = ConceptNetDAO.getConceptTo(noun.getId(), "HasA");
			}

			if (concepts != null) {

				while (!concepts.isEmpty()) {

					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept - 1);

					List<String> hasAAttributes = noun.getAttribute("HasA");
					List<Noun> hasAReferences = noun.getReference("HasA");

					if (hasAAttributes != null && hasAAttributes.contains(concept.getEnd())) {
						continue;
					} else if (hasAReferences != null) {
						boolean contains = false;
						for (Noun n : hasAReferences) {
							if (n.getId().equals(concept.getEnd())) {
								contains = true;
								break;
							}
						}
						if (contains) {
							continue;
						}
					}

					if (history.contains(concept.getId())) {
						continue;
					}

					int randomSentence = Randomizer.random(1, this.hasAStorySegment.length);
					String storySegment = this.hasAStorySegment[randomSentence - 1];
					String start = noun.getIsCommon() ? this.determinerFixer(noun.getId()) : noun.getId();
					String end = concept.getEnd();
					end = concept.getEndPOS().equals("proper noun")
							? (end.substring(0, 1).toUpperCase() + end.substring(1)) : this.determinerFixer(end);

					storySegment = storySegment.replace("<start>", start);
					storySegment = storySegment.replace("<end>", end);
					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);

					this.history.add(concept.getId());

					return storySegment;

				}

			}
		}

		return null;

	}

	private String isA() {

		List<String> listOfNouns = asr.getCurrentStorySentence().getAllNounsInStorySentence();

		while (!listOfNouns.isEmpty()) {

			int randomNoun = Randomizer.random(1, listOfNouns.size());
			Noun noun = asr.getNoun(listOfNouns.remove(randomNoun - 1));
			List<Concept> concepts = null;

			if (noun instanceof Character && !noun.getIsCommon()) {
				concepts = ConceptNetDAO.getConceptTo("person", "IsA");
			} else {
				concepts = ConceptNetDAO.getConceptTo(noun.getId(), "IsA");
			}

			if (concepts != null) {

				while (!concepts.isEmpty()) {

					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept - 1);

					List<String> isAAttributes = noun.getAttribute("IsA");
					List<Noun> isAReferences = noun.getReference("IsA");

					if (isAAttributes != null && isAAttributes.contains(concept.getEnd())) {
						continue;
					} else if (isAReferences != null) {
						boolean contains = false;
						for (Noun n : isAReferences) {
							if (n.getId().equals(concept.getEnd())) {
								contains = true;
								break;
							}
						}
						if (contains) {
							continue;
						}
					}

					if (history.contains(concept.getId())) {
						continue;
					}

					int randomSentence = Randomizer.random(1, this.isAStorySegment.length);
					String storySegment = this.isAStorySegment[randomSentence - 1];
					String start = noun.getIsCommon() ? this.determinerFixer(noun.getId()) : noun.getId();
					String end = concept.getEnd();
					end = concept.getEndPOS().equals("proper noun")
							? (end.substring(0, 1).toUpperCase() + end.substring(1)) : this.determinerFixer(end);

					storySegment = storySegment.replace("<start>", start);
					storySegment = storySegment.replace("<end>", end);
					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);

					this.history.add(concept.getId());

					return storySegment;

				}
			}
		}

		return null;

	}

	private String hasProperty() {

		List<String> listOfNouns = asr.getCurrentStorySentence().getAllNounsInStorySentence();

		while (!listOfNouns.isEmpty()) {

			int randomNoun = Randomizer.random(1, listOfNouns.size());
			Noun noun = asr.getNoun(listOfNouns.remove(randomNoun - 1));
			List<Concept> concepts = null;

			if (noun instanceof Character && !noun.getIsCommon()) {
				concepts = ConceptNetDAO.getConceptTo("person", "HasProperty");
			} else {
				concepts = ConceptNetDAO.getConceptTo(noun.getId(), "HasProperty");
			}

			if (concepts != null) {

				while (!concepts.isEmpty()) {

					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept - 1);

					List<String> hasPropertyAttributes = noun.getAttribute("HasProperty");
					List<Noun> hasPropertyReferences = noun.getReference("HasProperty");

					if (hasPropertyAttributes != null && hasPropertyAttributes.contains(concept.getEnd())) {
						continue;
					} else if (hasPropertyReferences != null) {
						boolean contains = false;
						for (Noun n : hasPropertyReferences) {
							if (n.getId().equals(concept.getEnd())) {
								contains = true;
								break;
							}
						}
						if (contains) {
							continue;
						}
					}

					if (history.contains(concept.getId())) {
						continue;
					}

					int randomSentence = Randomizer.random(1, this.hasPropertyStorySegment.length);
					String storySegment = this.hasPropertyStorySegment[randomSentence - 1];
					String start = noun.getIsCommon() ? this.determinerFixer(noun.getId()) : noun.getId();
					String end = concept.getEnd();

					switch (concept.getEndPOS()) {
					case "proper noun":
						end = end.substring(0, 1).toUpperCase() + end.substring(1);
						break;
					case "adjective":
					case "adjective phrase":
						break;
					default:
						end = this.determinerFixer(end);
						break;
					}

					storySegment = storySegment.replace("<start>", start);
					storySegment = storySegment.replace("<end>", end);
					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);

					this.history.add(concept.getId());

					return storySegment;

				}

			}

		}

		return null;

	}

	private String causes() {

		StorySentence storySentence = asr.getCurrentStorySentence();
		Set<Concept> temp = new HashSet<>();

		if (storySentence != null) {

			List<Noun> doers = Utilities.getDoers(storySentence);
			String characters = this.wordsConjunction(doers);

			for (Description description : storySentence.getManyDescriptions().values()) {
				for (String concept : description.getConcepts()) {
					temp.addAll(ConceptNetDAO.getConceptTo(concept, "Causes"));
				}
			}

			if (temp.isEmpty()) {
				return null;
			}

			List<Concept> concepts = new ArrayList<>(temp);

			boolean found = false;

			while (!concepts.isEmpty() && !found) {

				int randomConcept = Randomizer.random(1, concepts.size());
				Concept concept = concepts.remove(randomConcept - 1);

				if (history.contains(concept.getId())) {
					continue;
				}

				int randomSentence = 0;
				String storySegment = "";
				String start = concept.getStart();
				String end = concept.getEnd();

				switch (concept.getEndPOS()) {
				case "noun":
				case "proper noun":
					randomSentence = Randomizer.random(1, this.causesNoun.length);
					storySegment = causesNoun[randomSentence - 1];
					break;
				case "verb":
				case "verb phrase":
					randomSentence = Randomizer.random(1, this.causesVerb.length);
					storySegment = causesVerb[randomSentence - 1];
					VPPhraseSpec verb = nlgFactory.createVerbPhrase(end);
					verb.setFeature(Feature.TENSE, Tense.PAST);
					end = realiser.realise(verb).toString();
					break;
				case "adjective":
				case "adjective phrase":
					randomSentence = Randomizer.random(1, this.causesAdjective.length);
					storySegment = causesAdjective[randomSentence - 1];
					break;
				}

				storySegment = storySegment.replace("<start>", start);
				storySegment = storySegment.replace("<doer>", characters);
				storySegment = storySegment.replace("<end>", concept.getEnd());
				storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);

				this.history.add(concept.getId());
				found = true;

				return storySegment;

			}

		}

		return null;

	}

	private String determinerFixer(String start) {
		switch (start.charAt(0)) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
			return "an " + start;
		default:
			return "a " + start;
		}
	}

	// if (endPOS.equals("noun") || endPOS.equals("proper noun")) {
	// randomSentence = Randomizer.random(1, this.causesNoun.length);
	// storySegment = causesNoun[randomSentence - 1];
	// storySegment = storySegment.replace("<start>", concept.getStart());
	// storySegment = storySegment.replace("<end>", concept.getEnd());
	// } else if (endPOS.equals("verb") || endPOS.equals("verb phrase")) {
	// randomSentence = Randomizer.random(1, this.causesVerb.length);
	// storySegment = causesVerb[randomSentence - 1];
	// storySegment = storySegment.replace("<doer>", characters);
	// VPPhraseSpec verb = nlgFactory.createVerbPhrase(concept.getEnd());
	// verb.setFeature(Feature.TENSE, Tense.PAST);
	// storySegment = storySegment.replace("<end>",
	// realiser.realise(verb).toString());
	// } else if (endPOS.equals("adjective") || endPOS.equals("adjective
	// phrase")) {
	// randomSentence = Randomizer.random(1, this.causesAdjective.length);
	// storySegment = causesAdjective[randomSentence - 1];
	// storySegment = storySegment.replace("<doer>", characters);
	// storySegment = storySegment.replace("<end>", concept.getEnd());
	// }

	private String wordsConjunction(List<Noun> nouns) {
		String result = null;
		if (nouns.size() > 0) {
			result = "";
			for (int i = 0, j = nouns.size() - 1; i < j; i++) {
				if (i > 0)
					result += ", ";
				result += nouns.get(i).getId();
			}
			if (!result.equals(""))
				result += " and ";
			result += nouns.get(nouns.size() - 1).getId();
		}
		return result;
	}

}
