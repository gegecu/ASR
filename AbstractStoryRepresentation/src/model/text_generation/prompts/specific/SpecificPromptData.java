package model.text_generation.prompts.specific;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.text_generation.prompts.PromptData;

/**
 * Used to store prompt data for specific prompts
 */
public class SpecificPromptData extends PromptData {

	/**
	 * Contains the previous correctly answered specific prompt topics for each
	 * noun, to not be used again.
	 */
	private Map<Noun, List<String>> answered;
	/**
	 * The current generated special prompt
	 */
	private String currentTopic;
	/**
	 * Is set if the checkAnswer of SpecificPromptAnswerChecker identifies that
	 * the answer to the prompt was wrong.
	 */
	private boolean isWrong;

	/**
	 * @param history
	 *            the history to set
	 * @param asr
	 *            the asr to set
	 */
	public SpecificPromptData(Queue<String> history,
			AbstractStoryRepresentation asr) {
		super(history, asr);
		this.answered = new HashMap<>();
	}

	/**
	 * @return the answered
	 */
	public Map<Noun, List<String>> getAnswered() {
		return answered;
	}

	/**
	 * @param currentTopic
	 *            the currentTopic to set
	 */
	public void setCurrentTopic(String currentTopic) {
		this.currentTopic = currentTopic;
	}

	/**
	 * @return the currentTopic
	 */
	public String getCurrentTopic() {
		return currentTopic;
	}

	/**
	 * @param isWrong
	 *            the isWrong to set
	 */
	public void setIsWrong(boolean isWrong) {
		this.isWrong = isWrong;
	}

	/**
	 * @return the isWrong
	 */
	public boolean isWrong() {
		return isWrong;
	}

}
