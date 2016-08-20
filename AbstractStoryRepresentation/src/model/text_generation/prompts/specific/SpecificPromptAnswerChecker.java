package model.text_generation.prompts.specific;

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
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.story_element.noun.Noun;
import model.text_generation.prompts.PromptAnswerChecker;
import model.text_understanding.Preprocessing;
import model.utility.TypedDependencyComparator;

public class SpecificPromptAnswerChecker extends PromptAnswerChecker {

	private SpecificPromptData specificPromptData;

	public SpecificPromptAnswerChecker(SpecificPromptData specificPromptData) {
		this.specificPromptData = specificPromptData;
	}

	// need to fix or think of another way because possible compound compound.

	@Override
	public boolean postChecking(String answer) {
		// TODO Auto-generated method stub
		String currentPrompt = specificPromptData.getCurrentPrompt();
		Noun currentNoun = specificPromptData.getCurrentNoun();
		String currentTopic = specificPromptData.getCurrentTopic();
		Map<Noun, List<String>> answered = specificPromptData.getAnswered();
		
		this.preprocess.preprocess(currentPrompt + " " + answer);
		
		Map<String, String> corefMapping = preprocess.getCoref();
		Map<String, String> corefMappingTemp = new HashMap();
		
		for(Map.Entry<String, String> coref : corefMapping.entrySet()) {
			if(!coref.getKey().equals(coref.getValue())) {
				corefMappingTemp.put(coref.getValue(), coref.getKey());
			}
		}
		
		if(corefMappingTemp.size() == 1) {
			String noun = "";
			String topicAnswer = "";

			Annotation document = new Annotation(answer);
			pipeline.annotate(document);

			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			for (CoreMap sentence : sentences) {
				SemanticGraph dependencies = sentence
						.get(CollapsedCCProcessedDependenciesAnnotation.class);

				List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
						dependencies.typedDependencies());
				Collections.sort(listDependencies, new TypedDependencyComparator());
				
				for (TypedDependency td : listDependencies) {
					noun = currentNoun.getId();

					if (td.reln().toString().equals("nsubj")) {
						//noun = td.dep().lemma();
						topicAnswer = td.gov().lemma();
					}

					if (td.reln().toString().equals("compound")) {
						if (td.gov().lemma().equals(topicAnswer)) {
							topicAnswer = td.dep().lemma() + " " + topicAnswer;
						}
					}
				}
			}
			
			if (ConceptNetDAO.checkSRL(topicAnswer, "IsA", currentTopic)
					&& noun.equals(currentNoun.getId())) {

				List<String> topics = answered.get(currentNoun);
				if (topics == null) {
					topics = new ArrayList<>();
				}
				topics.add(currentTopic);
				answered.put(currentNoun, topics);
				return true;

			}

		}
		return false;
		
	}

}
