package model.text_generation.prompts.general;

import java.util.HashMap;
import java.util.Map;

import model.text_generation.prompts.PromptAnswerChecker;

public class GeneralPromptAnswerChecker extends PromptAnswerChecker {

	/**
	 * Prompt data to use
	 */
	private GeneralPromptData generalPromptData;

	/**
	 * @param generalPromptData
	 *            the generalPromptData to set
	 */
	public GeneralPromptAnswerChecker(GeneralPromptData generalPromptData) {
		this.generalPromptData = generalPromptData;
	}

	public boolean postChecking(String answer) {

		// TODO Auto-generated method stub
		this.preprocess.preprocess(
				generalPromptData.getCurrentPrompt() + " " + answer);
		Map<String, String> corefMapping = preprocess.getCoref();

		Map<String, String> corefMappingTemp = new HashMap();

		for (Map.Entry<String, String> coref : corefMapping.entrySet()) {
			corefMappingTemp.put(coref.getValue(), coref.getKey());
		}

		int countDuplicate = 0;

		for (Map.Entry<String, String> corefTemp : corefMappingTemp
				.entrySet()) {
			if (corefTemp.getKey().equals(corefTemp.getValue())) {
				countDuplicate++;
			}
		}

		if (corefMappingTemp.size() - countDuplicate == 1) {
			return true;
		}

		return false;

	}

}
