package model.text_generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public abstract class TextGeneration {
	protected Lexicon lexicon;
	protected NLGFactory nlgFactory;
	protected Realiser realiser;
	protected AbstractStoryRepresentation asr;
	
	//protected SPhraseSpec phraseElement;
	
	public TextGeneration(AbstractStoryRepresentation asr) {
		this.lexicon = Lexicon.getDefaultLexicon();
		this.nlgFactory = new NLGFactory(lexicon);
		this.realiser = new Realiser(lexicon);
		this.asr = asr;
	}
	
	public abstract String generateText();
	

	
	protected String determinerFixer(String word) {
		String temp = word.toLowerCase();
		if(temp.startsWith("a") || temp.startsWith("e") || temp.startsWith("i")
				|| temp.startsWith("o") || temp.startsWith("u")) {
			return "an " + temp;
		}
		else {
			return "a " + temp;
		}
	}
	
	protected String wordsConjunction(List<Noun> nouns) {
		String characters = "";
		for(int i = 0 ; i < nouns.size(); i++) {
			if(nouns.get(i).getIsCommon()) {
				characters += "the ";
			}
			if(i < nouns.size()-2) {
				characters += nouns.get(i).getId() + ", ";
			}
			else if (i < nouns.size() - 1) {
				characters += nouns.get(i).getId() + " and ";
			}
			else {
				characters += nouns.get(i).getId();
			}
		}
		
		return characters;
	}
	
	

}
