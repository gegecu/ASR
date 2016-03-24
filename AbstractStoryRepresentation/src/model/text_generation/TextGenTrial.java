package model.text_generation;

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;

import java.util.Random;

import simplenlg.features.*;

public class TextGenTrial {

	private Lexicon lexicon;
	private NLGFactory nlgFactory;
	private Realiser realiser;

	private SPhraseSpec phraseElement;
	
	private String verb;
	private String subject;
	private String directObject;
	private String indirectObject;
	
	public TextGenTrial() {
		this.lexicon = Lexicon.getDefaultLexicon();
		this.nlgFactory = new NLGFactory(lexicon);
		this.realiser = new Realiser(lexicon);
	}
	
	public void setVerb(String verb) {
		this.verb = verb;
	}
	
	public void setSubject (String subject) {
		this.subject = subject;
	}
	
	public void setDirectObject (String dirObject) {
		this.directObject = dirObject;
	}

	public void setIndirectObject (String indirObject) {
		this.indirectObject = indirObject;
	}
	
	private void checkPresentElements() {
	
		this.phraseElement = nlgFactory.createClause();
		
		if (this.verb != null) {
	//		phraseElement.setVerbPhrase(verb);
			this.phraseElement.setVerb(this.verb);			
		}
	
		if (this.subject != null) {
			this.phraseElement.setSubject(this.subject);			
		}
	
		if (this.directObject != null) {
			this.phraseElement.setObject(this.directObject);			
		}
		
		if (this.indirectObject != null) {
			this.phraseElement.setIndirectObject(this.indirectObject);
		}
		
	}
	
	public String createHowClause () {

		this.checkPresentElements();

//		phraseElement.setFeature(Feature.TENSE, Tense.FUTURE);
		this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW_PREDICATE);
		String output = realiser.realiseSentence(this.phraseElement);
		
		return output;

	}
	
	public String determineQuestion() {
		
		this.checkPresentElements();
		
		Random rand = new Random();
		int max = 4;
		int min = 1;
		int randomNum = rand.nextInt((max - min) + 1) + min;
		
		System.out.println("Random Num: " + randomNum);
		
		if(this.subject == null) {
			System.out.println("Missing subject");
			
			switch(randomNum) {
				case 1 :
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);	
					break;
				case 2:
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_SUBJECT);
					break;
				case 3 :
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW);
					break;
				case 4 :
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHERE);
					break;
			}
			
		} else if (this.directObject == null) {
			System.out.println("Missing direct object");
			
			switch(randomNum) {
				case 1 : 
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
					break;
				case 2:
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);
					break;
				case 3 :
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHY);
					break;
				case 4 :
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW_PREDICATE);
					break;
			}
		} else if (this.indirectObject == null) {
			System.out.println("Missing indirect object");
			
			switch(randomNum) {
				case 1 : 
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHY);
					break;
				case 2 :
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_INDIRECT_OBJECT);
					break;
				case 3 :
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW);
					break;
				case 4 :
					this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHERE);
					break;
			}
		}
		
		String output = realiser.realiseSentence(this.phraseElement);
		
		return output;
	}
	
}
