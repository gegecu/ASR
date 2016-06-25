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
import model.text_understanding.Preprocessing;
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
		currentPrompt = directive;
		
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
		
		Map<String, String> coref;
	
		Annotation document = new Annotation(input);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			
			//Describe the ball. It is round.
			//Tell me more about John. Mary gave him a bath.
			//If user inputs, pronoun, it is correct because John and he are same but not John and her
			//Describe John Roberts. He is fat or John Roberts is fat. but cannot be John is fat because in TU cannot be coreferenced anyway
			
			coref = preprocess.preprocess(currentPrompt + " " + sentence.toString());
			
			int countSame = 0;
			for(Map.Entry<String, String> entry: coref.entrySet()) {
				if(entry.getKey().equals(entry.getValue())) {
					countSame++;
				}
			}
			
			if(coref.size() - countSame >= 1) {
				return true;
			}
			// get first sentence of answer only.

			break;
			
		}
		
		return false;
	}


}
