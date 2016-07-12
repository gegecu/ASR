package model.text_generation.prompts.general;

import java.util.Queue;

public class GeneralPromptData {

	private Queue<String> history;

	public GeneralPromptData(Queue<String> history) {
		this.history = history;
	}

	public Queue<String> getHistory() {
		return history;
	}

}
