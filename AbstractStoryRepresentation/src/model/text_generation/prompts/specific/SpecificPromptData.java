package model.text_generation.prompts.specific;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.text_generation.prompts.PromptData;

public class SpecificPromptData extends PromptData {

	private Map<Noun, List<String>> answered;
	private String currentTopic;
	private boolean isWrong;

	public SpecificPromptData(Queue<String> history,
			AbstractStoryRepresentation asr) {
		super(history, asr);
		this.answered = new HashMap<>();
	}

	public Map<Noun, List<String>> getAnswered() {
		return answered;
	}

	public void setCurrentTopic(String currentTopic) {
		this.currentTopic = currentTopic;
	}

	public String getCurrentTopic() {
		return currentTopic;
	}

	public void setWrong(boolean isWrong) {
		this.isWrong = isWrong;
	}

	public boolean isWrong() {
		return isWrong;
	}

}
