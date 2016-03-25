package model.text_generation;

import java.util.Random;

import model.story_representation.AbstractStoryRepresentation;
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
		//this.phraseElement = nlgFactory.createClause();
	}
	
	public abstract String generateText();
	
	protected int randomizer(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

}
