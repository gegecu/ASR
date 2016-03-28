package model.text_generation;

import java.util.ArrayList;
import java.util.List;

import model.knowledge_base.conceptnet.Concept;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Event;
import model.story_representation.Predicate;
import model.story_representation.noun.Character;
import model.story_representation.noun.Noun;
import model.utility.Randomizer;

public class StorySegmentGenerator extends TextGeneration{
	
	private String[] atLocationStorySegment = {"There is <start> in <end>."
												, "<end> has <start>."
												, "<doer> saw <start> in <end>."};
	
	private String[] hasAStorySegment = {"<start> has <end>."};
	
	private String[] isAStorySegment = {"<start> is <end>."};
	
	private String[] hasPropertyStorySegment = {"<start> can be <end>."};
	
	private List<Integer> history; 
			
	public StorySegmentGenerator(AbstractStoryRepresentation asr) {
		super(asr);
		this.history = new ArrayList();
	}

	@Override
	public String generateText() {
		// TODO Auto-generated method stub
		int random = Randomizer.random(1, 4);
		
		switch(random) {
			case 1:
				return atLocation();
			case 2:
				return hasA();
			case 3:
				return isA();
			case 4:
				return hasProperty();
			default:
				return null;
		}
	}
	
	private String atLocation() {
		Event event = asr.getCurrentEvent();
		if(event.getLocation() != null) {
			String[] words = event.getLocation().getId().split(" ");
			String word = words[words.length-1];
			List<Concept> concepts = ConceptNetDAO.getConceptFrom(word, "AtLocation");

			List<Noun> doers = new ArrayList();
			for(Noun noun: event.getManyDoers().values()) {
				if(noun instanceof Character) {
					doers.add((Character)noun);
				}
			}
			
			String characters = this.wordsConjunction(doers);
			
			if(concepts != null)  {
				
				boolean found = false;
				
				while(!concepts.isEmpty() && !found) { 
				
					int randomConcept = Randomizer.random(1, concepts.size());

					int randomSentence = Randomizer.random(1, this.atLocationStorySegment.length);
					
					if(characters.isEmpty()) {
						randomSentence = Randomizer.random(1, this.atLocationStorySegment.length-1);
					}
					
					Concept concept = concepts.remove(randomConcept-1);
					
					if(history.contains(concept.getId())) {
						continue;
					}
					
					String storySegment = this.atLocationStorySegment[randomSentence-1];
					
					storySegment = storySegment.replace("<doer>", characters);

					if(event.getLocation().getIsCommon()) {
						storySegment = storySegment.replace("<end>", "the " + event.getLocation().getId());
					}
					else {
						storySegment = storySegment.replace("<end>", event.getLocation().getId());
					}
					
					String start = concept.getStart();
	
					storySegment = storySegment.replace("<start>", this.determinerFixer(start));
					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);
				
					this.history.add(concept.getId());
					found = true;
					
					return storySegment;
				}
			}
		}
		return null;
	}
	
	private String hasA() {
		Event event = asr.getCurrentEvent();
		
		List<Noun> nouns = new ArrayList();
		nouns.addAll(event.getManyDoers().values());
		
		for(Predicate predicate: event.getManyPredicates().values()) {
			nouns.addAll(predicate.getDirectObjects().values());
			nouns.addAll(predicate.getReceivers().values());
		}
		
		if(event.getLocation() != null)
			nouns.add(event.getLocation());
		
		boolean found = false;
		
		
		
		while(!nouns.isEmpty() && !found) {
			int randomNoun = Randomizer.random(1, nouns.size());
			Noun noun = nouns.remove(randomNoun-1);
			List<Concept> concepts = ConceptNetDAO.getConceptTo(noun.getId(), "HasA");
			if(concepts == null) {
				continue;
			}
			else {
				while(!concepts.isEmpty() && !found) {
					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept-1);
					
					int randomSentence = Randomizer.random(1, this.hasAStorySegment.length);
					
					if(history.contains(concept.getId())) {
						continue;
					}
					
					String storySegment = this.hasAStorySegment[randomSentence-1];
					
					if(noun.getIsCommon())
						storySegment = storySegment.replace("<start>", this.determinerFixer(noun.getId()));
					else 
						storySegment = storySegment.replace("<start>", noun.getId());
					storySegment = storySegment.replace("<end>", this.determinerFixer(concept.getEnd()));
					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);
					
					this.history.add(concept.getId());
					found = true;
					
					return storySegment;
				}
			}
			
		}
		
		return null;
	}
	
	private String isA() {
		Event event = asr.getCurrentEvent();
		
		List<Noun> nouns = new ArrayList();
		nouns.addAll(event.getManyDoers().values());
		
		for(Predicate predicate: event.getManyPredicates().values()) {
			nouns.addAll(predicate.getDirectObjects().values());
			nouns.addAll(predicate.getReceivers().values());
		}
		
		if(event.getLocation() != null)
			nouns.add(event.getLocation());
		
		boolean found = false;
		
		
		
		while(!nouns.isEmpty() && !found) {
			int randomNoun = Randomizer.random(1, nouns.size());
			Noun noun = nouns.remove(randomNoun-1);
			List<Concept> concepts = ConceptNetDAO.getConceptTo(noun.getId(), "IsA");
			if(concepts == null) {
				continue;
			}
			else {
				while(!concepts.isEmpty() && !found) {
					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept-1);
					
					int randomSentence = Randomizer.random(1, this.isAStorySegment.length);
					
					if(history.contains(concept.getId())) {
						continue;
					}
					
					String storySegment = this.isAStorySegment[randomSentence-1];
					
					if(noun.getIsCommon())
						storySegment = storySegment.replace("<start>", this.determinerFixer(noun.getId()));
					else 
						storySegment = storySegment.replace("<start>", noun.getId());
					storySegment = storySegment.replace("<end>", this.determinerFixer(concept.getEnd()));
					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);
					
					this.history.add(concept.getId());
					found = true;
					
					return storySegment;
				}
			}
			
		}
		
		return null;
	}
	
	private String hasProperty() {
		Event event = asr.getCurrentEvent();
		
		List<Noun> nouns = new ArrayList();
		nouns.addAll(event.getManyDoers().values());
		
		for(Predicate predicate: event.getManyPredicates().values()) {
			nouns.addAll(predicate.getDirectObjects().values());
			nouns.addAll(predicate.getReceivers().values());
		}
		
		if(event.getLocation() != null)
			nouns.add(event.getLocation());
		
		boolean found = false;
		
		
		
		while(!nouns.isEmpty() && !found) {
			int randomNoun = Randomizer.random(1, nouns.size());
			Noun noun = nouns.remove(randomNoun-1);
			List<Concept> concepts = ConceptNetDAO.getConceptTo(noun.getId(), "HasProperty");
			if(concepts == null) {
				continue;
			}
			else {
				while(!concepts.isEmpty() && !found) {
					
					
					int randomConcept = Randomizer.random(1, concepts.size());
					Concept concept = concepts.remove(randomConcept-1);
					
					int randomSentence = Randomizer.random(1, this.hasPropertyStorySegment.length);
					
					if(history.contains(concept.getId())) {
						continue;
					}
					
					String storySegment = this.hasPropertyStorySegment[randomSentence-1];
					
					if(noun.getIsCommon())
						storySegment = storySegment.replace("<start>", this.determinerFixer(noun.getId()));
					else 
						storySegment = storySegment.replace("<start>", noun.getId());
					storySegment = storySegment.replace("<end>", this.determinerFixer(concept.getEnd()));
					storySegment = storySegment.substring(0, 1).toUpperCase() + storySegment.substring(1);
					
					this.history.add(concept.getId());
					found = true;
					
					return storySegment;
				}
			}
			
		}
		
		return null;
	}

}
