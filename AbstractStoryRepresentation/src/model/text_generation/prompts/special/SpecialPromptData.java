package model.text_generation.prompts.special;

import java.util.List;
import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.text_generation.prompts.PromptData;

/**
 * Used to store prompt data for special prompts
 */
public class SpecialPromptData extends PromptData {

	/**
	 * List of nouns that was used in generating the current special prompt <br>
	 * <br>
	 * SpecialPromptGenerator sets the value when generating prompts
	 */
	private List<Noun> doers;

	/**
	 * @param history
	 *            the history to set
	 * @param asr
	 *            the asr to set
	 */
	public SpecialPromptData(Queue<String> history,
			AbstractStoryRepresentation asr) {
		super(history, asr);
	}

	/**
	 * @param doers
	 *            the doers to set
	 */
	public void setDoers(List<Noun> doers) {
		this.doers = doers;
	}

	/**
	 * @return the doers
	 */
	public List<Noun> getDoers() {
		return doers;
	}

}
