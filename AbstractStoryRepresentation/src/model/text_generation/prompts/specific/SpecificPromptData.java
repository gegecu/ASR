package model.text_generation.prompts.specific;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import model.story_representation.story_element.noun.Noun;

public class SpecificPromptData {

	private Map<Noun, List<String>> answered;
	private String currentTopic;
	private boolean isWrong;

	private Queue<String> history;

	public SpecificPromptData(Queue<String> history) {
		this.history = history;
		this.answered = new HashMap<>();
	}

	public Map<Noun, List<String>> getAnswered() {
		return answered;
	}

	public String getCurrentTopic() {
		return currentTopic;
	}

	public void setCurrentTopic(String currentTopic) {
		this.currentTopic = currentTopic;
	}

	public boolean isWrong() {
		return isWrong;
	}

	public void setWrong(boolean isWrong) {
		this.isWrong = isWrong;
	}

	public Queue<String> getHistory() {
		return history;
	}

}
