package model.text_generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Predicate;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.Randomizer;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.phrasespec.VPPhraseSpec;

public class DirectivesGenerator extends TextGeneration{
	
	// <= 7 hasproperty, capableof, isA, hasA
	
	private String[] nounStartDirective = {"Describe <noun>."
											, "Tell me more about <noun>."
											, "Write more about <noun>."
											, "I want to hear more about <noun>."
											, "Tell something more about <noun>."};
	
	private String[] nounStartDirectiveAlternative = {"You have mentioned <noun> awhile ago, tell me more about <noun>"};
	
	private int descriptionThreshold = 7;
	
	private String[] causeEffectDirective = { "Tell me more why <noun> <action>."
											, "Write more about why <noun> <action>."
											, "Write the reason why <noun> <action>."};
	
	private String[] causeEffectAlternative = { "Tell me more what happened."};
	
	private String[] locationDirective = {"Describe <location>."
												, "Can you say something about <location>."
												, "Tell me more about <location>."
												, "Write more about <location>."};	
	
	private Set<String> history;

	public DirectivesGenerator(AbstractStoryRepresentation asr) {
		super(asr);
		history = new HashSet();
	}

	@Override
	public String generateText() {
		Set<String> response = new HashSet();
		if(asr.getPartOfStory().equals("start")) {
			
			String directiveNoun = this.directiveNoun();
			if(directiveNoun != null) {
				response.add(directiveNoun);
			}

		}
		else {
			String directiveCapableOf = this.capableOf();
			if(directiveCapableOf != null) {
				response.add(directiveCapableOf);
			}
		}
		response.removeAll(history);
		if(!response.isEmpty()) {
			int random = Randomizer.random(1, response.size());
			return (String)response.toArray()[random-1];
		}
		else {
			return null;
		}
	}

	private String directiveNoun() {
		//nouns
		StorySentence storySentence = asr.getCurrentStorySentence();
		List<String> nounId = storySentence.getAllNounsInStorySentence();
		
		while(!nounId.isEmpty() && storySentence != null) {
			int threshold = 0;
			
			int randomNoun = Randomizer.random(1, nounId.size());
			int randomNounDirective = Randomizer.random(1, this.nounStartDirective.length);
			Noun noun = asr.getNoun(nounId.get(randomNoun-1));
			
			for(Set<String> attributes: noun.getAttributes().values()){
				threshold += attributes.size();
			}
			
			for(Set<Noun> references: noun.getReferences().values()) {
				threshold += references.size();
			}
			
			if(threshold >= descriptionThreshold) {
				continue;
			}
			
			if(noun.getIsCommon()) {
				String directive = this.nounStartDirective[randomNounDirective-1];
				directive = directive.replace("<noun>", "the " + noun.getId());
				return directive;
			}
			else {
				String directive = this.nounStartDirective[randomNounDirective-1];
				directive = directive.replace("<noun>", noun.getId());
				return directive;
			}
		}
		
		if(nounId.isEmpty() && storySentence != null) {
			nounId = new ArrayList(asr.getManyNouns().keySet());
			nounId.removeAll(storySentence.getAllNounsInStorySentence());
			while(!nounId.isEmpty()) {
				int threshold = 0;
				
				int randomNoun = Randomizer.random(1, nounId.size());
				int randomNounDirective = Randomizer.random(1, this.nounStartDirectiveAlternative.length);
				Noun noun = asr.getNoun(nounId.get(randomNoun-1));
				
				for(Set<String> attributes: noun.getAttributes().values()){
					threshold += attributes.size();
				}
				
				for(Set<Noun> references: noun.getReferences().values()) {
					threshold += references.size();
				}
				
				if(threshold >= descriptionThreshold) {
					continue;
				}
				
				if(noun.getIsCommon()) {
					String directive = this.nounStartDirectiveAlternative[randomNounDirective-1];
					directive = directive.replace("<noun>", "the " + noun.getId());
					return directive;
				}
				else {
					String directive = this.nounStartDirectiveAlternative[randomNounDirective-1];
					directive = directive.replace("<noun>", noun.getId());
					return directive;
				}
			}
			descriptionThreshold+=2;
		}
		
		
		return null;
	}
	
	private String capableOf() {
		StorySentence storySentence = asr.getCurrentStorySentence();
		List<Predicate> predicates = new ArrayList(storySentence.getManyPredicates().values());
		
		if(!predicates.isEmpty()) {
			int randomPredicate = Randomizer.random(1, predicates.size());
			int randomCapableOfQuestion = Randomizer.random(1, this.causeEffectDirective.length);
			
			Predicate predicate = predicates.get(randomPredicate);
			String directive = this.causeEffectDirective[randomCapableOfQuestion-1];
			
			List<Noun> doers = new ArrayList(predicate.getManyDoers().values());
			
			directive = directive.replace("<noun>", this.wordsConjunction(doers));

			VPPhraseSpec verb = nlgFactory.createVerbPhrase(predicate.getAction());
		    verb.setFeature(Feature.PERFECT, Tense.PRESENT);
		    directive = directive.replace("<action>", realiser.realise(verb).toString());
		    return directive;

		}
		else {
			int randomCapableOfQuestion = Randomizer.random(1, this.causeEffectDirective.length);
			return this.causeEffectAlternative[randomCapableOfQuestion];
		}
	}
	
//	private String locationDirective() {
//		StorySentence event = asr.getCurrentEvent();
//		
//		if(event != null) {
//			List<Noun> doers = new ArrayList();
//			for(Noun noun: event.getManyDoers().values()) {
//				if(noun instanceof Character) {
//					doers.add((Character)noun);
//				}
//			}
//			
//			String characters = this.wordsConjunction(doers);
//	
//			Location location = event.getLocation();
//			
//			if(location != null) {
//				int randomLocationDirective = Randomizer.random(1, locationDirective.length);
//				
//				if(characters.isEmpty()) {
//					randomLocationDirective = Randomizer.random(1, locationDirective.length-2);
//				}
//				
//				String directive = this.locationDirective[randomLocationDirective-1];
//				
//				directive = directive.replace("<doer>", characters);
//				if(location.getIsCommon())
//					directive = directive.replace("<location>", "the " + location.getId());
//				else 
//					directive = directive.replace("<location>", location.getId());
//					
//				return directive;
//			}
//		}
//		
//		return null;
//	}
	
}
