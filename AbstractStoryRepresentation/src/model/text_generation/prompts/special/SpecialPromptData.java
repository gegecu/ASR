package model.text_generation.prompts.special;

import java.util.List;
import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.text_generation.prompts.PromptData;

public class SpecialPromptData extends PromptData {

	private List<Noun> doers;
	private String currentPrompt;

	public SpecialPromptData(Queue<String> history,
			AbstractStoryRepresentation asr) {
		super(history, asr);
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

}
