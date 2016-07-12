package model.text_generation.prompts;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import model.instance.StanfordCoreNLPInstance;
import model.story_representation.story_element.noun.Noun;
import model.text_understanding.Preprocessing;

public abstract class PromptAnswerChecker {

	protected StanfordCoreNLP pipeline;
	protected Preprocessing preprocess;
	protected Noun currentNoun;
	protected String currentPrompt;

	public PromptAnswerChecker() {
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.preprocess = new Preprocessing();
	}

	public abstract boolean checkAnswer(String answer);

}
