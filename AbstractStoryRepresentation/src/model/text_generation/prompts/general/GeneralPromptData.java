package model.text_generation.prompts.general;

import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.text_generation.prompts.PromptData;

/**
 * Used to store prompt data for general prompts
 */
public class GeneralPromptData extends PromptData {

	/**
	 * @param history
	 *            the history to set
	 * @param asr
	 *            the asr to set
	 */
	public GeneralPromptData(Queue<String> history,
			AbstractStoryRepresentation asr) {
		super(history, asr);
	}

}
