package model.text_generation.prompts;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import model.instance.StanfordCoreNLPInstance;
import model.text_understanding.Preprocessing;

public abstract class PromptAnswerChecker {

	protected StanfordCoreNLP pipeline;
	protected Preprocessing preprocess;

	public PromptAnswerChecker() {
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.preprocess = new Preprocessing();
	}

	public abstract boolean checkAnswer(String answer);

}
