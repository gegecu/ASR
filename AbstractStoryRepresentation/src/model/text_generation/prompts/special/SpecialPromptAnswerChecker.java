package model.text_generation.prompts.special;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.instance.DictionariesInstance;
import model.instance.StanfordCoreNLPInstance;
import model.story_representation.story_element.noun.Noun;
import model.text_generation.prompts.PromptAnswerChecker;
import model.text_understanding.Preprocessing;
import model.utility.TypedDependencyComparator;

public class SpecialPromptAnswerChecker extends PromptAnswerChecker {

	private static Logger log = Logger
			.getLogger(SpecialPromptAnswerChecker.class.getName());

	private SpecialPromptData specialPromptData;

	public SpecialPromptAnswerChecker(SpecialPromptData specialPromptData) {
		this.specialPromptData = specialPromptData;
	}

	@Override
	public boolean postChecking(String answer) {
		
		// TODO Auto-generated method stub
		this.preprocess.preprocess(specialPromptData.getCurrentPrompt() + " " + answer);
		Map<String, String> corefMapping = preprocess.getCoref();
		
		int countDuplicate = 0;
		
		for(Map.Entry<String, String> coref: corefMapping.entrySet()) {
			if(coref.getKey().equals(coref.getValue())) {
				countDuplicate++;
			}
		}
		
		if(corefMapping.size() - countDuplicate == this.specialPromptData.getDoers().size()) {
			return true;
		}
		
		return false;
	}

}
