package model.text_generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Event;
import model.story_representation.Predicate;
import model.story_representation.noun.Location;
import model.story_representation.noun.Noun;
import model.story_representation.noun.Character;
import model.utility.Randomizer;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.phrasespec.VPPhraseSpec;

public class DirectivesGenerator extends TextGeneration{
	
	private String[] nounStartDirective = {"Describe <noun>."
											, "Tell me more about <noun>."
											, "Write more about <noun>."
											, "I want to hear more about <noun>."
											, "Tell something more about <noun>."};
	
	private String[] causeEffectDirective = { "Tell me more why <noun> <action>."
											, "Write more about why <noun> <action>."
											, "Write the reason why <noun> can <action>."};
	
	private String[] locationDirective = {"Describe <location>."
												, "Can you say something about <location>."
												, "Tell me more about <location>."
												, "Write more about <location>."};	

	public DirectivesGenerator(AbstractStoryRepresentation asr) {
		super(asr);
	}

	@Override
	public String generateText() {
//		int random = Randomizer.random(1, 2);
//		
//		switch(random) {
//			case 1:
//				return directiveNoun();
////			case 2:
////				return locationDirective();
//			default:
//				return "Tell me more.";
//		}
		if(asr.getPartOfStory().equals("start")) {
			int random = Randomizer.random(1, 2);
			switch(random) {
				case 1:
					return directiveNoun();
				case 2:
					return locationDirective();
				default:
					return "Tell me more!";
			}
		}
		else {
			return this.capableOf();
		}
	}

	private String directiveNoun() {
		//nouns
		Event event = asr.getCurrentEvent();
		List<Noun> nouns = new ArrayList();
		nouns.addAll(event.getManyDoers().values());
		
		for(Predicate predicate: event.getManyPredicates().values()) {
			nouns.addAll(predicate.getDirectObjects().values());
			nouns.addAll(predicate.getReceivers().values());
		}
				
		if(!nouns.isEmpty()) {
			int randomNoun = Randomizer.random(1, nouns.size());
			int randomNounDirective = Randomizer.random(1, this.nounStartDirective.length);
	
			Noun noun = nouns.get(randomNoun-1);
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
		
		return null;
	}
	
	private String capableOf() {
		List<Noun> nouns = this.getAllNounsBasedOnRelation("CapableOf");
		
		if(nouns != null && !nouns.isEmpty()) {
			int randomNoun = Randomizer.random(1, nouns.size());
			int randomCapableOfQuestion = Randomizer.random(1, this.causeEffectDirective.length);
			
			if(!nouns.isEmpty()) {
				String question = this.causeEffectDirective[randomCapableOfQuestion-1];
				Noun noun = nouns.get(randomNoun-1);
				List<String> capableOf = noun.getAttribute("CapableOf");
				int randomCapableOf = Randomizer.random(1, capableOf.size());
				
				if(noun.getIsCommon()) {
					question = question.replace("<noun>", "the " + noun.getId());
				}
				else {
					question = question.replace("<noun>", noun.getId());
				}
				
				if(randomCapableOfQuestion == 1) {
					VPPhraseSpec verb = nlgFactory.createVerbPhrase(capableOf.get(randomCapableOf-1));
			        verb.setFeature(Feature.PERFECT, Tense.PRESENT);
					question = question.replace("<action>", realiser.realise(verb).toString());
				}
				else {
					question = question.replace("<action>", capableOf.get(randomCapableOf-1));
				}
				return question;
			}
		}
		
		return null;
	}
	
	private List<Noun> getAllNounsBasedOnRelation(String relation) {
		Event event = asr.getCurrentEvent();
		List<Noun> nouns = new ArrayList();
		
		for(Noun noun: event.getManyDoers().values()) {
			if(noun.getAttributes().containsKey(relation)) {
				nouns.add(noun);
			}
		}
		
		for(Predicate predicate: event.getManyPredicates().values()) {
			for(Noun noun: predicate.getReceivers().values()) {
				if(noun.getAttributes().containsKey(relation)) {
					nouns.add(noun);
				}
			}
			for(Noun noun: predicate.getDirectObjects().values()) {
				if(noun.getAttributes().containsKey(relation)) {
					nouns.add(noun);
				}
			}
		}
		
		return nouns;
	}
	
	
	private String locationDirective() {
		Event event = asr.getCurrentEvent();
		
		List<Noun> doers = new ArrayList();
		for(Noun noun: event.getManyDoers().values()) {
			if(noun instanceof Character) {
				doers.add((Character)noun);
			}
		}
		
		String characters = this.wordsConjunction(doers);

		Location location = event.getLocation();
		
		if(location != null) {
			int randomLocationDirective = Randomizer.random(1, locationDirective.length);
			
			if(characters.isEmpty()) {
				randomLocationDirective = Randomizer.random(1, locationDirective.length-2);
			}
			
			String directive = this.locationDirective[randomLocationDirective-1];
			
			directive = directive.replace("<doer>", characters);
			if(location.getIsCommon())
				directive = directive.replace("<location>", "the " + location.getId());
			else 
				directive = directive.replace("<location>", location.getId());
				
			return directive;
		}
	
		
		return null;
	}
	
}
