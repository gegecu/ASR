package model.text_generation.prompts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.Verb;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Object;
import model.story_representation.story_element.noun.Unknown;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_generation.TextGeneration;
import model.text_generation.Utilities;
import model.utility.Randomizer;
import model.utility.SurfaceRealizer;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.phrasespec.VPPhraseSpec;

public class PromptChooser extends TextGeneration {

	private static Logger log = Logger.getLogger(PromptChooser.class.getName());

	private Set<String> restrictedInGeneral;
	private Set<String> restrictedInSpecific;
	private GeneralPrompt generalPrompt;
	private SpecificPrompt specificPrompt;
	private SpecialPrompt specialPrompt;
	private Prompt currentPrompt;
	private String currentId;
	private Queue<String> history;
	private boolean answeredCorrect;
	private StanfordCoreNLP pipeline;
	private boolean isLoop;

	public PromptChooser(AbstractStoryRepresentation asr) {
		super(asr);
		history = new LinkedList<>();
		generalPrompt = new GeneralPrompt(history);
		specificPrompt = new SpecificPrompt(history);
		specialPrompt = new SpecialPrompt(history, asr, nlgFactory, realiser);
		restrictedInGeneral = new LinkedHashSet<>();
		restrictedInSpecific = new LinkedHashSet<>();
		pipeline = StanfordCoreNLPInstance.getInstance();
	}

	@Override
	public String generateText() {

		String output = null;
		int i = 0;

		if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.start)) {

			while (output == null && i < 10) {
				i++;
//				if (currentPrompt != null
//						&& currentPrompt instanceof SpecificPrompt) {
//					if (((SpecificPrompt) currentPrompt).getIsWrong()) {
//						currentPrompt = specificPrompt;
//						output = currentPrompt.generateText(asr.getNoun(currentId));
//						break;
//					}
//					else {
//						output = currentPrompt.generateText(asr.getNoun(currentId));
//					}
//				}

				i++;
				System.out.println(isLoop);
				
				String nounid = "";
				
				if(isLoop) {
					nounid = currentId;
				}
				else {
					nounid = findNounId();
				}
//				String nounid = findNounId();
				Noun noun = asr.getNoun(nounid);
				currentId = nounid;

				if (restrictedInGeneral.contains(nounid)) {
					if (noun instanceof Object || noun instanceof Character) {
						currentPrompt = specificPrompt;
					}
				} else {
					currentPrompt = generalPrompt;
				}

				output = currentPrompt.generateText(noun);

				if (currentPrompt instanceof SpecificPrompt) {
					if (((SpecificPrompt) currentPrompt).checkifCompleted()) {
						restrictedInGeneral.remove(currentId);
						restrictedInSpecific.add(currentId);
					}
				}
			}

		} else {
			output = specialPrompt.capableOf();
		}

		history.add(output);
		if (history.size() > 3) {
			history.remove();
		}

		return output;

	}

	public boolean checkAnswer(String input) {

		String temp = incompleteAnswer(input);

		Noun noun = asr.getNoun(currentId);

		answeredCorrect = false;
		isLoop = false;

		if (asr.getCurrentPartOfStory().equals("start")) {
			if (currentPrompt instanceof GeneralPrompt) {
				//wrong answer
				if (!currentPrompt.checkAnswer(temp)) {

					//forever in general prompts, never add in restrictedGeneral because all specific answered
					if (!restrictedInSpecific.contains(currentId)) {
						// answered wrong, not yet completed q/a and not object or person
						if (!(noun instanceof Location
								|| noun instanceof Unknown)) {
							log.debug(currentId);
							restrictedInGeneral.add(currentId);
							isLoop = true;
						}

					}

				} else {
					answeredCorrect = true;
				}

			} else if (currentPrompt instanceof SpecificPrompt) {

				//correct answer
				if (currentPrompt.checkAnswer(temp)) {
					answeredCorrect = true;
					if (((SpecificPrompt) currentPrompt).checkifCompleted()) {
						restrictedInGeneral.remove(currentId);
						restrictedInSpecific.add(currentId);
					}
				} else {
					((SpecificPrompt) currentPrompt).setIsWrongIgnored(true);
					isLoop = true;
				}
			}
		} else {
			answeredCorrect = specialPrompt.checkAnswer(input);
		}

		return answeredCorrect;

	}

	public void ignored() {
		if (currentPrompt instanceof SpecificPrompt) {
			((SpecificPrompt) currentPrompt).setIsWrongIgnored(false);
		}
	}
	
	public void stopLoop() {
		this.isLoop = false;
	}
	
	public boolean getIsLoop() {
		return this.isLoop;
	}

	public String incompleteAnswer(String input) {

		Annotation document = new Annotation(input);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);

			List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
					dependencies.typedDependencies());
			//Collections.sort(listDependencies, new TypedDependencyComparator());

			if (listDependencies.size() == 1) {
				return "The " + asr.getNoun(currentId).getId() + " is "
						+ listDependencies.get(0).dep().lemma().toLowerCase()
						+ ".";
			}
		}

		return input;

	}

	private String findNounId() {

		List<String> nounId = this.getNouns();
		int random = Randomizer.random(1, nounId.size());
		return nounId.get(random - 1);
	}

}
