package model.text_generation.prompts;

import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;

/**
 * Abstract class to store prompt data
 */
public abstract class PromptData {

	/**
	 * To track history of generated prompts
	 */
	protected Queue<String> history;
	/**
	 * Used to store the extracted story elements
	 */
	protected AbstractStoryRepresentation asr;
	/**
	 * Currently used noun used in generating prompt
	 */
	protected Noun currentNoun;
	/**
	 * Current generated prompt
	 */
	protected String currentPrompt;

	/**
	 * @param history
	 *            the history to set
	 * @param asr
	 *            the asr to set
	 */
	public PromptData(Queue<String> history, AbstractStoryRepresentation asr) {
		this.history = history;
		this.asr = asr;
	}

	/**
	 * @return the history
	 */
	public Queue<String> getHistory() {
		return history;
	}

	/**
	 * @return the asr
	 */
	public AbstractStoryRepresentation getASR() {
		return asr;
	}

	/**
	 * @return the currentNoun
	 */
	public Noun getCurrentNoun() {
		return currentNoun;
	}

	/**
	 * @param currentNoun
	 *            the currentNoun to set
	 */
	public void setCurrentNoun(Noun currentNoun) {
		this.currentNoun = currentNoun;
	}

	/**
	 * @return the currentPrompt
	 */
	public String getCurrentPrompt() {
		return currentPrompt;
	}

	/**
	 * @param currentPrompt
	 *            the currentPrompt to set
	 */
	public void setCurrentPrompt(String currentPrompt) {
		this.currentPrompt = currentPrompt;
	}

}
