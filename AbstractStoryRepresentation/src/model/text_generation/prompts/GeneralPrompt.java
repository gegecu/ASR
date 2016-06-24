package model.text_generation.prompts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.phrasespec.VPPhraseSpec;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_generation.Utilities;
import model.utility.Randomizer;
import model.utility.SurfaceRealizer;
import model.utility.TypedDependencyAnswerCheckerComparator;
import model.utility.TypedDependencyComparator;

public class GeneralPrompt extends Prompt {

	private String[] nounStartDirective = { "Describe <noun>.",
			"Tell me more about <noun>.", "Write more about <noun>.",
			"I want to hear more about <noun>.",
			"Tell something more about <noun>." };
	
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
		String noun = "";
//		String topicAnswer = "";
		
		Annotation document = new Annotation(input);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);
			
			List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
					dependencies.typedDependencies());
			Collections.sort(listDependencies, new TypedDependencyComparator());
			
			for (TypedDependency td : listDependencies) {
				if(td.reln().toString().equals("nsubj")) {
					noun = td.dep().lemma();
//					topicAnswer = td.gov().lemma();
				}
				
				if(td.reln().toString().equals("compound")) {
					if(td.gov().lemma().equals(noun)) {
						noun = td.dep().lemma() + " " + noun;
					}
//					else if (td.gov().lemma().equals(topicAnswer)) {
//						topicAnswer = td.dep().lemma() + " " + topicAnswer;
//					}
//					
				}
				
			}
			
			if(noun.equals(currentNoun.getId())) {
				return true;
			}
				
				
			// get first sentence of answer only.
			break;
		}
		
		return false;
	}


}
