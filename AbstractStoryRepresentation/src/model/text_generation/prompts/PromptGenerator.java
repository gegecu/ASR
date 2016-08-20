package model.text_generation.prompts;

import java.util.Queue;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import model.instance.StanfordCoreNLPInstance;
import model.story_representation.story_element.noun.Noun;
import model.text_understanding.Preprocessing;

/**
 * Abstract class for prompt generation
 */
public abstract class PromptGenerator {

	/**
	 * To track history of generated prompts
	 */
	protected Queue<String> history;
	/**
	 * Stanford CoreNLP Pipeline
	 */
	protected StanfordCoreNLP pipeline;
	/**
	 * Used to preprocess the story input
	 */
	protected Preprocessing preprocess;

	/**
	 * initialize the variables
	 * 
	 * @param history
	 *            the history to set
	 */
	public PromptGenerator(Queue<String> history) {
		super();
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.history = history;
		this.preprocess = new Preprocessing();
	}

	/**
	 * @param noun
	 *            the noun topic to use for generating prompts
	 * @return generated prompt according to the rules
	 */
	public abstract String generateText(Noun noun);

}
