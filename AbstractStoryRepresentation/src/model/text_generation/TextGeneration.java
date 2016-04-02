package model.text_generation;

import model.story_representation.AbstractStoryRepresentation;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

public abstract class TextGeneration {
	protected Lexicon lexicon;
	protected NLGFactory nlgFactory;
	protected Realiser realiser;
	protected AbstractStoryRepresentation asr;
	
	public TextGeneration(AbstractStoryRepresentation asr) {
		this.lexicon = Lexicon.getDefaultLexicon();
		this.nlgFactory = new NLGFactory(lexicon);
		this.realiser = new Realiser(lexicon);
		this.asr = asr;
	}
	
	public abstract String generateText();
}
