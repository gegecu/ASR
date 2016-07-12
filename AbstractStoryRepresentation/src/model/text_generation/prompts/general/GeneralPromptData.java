package model.text_generation.prompts.general;

import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.text_generation.prompts.PromptData;

public class GeneralPromptData extends PromptData {

	public GeneralPromptData(Queue<String> history,
			AbstractStoryRepresentation asr) {
		super(history, asr);
	}

}
