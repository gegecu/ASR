package model.text_generation.prompts;

import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;

public abstract class PromptData {

	protected Queue<String> history;
	protected AbstractStoryRepresentation asr;
	protected Noun currentNoun;
	protected String currentPrompt;

	public PromptData(Queue<String> history, AbstractStoryRepresentation asr) {
		this.history = history;
		this.asr = asr;
	}

	public Queue<String> getHistory() {
		return history;
	}

	public AbstractStoryRepresentation getASR() {
		return asr;
	}

	public Noun getCurrentNoun() {
		return currentNoun;
	}

	public void setCurrentNoun(Noun currentNoun) {
		this.currentNoun = currentNoun;
	}

	public String getCurrentPrompt() {
		return currentPrompt;
	}

	public void setCurrentPrompt(String currentPrompt) {
		this.currentPrompt = currentPrompt;
	}

}
