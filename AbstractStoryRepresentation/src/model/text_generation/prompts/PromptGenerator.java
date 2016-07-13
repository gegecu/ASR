package model.text_generation.prompts;

import java.util.Queue;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import model.instance.StanfordCoreNLPInstance;
import model.story_representation.story_element.noun.Noun;
import model.text_understanding.Preprocessing;

public abstract class PromptGenerator {

	protected Queue<String> history;
	protected StanfordCoreNLP pipeline;
	protected Preprocessing preprocess;

	public PromptGenerator(Queue<String> history) {
		super();
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.history = history;
		this.preprocess = new Preprocessing();
	}

	public abstract String generateText(Noun noun);

}
