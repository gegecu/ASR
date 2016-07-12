package model.text_generation.prompts;

import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;

public abstract class PromptData {

	protected Queue<String> history;
	protected AbstractStoryRepresentation asr;

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

}
