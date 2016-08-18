package model.text_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import model.knowledge_base.conceptnet.Concept;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.template.TemplateDAO;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.Randomizer;
import model.utility.SurfaceRealizer;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.phrasespec.VPPhraseSpec;

public class StorySegmentGenerator extends TextGeneration {

	private String[] atLocationStorySegmentStart = TemplateDAO.getTemplates("atLocationStorySegmentStart");

	private String[] atLocationStorySegmentDirectObject = TemplateDAO.getTemplates("atLocationStorySegmentDirectObject");

	private String[] hasAStorySegment = TemplateDAO.getTemplates("hasAStorySegment");

	private String[] isAStorySegment = TemplateDAO.getTemplates("isAStorySegment");

	private String[] hasPropertyStorySegment = TemplateDAO.getTemplates("hasPropertyStorySegment");

	private String[] causesNoun = TemplateDAO.getTemplates("causesNoun");

	private String[] causesVerb = TemplateDAO.getTemplates("causesVerb");

	private String[] causesAdjective = TemplateDAO.getTemplates("causesAdjective");

	private Queue<Integer> history;

	private Set<String> used;

	private int historySizeThreshold = 2;

	public StorySegmentGenerator(AbstractStoryRepresentation asr) {
		super(asr);
		this.history = new LinkedList<Integer>();
		this.used = new HashSet<>();
	}

	public void addUsed(String storySegment) {
		this.used.add(storySegment);
	}

	@Override
	public String generateText() {
		// TODO Auto-generated method stub

		Map<Integer, String> response = new HashMap<>();

		if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.start)) {

			Map<Integer, String> atLocation = atLocation();
			if (atLocation != null) {
				response.putAll(atLocation);
			}

			Map<Integer, String> atLocationDobj = atLocationDobj();
			if (atLocationDobj != null) {
				response.putAll(atLocationDobj);
			}

			Map<Integer, String> hasA = hasA();
			if (hasA != null) {
				response.putAll(hasA);
			}

			Map<Integer, String> isA = isA();
			if (isA != null) {
				response.putAll(isA);
			}

			Map<Integer, String> hasProperty = hasProperty();
			if (hasProperty != null) {
				response.putAll(hasProperty);
			}

		} else {

			Map<Integer, String> causes = causes();
			if (causes != null) {
				response.putAll(causes);
			}

		}

		if (!response.isEmpty()) {
			List<Integer> keys = new ArrayList<>(response.keySet());
			int random = Randomizer.random(1, keys.size());
			history.add(keys.get(random - 1));

			if (history.size() >= historySizeThreshold) {
				history.remove();
			}

			return response.get(keys.get(random - 1));
		} else {
			return null;
		}

	}

	//	private Map<String, Description> getNeededDescription(String relation,
	//			Map<String, Description> descriptions) {
	//		Map<String, Description> temp = new HashMap<>(descriptions);
	//		for (Map.Entry<String, Description> description : descriptions
	//				.entrySet()) {
	//			Description d = description.getValue();
	//			if (d.getAttribute(relation) == null
	//					&& d.getReference(relation) == null) {
	//				temp.remove(description.getKey());
	//			}
	//		}
	//		return temp;
	//	}

	private Map<Integer, String> atLocationDobj() {

		Map<Integer, String> output = new HashMap<>();

		StorySentence storySentence = asr.getCurrentStorySentence();

		List<String> eventIds = new ArrayList<>(
				storySentence.getManyEvents().keySet());

		boolean found = false;

		while (storySentence != null && !eventIds.isEmpty() && !found) {

			int randomEvent = Randomizer.random(1, eventIds.size());
			Event event = storySentence
					.getEvent(eventIds.remove(randomEvent - 1));

			List<String> directObjects = new ArrayList<String>(
					event.getDirectObjects().keySet());

			while (!directObjects.isEmpty() && !found) {
				int randomObject = Randomizer.random(1, directObjects.size());
				Noun dobj = event.getDirectObject(
						directObjects.remove(randomObject - 1));

				List<Concept> concepts = ConceptNetDAO
						.getConceptsTo(dobj.getId(), "AtLocation");

				List<Noun> doers = new ArrayList<Noun>(
						event.getManyDoers().values());
				String characters = SurfaceRealizer.nounFixer(doers);

				if (concepts != null) {

					while (!concepts.isEmpty()) {

						int randomConcept = Randomizer.random(1,
								concepts.size());
						Concept concept = concepts.remove(randomConcept - 1);

						if (history.contains(concept.getId())) {
							continue;
						}

						int randomSentence = Randomizer.random(1,
								this.atLocationStorySegmentDirectObject.length);

						// System.out.println(characters);
						String storySegment = this.atLocationStorySegmentDirectObject[randomSentence
								- 1];
						String end = concept.getEnd();
						end = concept.getStartPOS().equals("proper noun")
								? SurfaceRealizer.capitalizeFirstLetter(end)
								: SurfaceRealizer.determinerFixer(end);

						storySegment = storySegment.replace("<doer>",
								characters);
						
						storySegment = storySegment.replace("<end>", end);
						storySegment = SurfaceRealizer.capitalizeFirstLetter(storySegment);

						// this.history.add(concept.getId());

						if (this.used.contains(storySegment)) {
							continue;
						}

						output.put(concept.getId(), storySegment);
						found = true;
						return output;

					}
				}
			}
		}
		return null;
	}

	/**
	 * The room has a chair. possible storySegment There is a chair in room.
	 ***/

	private Map<Integer, String> atLocation() {

		Map<Integer, String> output = new HashMap<>();

		StorySentence storySentence = asr.getCurrentStorySentence();

		if (storySentence != null) {

			List<Location> locations = Utilities.getLocation(storySentence, asr);

			while(!locations.isEmpty()) {
				int randomLocation = Randomizer.random(1, locations.size());
				Location location = locations.remove(randomLocation-1);
				
				String[] words = location.getId().split(" ");
				String word = words[words.length - 1];
				List<Concept> concepts = ConceptNetDAO.getConceptsFrom(word,
						"AtLocation");

//				List<Noun> doers = Utilities.getDoers(storySentence);
//				String characters = SurfaceRealizer.wordsConjunction(doers);

				if (concepts != null) {

					while (!concepts.isEmpty()) {

						int randomConcept = Randomizer.random(1,
								concepts.size());
						Concept concept = concepts.remove(randomConcept - 1);

						if (history.contains(concept.getId())) {
							continue;
						}

						int randomSentence = Randomizer.random(1,
								this.atLocationStorySegmentStart.length);

						String storySegment = this.atLocationStorySegmentStart[randomSentence
								- 1];
						
						String start = concept.getStart();
						start = concept.getStartPOS().equals("proper noun")
								? SurfaceRealizer.capitalizeFirstLetter(start)
								: SurfaceRealizer.determinerFixer(start);
						storySegment = storySegment.replace("<start>", start);
						
						List<Noun> temp = new ArrayList();
						temp.add(location);
						String end = SurfaceRealizer.nounFixer(temp);
						storySegment = storySegment.replace("<end>", end);
						storySegment = SurfaceRealizer.capitalizeFirstLetter(storySegment);

						// this.history.add(concept.getId());
						if (this.used.contains(storySegment)) {
							continue;
						}

						output.put(concept.getId(), storySegment);
						return output;

					}
				}
			}
		}

		return null;

	}

	private Map<Integer, String> hasA() {

		Map<Integer, String> output = new HashMap<>();

		List<String> listOfNouns = getNouns();

		while (!listOfNouns.isEmpty()) {

			int randomNoun = Randomizer.random(1, listOfNouns.size());
			Noun noun = asr.getNoun(listOfNouns.remove(randomNoun - 1));
			List<Concept> concepts = null;

			if (noun.getType() == TypeOfNoun.CHARACTER && !noun.getIsCommon()) {
				concepts = ConceptNetDAO.getConceptsTo("person", "HasA");
			} else {
				concepts = ConceptNetDAO.getConceptsTo(noun.getId(), "HasA");
			}

			if (concepts != null) {

				while (!concepts.isEmpty()) {

					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept - 1);

					//discard concepts already stated
					List<String> hasAAttributes = noun.getAttribute("HasA");
					Map<String, Noun> hasAReferences = noun
							.getReference("HasA");
					if (hasAAttributes != null
							&& hasAAttributes.contains(concept.getEnd())) {
						continue;
					} else if (hasAReferences != null) {
						boolean contains = false;
						for (Noun n : hasAReferences.values()) {
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

					int randomSentence = Randomizer.random(1,
							this.hasAStorySegment.length);
					String storySegment = this.hasAStorySegment[randomSentence
							- 1];

					String end = concept.getEnd();
					end = concept.getEndPOS().equals("proper noun")
							? SurfaceRealizer.capitalizeFirstLetter(end)
							: SurfaceRealizer.determinerFixer(end);

					List<Noun> temp = new ArrayList<>();
					temp.add(noun);
					storySegment = storySegment.replace("<start>", SurfaceRealizer.nounFixer(temp));
					storySegment = storySegment.replace("<end>", end);
					storySegment = SurfaceRealizer.capitalizeFirstLetter(storySegment);

					// this.history.add(concept.getId());
					if (this.used.contains(storySegment)) {
						continue;
					}

					output.put(concept.getId(), storySegment);
					return output;

				}

			}
		}

		return null;

	}

	private Map<Integer, String> isA() {

		Map<Integer, String> output = new HashMap<>();

		List<String> listOfNouns = getNouns();

		while (!listOfNouns.isEmpty()) {

			int randomNoun = Randomizer.random(1, listOfNouns.size());
			Noun noun = asr.getNoun(listOfNouns.remove(randomNoun - 1));
			List<Concept> concepts = null;

			if (noun.getType() == TypeOfNoun.CHARACTER && !noun.getIsCommon()) {
				concepts = ConceptNetDAO.getConceptsTo("person", "IsA");
			} else {
				concepts = ConceptNetDAO.getConceptsTo(noun.getId(), "IsA");
			}

			if (concepts != null) {

				while (!concepts.isEmpty()) {

					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept - 1);

					//discard already stated
					List<String> isAAttributes = noun.getAttribute("IsA");
					Map<String, Noun> isAReferences = noun.getReference("IsA");

					if (isAAttributes != null
							&& isAAttributes.contains(concept.getEnd())) {
						continue;
					} else if (isAReferences != null) {
						boolean contains = false;
						for (Noun n : isAReferences.values()) {
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

					int randomSentence = Randomizer.random(1,
							this.isAStorySegment.length);
					String storySegment = this.isAStorySegment[randomSentence
							- 1];

					String end = concept.getEnd();
					end = concept.getEndPOS().equals("proper noun")
							? SurfaceRealizer.capitalizeFirstLetter(end)
							: SurfaceRealizer.determinerFixer(end);
							
					List<Noun> temp = new ArrayList<>();
					temp.add(noun);

					storySegment = storySegment.replace("<start>", SurfaceRealizer.nounFixer(temp));
					storySegment = storySegment.replace("<end>", end);
					storySegment = SurfaceRealizer.capitalizeFirstLetter(storySegment);

					// this.history.add(concept.getId());
					if (this.used.contains(storySegment)) {
						continue;
					}

					output.put(concept.getId(), storySegment);
					return output;

				}
			}
		}

		return null;

	}

	private Map<Integer, String> hasProperty() {

		Map<Integer, String> output = new HashMap<>();

		List<String> listOfNouns = getNouns();

		while (!listOfNouns.isEmpty()) {

			int randomNoun = Randomizer.random(1, listOfNouns.size());
			Noun noun = asr.getNoun(listOfNouns.remove(randomNoun - 1));
			List<Concept> concepts = null;

			if (noun.getType() == TypeOfNoun.CHARACTER && !noun.getIsCommon()) {
				concepts = ConceptNetDAO.getConceptsTo("person", "HasProperty");
			} else {
				concepts = ConceptNetDAO.getConceptsTo(noun.getId(),
						"HasProperty");
			}

			if (concepts != null) {

				while (!concepts.isEmpty()) {

					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept - 1);

					List<String> hasPropertyAttributes = noun
							.getAttribute("HasProperty");
					Map<String, Noun> hasPropertyReferences = noun
							.getReference("HasProperty");

					if (hasPropertyAttributes != null && hasPropertyAttributes
							.contains(concept.getEnd())) {
						continue;
					} else if (hasPropertyReferences != null) {
						boolean contains = false;
						for (Noun n : hasPropertyReferences.values()) {
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

					int randomSentence = Randomizer.random(1,
							this.hasPropertyStorySegment.length);
					String storySegment = this.hasPropertyStorySegment[randomSentence
							- 1];


					String end = concept.getEnd();

					switch (concept.getEndPOS()) {
						case "proper noun" :
							end = SurfaceRealizer.capitalizeFirstLetter(end);
							break;
						case "adjective" :
						case "adjective phrase" :
							break;
						default :
							end = SurfaceRealizer.determinerFixer(end);
							break;
					}

					List<Noun> temp = new ArrayList<>();
					temp.add(noun);
					storySegment = storySegment.replace("<start>", SurfaceRealizer.nounFixer(temp));
					storySegment = storySegment.replace("<end>", end);
					storySegment = SurfaceRealizer.capitalizeFirstLetter(storySegment);

					// this.history.add(concept.getId());
					if (this.used.contains(storySegment)) {
						continue;
					}

					output.put(concept.getId(), storySegment);
					return output;

				}

			}

		}

		return null;

	}

	private Map<Integer, String> causes() {

		Map<Integer, String> output = new HashMap<>();

		StorySentence storySentence = asr.getCurrentStorySentence();
		List<Clause> clauses = new ArrayList<Clause>();
		clauses.addAll(storySentence.getManyDescriptions().values());
		clauses.addAll(storySentence.getManyEvents().values());

		Set<Concept> temp = new HashSet<>();
		boolean found = false;
		while (!clauses.isEmpty() && !found) {

			int randomClause = Randomizer.random(1, clauses.size());
			Clause clause = clauses.remove(randomClause - 1);

			List<Noun> doers = new ArrayList<>();
			for (Noun noun : clause.getManyDoers().values()) {
				doers.add(noun);
			}

			String characters = SurfaceRealizer.nounFixer(doers);

			for (String concept : clause.getConcepts()) {
				temp.addAll(ConceptNetDAO.getConceptsTo(concept, "Causes"));
			}

			if (temp.isEmpty()) {
				found = false;
			}

			List<Concept> concepts = new ArrayList<>(temp);

			while (!concepts.isEmpty() && !found) {

				int randomConcept = Randomizer.random(1, concepts.size());
				Concept concept = concepts.remove(randomConcept - 1);

				if (history.contains(concept.getId())) {
					continue;
				}

				String endPOS = concept.getEndPOS();

				int randomSentence = 0;
				String storySegment = "";
				if (endPOS.equals("proper noun")) {
					randomSentence = Randomizer.random(1,
							this.causesNoun.length);
					storySegment = causesNoun[randomSentence - 1];
					storySegment = storySegment.replace("<start>",
							concept.getStart());
					storySegment = storySegment.replace("<end>",
							concept.getEnd());
				} else if (endPOS.equals("noun")) {
					randomSentence = Randomizer.random(1,
							this.causesNoun.length);
					storySegment = causesNoun[randomSentence - 1];
					storySegment = storySegment.replace("<start>",
							SurfaceRealizer
									.determinerFixer(concept.getStart()));
					storySegment = storySegment.replace("<end>",
							concept.getEnd());
				} else if (endPOS.equals("verb")
						|| endPOS.equals("verb phrase")) {
					randomSentence = Randomizer.random(1,
							this.causesVerb.length);
					storySegment = causesVerb[randomSentence - 1];
					storySegment = storySegment.replace("<doer>", characters);
					VPPhraseSpec verb = nlgFactory
							.createVerbPhrase(concept.getEnd());
					verb.setFeature(Feature.TENSE, Tense.PAST);
					storySegment = storySegment.replace("<end>",
							realiser.realise(verb).toString());

					// unsure
					List<Concept> temp1 = ConceptNetDAO
							.getConceptsFrom(concept.getEnd(), "usedFor");
					if (!temp1.isEmpty()) {
						int rand = Randomizer.random(1, temp1.size());
						storySegment = storySegment.replace("<object>",
								"using " + temp1.get(rand - 1).getStart());
					} else {
						storySegment = storySegment.replace(" <object>", "");
					}

				} else if (endPOS.equals("adjective")
						|| endPOS.equals("adjective phrase")) {
					randomSentence = Randomizer.random(1,
							this.causesAdjective.length);
					storySegment = causesAdjective[randomSentence - 1];
					storySegment = storySegment.replace("<doer>", characters);
					storySegment = storySegment.replace("<end>",
							concept.getEnd());
				}

				if (storySegment.length() == 0) {
					continue;
				}

				storySegment = SurfaceRealizer.capitalizeFirstLetter(storySegment);

				// this.history.add(concept.getId());
				found = true;
				output.put(concept.getId(), storySegment);
				return output;

			}

		}

		return null;

	}

}