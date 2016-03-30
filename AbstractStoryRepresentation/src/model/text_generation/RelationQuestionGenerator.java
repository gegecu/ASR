package model.text_generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.VPPhraseSpec;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Predicate;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.Randomizer;

public class RelationQuestionGenerator extends TextGeneration{
	
	private String[] hasPropertyStartQuestions = {"Where can you find <noun>?"
													, "Now that <noun> is <property>. What else can you say about <noun>?"
													, "How <property> is <noun>?" };
	
	private String[] hasPropertyMidEndQuestions = {"Why is <noun> <property>?"
													, "Why did you say that <noun> is <property>?"
													, "How did <noun> become <property>?"};

	private String[] capableOfQuestions = {"Why is <noun> <action>?" 
											, "How did <noun> <action>?"
											, "What does <noun> use to <action>?"
											, "What is <noun> <action>?"};
	
	private String[] locationStartQuestions = {"What did <doer> see in <location>?"
												, "What can you say about <location>?"
												, "Describe <location>."
												, "Can you say something about <location>."
												, "Tell me more about <location>."
												, "Write more about <location>."};
	
	private String[] locationMidEndQuestions = {"What happened to <doer> in <location>?"
												, "What is <doer> doing in <location>?"
												, "Why did <doer> go to <location>?"
												, "What else did <doer> do in <location>?"};
	private Set<String> history;
										
	public RelationQuestionGenerator(AbstractStoryRepresentation asr) {
		super(asr);
		history = new HashSet();
	}

	@Override
	public String generateText() {
		// TODO Auto-generated method stub
		Set<String> response = new HashSet();
		
		String hasProperty = hasProperty();
		if(hasProperty != null) {
			response.add(hasProperty);
		}
		
		String capableOf = capableOf();
		if(capableOf != null) {
			response.add(capableOf);
		}
		
		String locationQuestion = locationQuestions();
		if(locationQuestion != null) {
			response.add(locationQuestion);
		}
		
		if(!response.isEmpty()) {
			response.removeAll(history);
			int random = Randomizer.random(1, response.size());
			return (String)response.toArray()[random-1];
		}
		else {
			return null;
		}
	}
	
	private String hasProperty() {
		
		String[] questions;
		
		if(asr.getPartOfStory().equals("start")) {
			questions = new String[this.hasPropertyStartQuestions.length];
			questions = Arrays.copyOf(this.hasPropertyStartQuestions, this.hasPropertyStartQuestions.length);
		}
		else {
			questions = new String[this.hasPropertyMidEndQuestions.length];
			questions = Arrays.copyOf(this.hasPropertyMidEndQuestions, this.hasPropertyMidEndQuestions.length);
		}			
			
		List<Noun> nouns = asr.getAllNounsBasedOnRelation("HasProperty");
		
		if(nouns != null && !nouns.isEmpty()) {
			int randomNoun = Randomizer.random(1, nouns.size());
			int randomHasPropertyQuestion = Randomizer.random(1, questions.length);
			
			if(!nouns.isEmpty()) {
				String question = questions[randomHasPropertyQuestion-1];
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
		}
		
		return null;
	}
	
	private String capableOf() {
		List<Noun> nouns = asr.getAllNounsBasedOnRelation("CapableOf");
		
		if(nouns != null && !nouns.isEmpty()) {
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
		}
		
		return null;
	}
	
	//hasA
	
	//isA
	
	private String locationQuestions() {
		StorySentence event = asr.getCurrentEvent();
		
		if(event != null) {
			Location location = event.getLocation();
		
			if(location != null) {
				List<Noun> doers = new ArrayList();
				for(Noun noun: event.getManyDoers().values()) {
					if(noun instanceof Character) {
						doers.add((Character)noun);
					}
				}
				
				String characters = this.wordsConjunction(doers);
		
				
				
				String[] questions;
				
				if(asr.getPartOfStory().equals("start")) {
					questions = new String[this.locationStartQuestions.length];
					questions = Arrays.copyOf(this.locationStartQuestions, this.locationStartQuestions.length);
				}
				else {
					questions = new String[this.locationMidEndQuestions.length];
					questions = Arrays.copyOf(this.locationMidEndQuestions, this.locationMidEndQuestions.length);
				}	
			
			
				int randomLocationDirective = Randomizer.random(1, questions.length);
				
				if(characters.isEmpty()) {
					randomLocationDirective = Randomizer.random(1, questions.length-2);
				}
				
				String directive = questions[randomLocationDirective-1];
				
				directive = directive.replace("<doer>", characters);
				if(location.getIsCommon())
					directive = directive.replace("<location>", "the " + location.getId());
				else 
					directive = directive.replace("<location>", location.getId());
					
				return directive;
			}
		}
		
		return null;
	}
}
