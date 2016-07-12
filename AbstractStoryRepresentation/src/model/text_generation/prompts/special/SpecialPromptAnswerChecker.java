package model.text_generation.prompts.special;

import java.util.ArrayList;
import java.util.Collections;
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
import model.instance.StanfordCoreNLPInstance;
import model.story_representation.story_element.noun.Noun;
import model.text_generation.prompts.PromptAnswerChecker;
import model.text_understanding.Preprocessing;
import model.utility.TypedDependencyComparator;

public class SpecialPromptAnswerChecker extends PromptAnswerChecker {

	private static Logger log = Logger
			.getLogger(SpecialPromptAnswerChecker.class.getName());

	private StanfordCoreNLP pipeline;
	private Preprocessing preprocess;
	private SpecialPromptData specialPromptData;

	public SpecialPromptAnswerChecker(SpecialPromptData specialPromptData) {
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.preprocess = new Preprocessing();
		this.specialPromptData = specialPromptData;
	}

	public boolean checkAnswer(String answer) {

		List<Noun> doers = specialPromptData.getDoers();
		String currentPrompt = specialPromptData.getCurrentPrompt();

		int counter = 0;
		Set<String> currentDoerNames = new HashSet<>();
		Map<String, String> coref;

		for (Noun doer : doers) {
			currentDoerNames.add(doer.getId());
		}

		preprocess.preprocess(currentPrompt + " " + answer);
		String updatedText = preprocess.getUpdatedString();
		coref = preprocess.getCoref();

		Annotation document = new Annotation(updatedText);
		pipeline.annotate(document);

		boolean skipped = false;

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {

			//skip first sentence
			if (!skipped) {
				skipped = true;
				continue;
			}

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

					String noun = "";

					if (td.reln().toString().contains("nsubj")) {
						noun = td.dep().lemma();
					}

					if (td.reln().toString().equals("compound")) {
						if (td.gov().lemma().equals(noun)) {
							noun = td.dep().lemma() + " " + noun;
						}
					}

					if (currentDoerNames.contains(noun)) {
						log.debug("counter++");
						counter++;
					}

				}

			}

			if (counter == doers.size()) {
				log.debug("counter: " + counter);
				return true;
			}

			break;

		}

		return false;

	}

}
