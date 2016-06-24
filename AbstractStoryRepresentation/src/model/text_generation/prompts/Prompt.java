package model.text_generation.prompts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import model.instance.StanfordCoreNLPInstance;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;

public abstract class Prompt {
	protected Queue<String> history;
	protected StanfordCoreNLP pipeline;
	protected Noun currentNoun;
	
	public Prompt() {
		super();
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.history = new LinkedList();
	}
	
	public abstract String generateText(Noun noun);
	
	public abstract boolean checkAnswer(String input);
	
	public Noun getCurrentNoun() {
		return this.currentNoun;
	}
	
}
