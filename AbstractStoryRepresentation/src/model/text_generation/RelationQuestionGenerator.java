package model.text_generation;

import java.util.ArrayList;
import java.util.List;

import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.VPPhraseSpec;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Event;
import model.story_representation.Predicate;
import model.story_representation.noun.Noun;
import model.utility.Randomizer;

public class RelationQuestionGenerator extends TextGeneration{
	
	private String[] hasPropertyQuestions = {"Why is <noun> <property>?", "How <property> is <noun>?"};
	private String[] capableOfQuestions = {"Why is <noun> <action>?" , "How did <noun> <action>?"};

	public RelationQuestionGenerator(AbstractStoryRepresentation asr) {
		super(asr);
	}

	@Override
	public String generateText() {
		// TODO Auto-generated method stub
		int random = Randomizer.random(1, 2);
		
		switch(random) {
			case 1:
				return hasProperty();
			case 2:
				return capableOf();
			default:
				return null;
		}
	}
	
	private String hasProperty() {
		
		List<Noun> nouns = this.getAllNounsBasedOnRelation("HasProperty");
		
		int randomNoun = Randomizer.random(1, nouns.size());
		int randomHasPropertyQuestion = Randomizer.random(1, this.hasPropertyQuestions.length);
		
		if(!nouns.isEmpty()) {
			String question = this.hasPropertyQuestions[randomHasPropertyQuestion-1];
			Noun noun = nouns.get(randomNoun-1);
			List<String> properties = noun.getAttribute("HasProperty");
			int randomProperty = Randomizer.random(1, properties.size());
			
			if(noun.getIsCommon()) {
				question = question.replace("<noun>", "the " + noun.getId());
			}
			else {
				question = question.replace("<noun>", noun.getId());
			}
			
			question = question.replace("<property>", properties.get(randomProperty-1));
			return question;
		}
		
		return null;
	}
	
	private String capableOf() {
		Event event = asr.getCurrentEvent();
		List<Noun> nouns = this.getAllNounsBasedOnRelation("CapableOf");
		
		int randomNoun = Randomizer.random(1, nouns.size());
		int randomCapableOfQuestion = Randomizer.random(1, this.capableOfQuestions.length);
		
		if(!nouns.isEmpty()) {
			String question = this.capableOfQuestions[randomCapableOfQuestion-1];
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
		        verb.setFeature(Feature.FORM, Form.GERUND);
				question = question.replace("<action>", realiser.realise(verb).toString());
			}
			else {
				question = question.replace("<action>", capableOf.get(randomCapableOf-1));
			}
			return question;
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
	
	//hasA
	
	//isA
}
