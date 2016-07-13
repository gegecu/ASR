package model.text_generation.prompts.specific;

import java.util.ArrayList;
import java.util.Collections;
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
import model.utility.TypedDependencyComparator;

public class SpecificPromptAnswerChecker extends PromptAnswerChecker {

	private SpecificPromptData specificPromptData;

	public SpecificPromptAnswerChecker(SpecificPromptData specificPromptData) {
		this.specificPromptData = specificPromptData;
	}

	// need to fix or think of another way because possible compound compound.
	public boolean checkAnswer(String answer) {

		String currentPrompt = specificPromptData.getCurrentPrompt();
		Noun currentNoun = specificPromptData.getCurrentNoun();
		String currentTopic = specificPromptData.getCurrentTopic();
		Map<Noun, List<String>> answered = specificPromptData.getAnswered();

		Map<String, String> coref;

		String noun = "";
		String topicAnswer = "";

		Annotation document = new Annotation(answer);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {

			preprocess.preprocess(currentPrompt + " " + sentence.toString());

			coref = preprocess.getCoref();

			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);

			List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
					dependencies.typedDependencies());
			Collections.sort(listDependencies, new TypedDependencyComparator());

			for (TypedDependency td : listDependencies) {

				//What is the color of the ball? It is red. cannot be He is red.
				//What is the nationality of John Roberts. He is Chinese or John Roberts is Chinese.
				int countSame = 0;
				for (Map.Entry<String, String> entry : coref.entrySet()) {
					if (entry.getKey().equals(entry.getValue())) {
						countSame++;
					}
				}

				if (coref.size() - countSame >= 1) {

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

			// get first sentence of answer only.
			break;

		}

		return false;

	}

}
