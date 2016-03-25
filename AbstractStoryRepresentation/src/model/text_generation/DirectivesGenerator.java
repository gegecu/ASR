package model.text_generation;

import java.util.ArrayList;
import java.util.List;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Event;
import model.story_representation.Predicate;
import model.story_representation.noun.Location;
import model.story_representation.noun.Noun;
import model.story_representation.noun.Character;

public class DirectivesGenerator extends TextGeneration{
	
	private String[] nounDirective = {"Describe <noun>.", "Tell me more about <noun>.", "Write more about <noun>."};
	private String[] locationDirective = {"What did <doer> see in <location>?", "What happened in <location>?"
			, "What is <doer> doing in <location>"};

	public DirectivesGenerator(AbstractStoryRepresentation asr) {
		super(asr);
	}

	@Override
	public String generateText() {
		int random = this.randomizer(1, 2);
		
		switch(random) {
			case 1:
				return directiveNoun();
			case 2:
				return locationDirective();
			default:
				return "Tell me more.";
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
		
		int randomNoun = this.randomizer(1, nouns.size());
		int randomNounDirective = this.randomizer(1, this.nounDirective.length);

		Noun noun = nouns.get(randomNoun-1);
		if(noun.getIsCommon()) {
			String directive = this.nounDirective[randomNounDirective-1];
			directive = directive.replace("<noun>", "the " + noun.getId());
		}
		else {
			String directive = this.nounDirective[randomNounDirective-1];
			directive = directive.replace("<noun>", noun.getId());
		}
		
		return null;
	}
	
	private String locationDirective() {
		Event event = asr.getCurrentEvent();
		
		List<Character> doers = new ArrayList();
		for(Noun noun: event.getManyDoers().values()) {
			if(noun instanceof Character) {
				doers.add((Character)noun);
			}
		}
		
		String characters = "";
		for(int i = 0 ; i < doers.size(); i++) {
			if(i < doers.size()-2) {
				characters += doers.get(i).getId() + ", ";
			}
			else if (i < doers.size() - 1) {
				characters += doers.get(i).getId() + " and ";
			}
			else {
				characters += doers.get(i).getId();
			}
		}
		
		
		Location location = event.getLocation();
		int randomLocationDirective = this.randomizer(1, this.locationDirective.length);
		
		if(location != null) {
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