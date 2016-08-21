package model.text_generation.prompts.special;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import model.text_generation.prompts.PromptAnswerChecker;

/**
 * Prompt answer checker for special prompts
 */
public class SpecialPromptAnswerChecker extends PromptAnswerChecker {

	private static Logger log = Logger
			.getLogger(SpecialPromptAnswerChecker.class.getName());

	/**
	 * The data that is used for generating prompts and answer checking
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

		// TODO Auto-generated method stub
		this.preprocess.preprocess(
				specialPromptData.getCurrentPrompt() + " " + answer);
		Map<String, String> corefMapping = preprocess.getCoref();
		Map<String, String> corefMappingTemp = new HashMap();

		for (Map.Entry<String, String> coref : corefMapping.entrySet()) {
			if (!coref.getKey().equals(coref.getValue())) {
				corefMappingTemp.put(coref.getValue(), coref.getKey());
			}
		}

		System.out.println(this.specialPromptData.getDoers().size());

		if (corefMappingTemp.size() == this.specialPromptData.getDoers()
				.size()) {
			return true;
		}

		return false;
	}

}
