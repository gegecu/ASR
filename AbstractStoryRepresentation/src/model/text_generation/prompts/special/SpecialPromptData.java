package model.text_generation.prompts.special;

import java.util.List;
import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;

public class SpecialPromptData {

	private List<Noun> doers;
	private String currentPrompt;

	private Queue<String> history;
	private AbstractStoryRepresentation asr;

	public SpecialPromptData(Queue<String> history,
			AbstractStoryRepresentation asr) {
		this.history = history;
		this.asr = asr;
	}

	public void setDoers(List<Noun> doers) {
		this.doers = doers;
	}

	public List<Noun> getDoers() {
		return doers;
	}

	public void setCurrentPrompt(String currentPrompt) {
		this.currentPrompt = currentPrompt;
	}

	public String getCurrentPrompt() {
		return currentPrompt;
	}

	public AbstractStoryRepresentation getASR() {
		return asr;
	}

	public Queue<String> getHistory() {
		return history;
	}

}
