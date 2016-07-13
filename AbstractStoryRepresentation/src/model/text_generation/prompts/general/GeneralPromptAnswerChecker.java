package model.text_generation.prompts.general;

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
import model.instance.DictionariesInstance;
import model.text_generation.prompts.PromptAnswerChecker;
import model.utility.TypedDependencyComparator;

public class GeneralPromptAnswerChecker extends PromptAnswerChecker {

	private GeneralPromptData generalPromptData;

	public GeneralPromptAnswerChecker(GeneralPromptData generalPromptData) {
		this.generalPromptData = generalPromptData;
	}

	// need to fix or think of another way because possible compound compound.
	public boolean checkAnswer(String answer) {

		String currentPrompt = generalPromptData.getCurrentPrompt();
		
		Map<String, String> coref;

		Annotation document = new Annotation(answer);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {

			//Describe the ball. It is round.
			//Tell me more about John. Mary gave him a bath.
			//If user inputs, pronoun, it is correct because John and he are same but not John and her
			//Describe John Roberts. He is fat or John Roberts is fat. but cannot be John is fat because in TU cannot be coreferenced anyway

			preprocess.preprocess(currentPrompt + " " + sentence.toString());

			coref = preprocess.getCoref();

			int countSame = 0;
			for (Map.Entry<String, String> entry : coref.entrySet()) {
				if (entry.getKey().equals(entry.getValue())) {
					countSame++;
				}
			}

			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);

			List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
					dependencies.typedDependencies());
			Collections.sort(listDependencies, new TypedDependencyComparator());

			if (coref.size() - countSame >= 1) {
				
				boolean found = false;

				for (TypedDependency td : listDependencies) {
					if (td.reln().toString().contains("nsubj")) {
						//noun = td.dep().lemma();
						// John cried is correct, but John is <blank> is wrong

						if (!DictionariesInstance.getInstance().copulas
								.contains(td.gov().lemma())) {
							return true;
						}
						else {
							found = true;
						}
					}
					else if (td.reln().toString().equals("xcomp")) {
						if (DictionariesInstance.getInstance().copulas
								.contains(td.gov().lemma()) && found) {
							return true;
						}
					}
				}
			}
			// get first sentence of answer only.

			break;

		}

		return false;

	}

}
