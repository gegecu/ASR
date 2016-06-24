package model.text_generation.prompts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_generation.Utilities;
import model.utility.Randomizer;
import model.utility.TypedDependencyAnswerCheckerComparator;
import model.utility.TypedDependencyComparator;

public class GeneralPrompt extends Prompt {

	private String[] nounStartDirective = { "Describe <noun>.",
			"Tell me more about <noun>.", "Write more about <noun>.",
			"I want to hear more about <noun>.",
			"Tell something more about <noun>." };

	private int descriptionThreshold = 7;

	@Override
	public String generateText(Noun noun) {
		// TODO Auto-generated method stub
		String directive = findDirective(noun, new ArrayList(Arrays.asList(nounStartDirective)));
		this.history.add(directive);
		currentNoun = noun;
		
		if (history.size() > 3) {
			history.remove();
		}
		
		return directive;
	}

	private String findDirective(Noun noun, List<String> directives) {

		String directive = null;
		while (!directives.isEmpty()
				&& (directive == null || history.contains(directive))) {

			int randomNounDirective = Randomizer.random(1, directives.size());

			directive = directives.remove(randomNounDirective - 1);

			String toBeReplaced = "";

			Map<String, Noun> ownersMap = noun.getReference("IsOwnedBy");

			if (ownersMap != null) {
				List<Noun> owners = new ArrayList<>(ownersMap.values());
				if (owners != null) {
					toBeReplaced = owners.get(owners.size() - 1).getId()
							+ "'s ";
				}
			} else {
				toBeReplaced = (noun.getIsCommon() ? "the " : "");
			}

			directive = directive
					.replace("<noun>", toBeReplaced + noun.getId());

		}

		return directive;
	}
	
	// need to fix or think of another way because possible compound compound.
	public boolean checkAnswer(String input) {
		Annotation document = new Annotation(input);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);
			
			List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
					dependencies.typedDependencies());
			Collections.sort(listDependencies, new TypedDependencyAnswerCheckerComparator());
			
			String name = null;
			
			for (TypedDependency td : listDependencies) {
				if(td.reln().toString().equals("compound")) {
					name = td.dep().lemma() + " " + td.gov().lemma();
				}
				
				if(td.reln().toString().equals("nsubj")) {
					if(name == null) {
						name = td.dep().lemma();
					}
					if(name.equals(currentNoun.getId())) {
						return true;
					}
				}
				
			}
			// get first sentence of answer only.
			break;
		}
		
		return false;
	}


}
