package model.text_generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.phrasespec.VPPhraseSpec;
import model.knowledge_base.conceptnet.Concept;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Predicate;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.Randomizer;

public class StorySegmentGenerator extends TextGeneration{
	
	private String[] atLocationStorySegmentStart = {"There is <start> in <end>."
												, "<end> has <start>."};
	
	private String[] atLocationStorySegmentMiddle = {"<doer> saw <start> in <end>."};
	
	private String[] hasAStorySegment = {"<start> has <end>."};
	
	private String[] isAStorySegment = {"<start> is <end>."};
	
	private String[] hasPropertyStorySegment = {"<start> can be <end>."};
	
	private String[] causesNoun = {"<start> produces <end>."};
	
	private String[] causesVerb = {"<doer> <end>."};
	
	private String[] causesAdjective = {"<doer> became <end>."};

	private Set<Integer> history; 
			
	public StorySegmentGenerator(AbstractStoryRepresentation asr) {
		super(asr);
		this.history = new HashSet();
	}

	@Override
	public String generateText() {
		// TODO Auto-generated method stub
		
		Set<String> response = new HashSet();
		
		if(asr.getPartOfStory().equals("start")) {
			String atLocation = atLocationStart();
			if(atLocation != null) {
				response.add(atLocation);
			}
			
			String hasA = hasA();
			if(hasA != null) {
				response.add(hasA);
			}
			
			String isA = isA();
			if(isA != null) {
				response.add(isA);
			}
			
			String hasProperty = hasProperty();
			if(hasProperty != null) {
				response.add(hasProperty);
			}
		}
		else {
			String causes = causes();
			if(causes != null) {
				response.add(causes);
			}
		}
		
		if(!response.isEmpty()) {
			int random = Randomizer.random(1, response.size());
			return (String)response.toArray()[random-1];
		}
		else {
			return null;
		}
	}
		
	private Map<String, Description> getNeededDescription(String relation, Map<String, Description> descriptions) {
		Map<String, Description> temp = new HashMap(descriptions);
		for(Map.Entry<String, Description> description: descriptions.entrySet()) {
			Description d = description.getValue();
			if(d.getAttribute(relation) == null && d.getReference(relation) == null) {
				temp.remove(description.getKey());
			}
		}
		return temp;
	}
		
	
	private String atLocationStart() {
		StorySentence storySentence = asr.getCurrentStorySentence();
		Map<String, Description> descriptions = getNeededDescription("AtLocation", storySentence.getManyDescriptions());
		List<String> keys = new ArrayList(descriptions.keySet());
		
		while(!keys.isEmpty()) {
			int randomKey = Randomizer.random(1, keys.size());
			List<Noun> locations = descriptions.get(keys.get(randomKey-1)).getReference("AtLocation");
			
			while(!locations.isEmpty()) {
				int randomLocation = Randomizer.random(1, locations.size());
				Location location = (Location) locations.get(randomLocation-1);
				
				String[] words = location.getId().split(" ");
				String word = words[words.length-1];
				List<Concept> concepts = ConceptNetDAO.getConceptFrom(word, "AtLocation");
				
				if(concepts == null || concepts.isEmpty()) {
					continue;
				}
				
				int randomConcept = Randomizer.random(1, concepts.size());
				Concept concept = concepts.remove(randomConcept-1);
				
				if(history.contains(concept.getId())) {
					continue;
				}

				int randomSentence = Randomizer.random(1, this.atLocationStorySegmentStart.length);
				
				String storySegment = this.atLocationStorySegmentStart[randomSentence-1];
				
				if(location.getIsCommon()) {
					storySegment = storySegment.replace("<end>", "the " + location.getId());
				}
				else {
					storySegment = storySegment.replace("<end>", location.getId());
				}
				
				String start = concept.getStart();
				String startPOS = concept.getStartPOS();
				
				if(startPOS.equals("proper noun")) {
					start = start.substring(0, 1).toUpperCase() + start.substring(1);
					storySegment = storySegment.replace("<start>", start);
				}
				else {
					storySegment = storySegment.replace("<start>", this.determinerFixer(start));
				}
				
				storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);
			
				this.history.add(concept.getId());
				
				return storySegment;
			}	
		}
		return null;
	}
	
	private String hasA() {
		StorySentence storySentence = asr.getCurrentStorySentence();
		List<String> nounId = storySentence.getAllNounsInStorySentence();
		
		boolean found = false;

		/* loop all nouns in current story sentence, 
		 * already checks if history got concept extracted from db, 
		 * already checks if noun already has that from text understand
		 * but no check consecutive same template*/
		
		while(!nounId.isEmpty() && !found) {
			int randomNoun = Randomizer.random(1, nounId.size());
			Noun noun = asr.getNoun(nounId.remove(randomNoun-1));
			
			List<Concept> concepts = null;
			
			//proper noun character ie. John, Gege
			if(noun instanceof Character && !noun.getIsCommon()) {
				concepts = ConceptNetDAO.getConceptTo("person", "HasA");
			}
			else {
				concepts = ConceptNetDAO.getConceptTo(noun.getId(), "HasA");
			}
			if(concepts == null || concepts.isEmpty()) {
				continue;
			}
			else {
				while(!concepts.isEmpty() && !found) {
					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept-1);
					
					if(noun.getAttribute("HasA") != null) {
						if(noun.getAttribute("HasA").contains(concept.getEnd())){
						continue;
						}
					}
					else {
						if(noun.getReference("HasA") != null) {
							for(Noun n: noun.getReference("HasA")) {
								if(n.getId().equals(concept.getEnd())) {
									continue;
								}
							}
						}
					}
					
					int randomSentence = Randomizer.random(1, this.hasAStorySegment.length);
					
					if(history.contains(concept.getId())) {
						continue;
					}
					
					String storySegment = this.hasAStorySegment[randomSentence-1];
					
					if(noun.getIsCommon())
						storySegment = storySegment.replace("<start>", this.determinerFixer(noun.getId()));
					else 
						storySegment = storySegment.replace("<start>", noun.getId());
					
					String end = concept.getEnd();
					String endPOS = concept.getEndPOS();
					
					if(endPOS.equals("proper noun")) {
						end = end.substring(0, 1).toUpperCase() + end.substring(1);
						storySegment = storySegment.replace("<end>", end);
					}
					else {
						storySegment = storySegment.replace("<end>", this.determinerFixer(end));
					}

					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);
					
					this.history.add(concept.getId());
					found = true;
					
					return storySegment;
				}
			}
			
		}
		
		return null;
	}
	
	/* loop all nouns in current story sentence, 
	 * already checks if history got concept extracted from db, 
	 * already checks if noun already has that from text understand
	 * but no check consecutive same template*/
	private String isA() {
		StorySentence storySentence = asr.getCurrentStorySentence();
		List<String> nounId = storySentence.getAllNounsInStorySentence();
		
		boolean found = false;

		while(!nounId.isEmpty() && !found) {
			int randomNoun = Randomizer.random(1, nounId.size());
			Noun noun = asr.getNoun(nounId.remove(randomNoun-1));
			List<Concept> concepts = null;
			
			//Gege, John (character and proper noun)
			if(noun instanceof Character && !noun.getIsCommon()) {
				concepts = ConceptNetDAO.getConceptTo("person", "IsA");
			}
			else {
				concepts = ConceptNetDAO.getConceptTo(noun.getId(), "IsA");
			}
			if(concepts == null || concepts.isEmpty()) {
				continue;
			}
			else {
				while(!concepts.isEmpty() && !found) {
					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept-1);
					
					if(noun.getAttribute("IsA") != null) {
						if(noun.getAttribute("IsA").contains(concept.getEnd())){
						continue;
						}
					}
					else {
						if(noun.getReference("IsA") != null) {
							for(Noun n: noun.getReference("IsA")) {
								if(n.getId().equals(concept.getEnd())) {
									continue;
								}
							}
						}
					}
					
					
					int randomSentence = Randomizer.random(1, this.isAStorySegment.length);
					
					if(history.contains(concept.getId())) {
						continue;
					}
					
					String storySegment = this.isAStorySegment[randomSentence-1];
					
					if(noun.getIsCommon())
						storySegment = storySegment.replace("<start>", this.determinerFixer(noun.getId()));
					else 
						storySegment = storySegment.replace("<start>", noun.getId());
					
					
					String end = concept.getEnd();
					String endPOS = concept.getEndPOS();
					
					if(endPOS.equals("proper noun")) {
						end = end.substring(0, 1).toUpperCase() + end.substring(1);
						storySegment = storySegment.replace("<end>", end);
					}
					else {
						storySegment = storySegment.replace("<end>", this.determinerFixer(end));
					}
					
					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);
					
					this.history.add(concept.getId());
					found = true;
					
					return storySegment;
				}
			}
			
		}
		
		return null;
	}
	
	/* loop all nouns in current story sentence, 
	 * already checks if history got concept extracted from db, 
	 * already checks if noun already has that from text understand
	 * but no check consecutive same template*/
	private String hasProperty() {
		
		StorySentence storySentence = asr.getCurrentStorySentence();
		List<String> nounId = storySentence.getAllNounsInStorySentence();

		boolean found = false;
	
		while(!nounId.isEmpty() && !found) {
			int randomNoun = Randomizer.random(1, nounId.size());
			Noun noun = asr.getNoun(nounId.remove(randomNoun-1));
			List<Concept> concepts = null;
			
			if(noun instanceof Character && !noun.getIsCommon()) {
				concepts = ConceptNetDAO.getConceptTo("person", "HasProperty");
			}
			else {
				concepts = ConceptNetDAO.getConceptTo(noun.getId(), "HasProperty");
			}
			if(concepts == null) {
				continue;
			}
			else {
				while(!concepts.isEmpty() && !found) {
					
					
					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept-1);
					
					if(noun.getAttribute("HasProperty") != null) {
						if(noun.getAttribute("HasProperty").contains(concept.getEnd())){
						continue;
						}
					}
					else {
						if(noun.getReference("HasProperty") != null) {
							for(Noun n: noun.getReference("HasProperty")) {
								if(n.getId().equals(concept.getEnd())) {
									continue;
								}
							}
						}
					}
					
					int randomSentence = Randomizer.random(1, this.hasPropertyStorySegment.length);
					
					if(history.contains(concept.getId())) {
						continue;
					}
					
					String storySegment = this.hasPropertyStorySegment[randomSentence-1];
					
					if(noun.getIsCommon())
						storySegment = storySegment.replace("<start>", this.determinerFixer(noun.getId()));
					else 
						storySegment = storySegment.replace("<start>", noun.getId());
					
					
					String end = concept.getEnd();
					String endPOS = concept.getEndPOS();
					
					if(endPOS.equals("proper noun")) {
						end = end.substring(0, 1).toUpperCase() + end.substring(1);
						storySegment = storySegment.replace("<end>", end);
					}
					else if (endPOS.equals("adjective") || endPOS.equals("adjective phrase")) {
						storySegment = storySegment.replace("<end>", end);
					}
					else {
						storySegment = storySegment.replace("<end>", this.determinerFixer(end));
					}
					
					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);
					
					this.history.add(concept.getId());
					found = true;
					
					return storySegment;
				}
			}
			
		}
		
		return null;
	}
	
	
	private String causes() {
		StorySentence storySentence = asr.getCurrentStorySentence();
		List<Predicate> predicates = new ArrayList<>(storySentence.getManyPredicates().values());
		Set<Concept> temp = new HashSet();
		if(!predicates.isEmpty()) {
			
			int randomPredicate = Randomizer.random(1, predicates.size());
			Predicate predicate = predicates.remove(randomPredicate - 1);
			
			List<Noun> doers = new ArrayList();
			for(Noun noun: predicate.getManyDoers().values()) {
				if(noun instanceof Character) {
					doers.add((Character)noun);
				}
			}
			
			String characters = this.wordsConjunction(doers);
			
			
			for(String concept: storySentence.getConcepts()) {
				temp.addAll(ConceptNetDAO.getConceptTo(concept, "Causes"));
			}
			
			if(temp.isEmpty()) {
				return null;
			}
			List<Concept> concepts = new ArrayList(temp);
			
			boolean found = false;
			
			while(!concepts.isEmpty() && !found) {
				int randomConcept = Randomizer.random(1, concepts.size());
				Concept concept = concepts.remove(randomConcept-1);
				
				if(history.contains(concept.getId())) {
					continue;
				}
				
				String endPOS = concept.getEndPOS();
				
				int randomSentence = 0;
				String storySegment = "";
				if(endPOS.equals("noun") || endPOS.equals("proper noun")) {
					randomSentence = Randomizer.random(1, this.causesNoun.length);
					storySegment = causesNoun[randomSentence-1];
					storySegment = storySegment.replace("<start>", concept.getStart());
					storySegment = storySegment.replace("<end>", concept.getEnd());
				}
				else if (endPOS.equals("verb") || endPOS.equals("verb phrase")) {
					randomSentence = Randomizer.random(1, this.causesVerb.length);
					storySegment = causesVerb[randomSentence-1];
					storySegment = storySegment.replace("<doer>", characters);
					VPPhraseSpec verb = nlgFactory.createVerbPhrase(concept.getEnd());
			        verb.setFeature(Feature.TENSE, Tense.PAST);
			        storySegment = storySegment.replace("<end>", realiser.realise(verb).toString());

				}
				else if (endPOS.equals("adjective") || endPOS.equals("adjective phrase")) {
					randomSentence = Randomizer.random(1, this.causesAdjective.length);
					storySegment = causesAdjective[randomSentence-1];
					storySegment = storySegment.replace("<doer>", characters);
					storySegment = storySegment.replace("<end>", concept.getEnd());
				}
				
				
				storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);
				
				this.history.add(concept.getId());
				found = true;
				
				return storySegment;
				
			}	
		}
		
		return null;
	}

}
