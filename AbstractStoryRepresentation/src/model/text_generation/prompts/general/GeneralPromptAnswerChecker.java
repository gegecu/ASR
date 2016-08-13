package model.text_generation.prompts.general;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.instance.DictionariesInstance;
import model.text_generation.prompts.PromptAnswerChecker;
import model.text_understanding.Preprocessing;
import model.utility.TypedDependencyComparator;

public class GeneralPromptAnswerChecker extends PromptAnswerChecker {

	private GeneralPromptData generalPromptData;

	public GeneralPromptAnswerChecker(
			GeneralPromptData generalPromptData) {
		this.generalPromptData = generalPromptData;
	}

	public boolean postChecking(String answer) {

		// TODO Auto-generated method stub
		this.preprocess.preprocess(generalPromptData.getCurrentPrompt() + " " + answer);
		Map<String, String> corefMapping = preprocess.getCoref();

		Map<String, String> corefMappingTemp = new HashMap();
		
		for(Map.Entry<String, String> coref: corefMapping.entrySet()) {
			corefMappingTemp.put(coref.getValue(), coref.getKey());
		}
		
		int countDuplicate = 0;

		for (Map.Entry<String, String> corefTemp : corefMappingTemp.entrySet()) {
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
