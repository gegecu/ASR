package model.text_generation.prompts.special;

import java.util.HashMap;
import java.util.Map;

import model.text_generation.prompts.PromptAnswerChecker;

public class SpecialPromptAnswerChecker extends PromptAnswerChecker {

	/**
	 * Prompt data to use
	 */
	private SpecialPromptData specialPromptData;

	/**
	 * @param specialPromptData
	 *            the specialPromptData to set
	 */
	public SpecialPromptAnswerChecker(SpecialPromptData specialPromptData) {
		this.specialPromptData = specialPromptData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * model.text_generation.prompts.PromptAnswerChecker#postChecking(java.lang.
	 * String)
	 */
	@Override
	public boolean postChecking(String answer) {

		this.preprocess.preprocess(
				specialPromptData.getCurrentPrompt() + " " + answer);
		Map<String, String> corefMapping = preprocess.getCoref();
		Map<String, String> corefMappingTemp = new HashMap<>();

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

		if (corefMappingTemp.size() - countDuplicate == this.specialPromptData
				.getDoers().size()) {
			return true;
		}

		return false;

	}

}
