package model.text_generation.prompts.general;

import java.util.HashMap;
import java.util.Map;

import model.text_generation.prompts.PromptAnswerChecker;

/**
 * Prompt answer checker for general prompts
 */
public class GeneralPromptAnswerChecker extends PromptAnswerChecker {

	/**
	 * The data that is used for generating prompts and answer checking
	 */
	private GeneralPromptData generalPromptData;

	/**
	 * @param generalPromptData
	 *            the generalPromptData to set
	 */
	public GeneralPromptAnswerChecker(GeneralPromptData generalPromptData) {
		this.generalPromptData = generalPromptData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * model.text_generation.prompts.PromptAnswerChecker#postChecking(java.lang.
	 * String)
	 */
	public boolean postChecking(String answer) {

		// TODO Auto-generated method stub
		this.preprocess.preprocess(
				generalPromptData.getCurrentPrompt() + " " + answer);
		Map<String, String> corefMapping = preprocess.getCoref();

		Map<String, String> corefMappingTemp = new HashMap();

		for (Map.Entry<String, String> coref : corefMapping.entrySet()) {
			if (!coref.getKey().equals(coref.getValue())) {
				corefMappingTemp.put(coref.getValue(), coref.getKey());
			}
		}

		if (corefMappingTemp.size() == 1) {
			return true;
		}

		return false;

	}

}
