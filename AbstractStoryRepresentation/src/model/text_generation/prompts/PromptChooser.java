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
	private Set<String> restrictedInGeneral;
	private Set<String> restrictedInSpecific;
	private GeneralPrompt generalPrompt;
	private SpecificPrompt specificPrompt;
	private Prompt currentPrompt;
	private int descriptionThreshold;
	private String currentId;
	private Queue<String> history;
	private boolean answeredCorrect;
	private StanfordCoreNLP pipeline;

	private static Logger log = Logger.getLogger(PromptChooser.class.getName());

	private String[] causeEffectDirective = {
			"Tell me more why <noun> <action>.",
			"Write more about why <noun> <action>.",
			"Write the reason why <noun> <action>."};

	private String[] causeEffectAlternative = {"Tell me more what happened."};

	public PromptChooser(AbstractStoryRepresentation asr) {
		super(asr);
		history = new LinkedList<>();
		generalPrompt = new GeneralPrompt();
		specificPrompt = new SpecificPrompt();
		descriptionThreshold = 7;
		restrictedInGeneral = new LinkedHashSet<>();
		restrictedInSpecific = new LinkedHashSet<>();
		pipeline = StanfordCoreNLPInstance.getInstance();
	}

	@Override
	public String generateText() {

		String output = null;

		if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.start)) {
			
			while(output == null) {
			
				if(currentPrompt != null && currentPrompt instanceof SpecificPrompt) {
					if(((SpecificPrompt) currentPrompt).getIsWrong()) {
						currentPrompt = specificPrompt;
						return currentPrompt.generateText(null);
					}
				}
				
				String nounid = findNounId();
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
				
				if(currentPrompt instanceof SpecificPrompt) {
					if (((SpecificPrompt) currentPrompt).checkifCompleted()) {
						restrictedInGeneral.remove(currentId);
						restrictedInSpecific.add(currentId);
					}
				}
			}

		} else {
			output = capableOf();
			history.add(output);
			if (history.size() > 3) {
				history.remove();
			}
		}

		log.debug("text gen: " + output);

		return output;

	}

	private String capableOf() {

		StorySentence storySentence = asr.getCurrentStorySentence();
		List<Event> predicates = new ArrayList<>(
				storySentence.getManyPredicates().values());
		List<String> directives = new ArrayList<>(
				Arrays.asList(this.causeEffectDirective));

		String directive = null;

		while (!predicates.isEmpty() && (directive == null
				|| (directive != null && history.contains(directive)))) {

			int randomPredicate = Randomizer.random(1, predicates.size());
			Event predicate = predicates.remove(randomPredicate - 1);

			while (!directives.isEmpty()) {

				int randomCapableOfQuestion = Randomizer.random(1,
						directives.size());
				directive = directives.remove(randomCapableOfQuestion - 1);

				List<Noun> doers = new ArrayList<>(
						predicate.getManyDoers().values());

				directive = directive.replace("<noun>",
						SurfaceRealizer.wordsConjunction(doers));
				
				String action = "";
				
				Collection<Noun> directObjects = predicate.getDirectObjects()
						.values();

				if(!predicate.isNegated()) {
				
					VPPhraseSpec verb = nlgFactory
							.createVerbPhrase(predicate.getVerb().getAction());

					if (directObjects.size() > 0) {
						verb.setFeature(Feature.TENSE, Tense.PAST);
						action = realiser.realise(verb).toString();
	
					} else {
						verb.setFeature(Feature.PROGRESSIVE, true);
						action = realiser.realise(verb).toString();
					}
				}
				else {
					VPPhraseSpec verb = nlgFactory.createVerbPhrase(predicate.getVerb().getAction());
					verb.setFeature(Feature.TENSE, Tense.PRESENT);
					verb.setFeature(Feature.NEGATED, true);
					action = realiser.realise(verb).toString();
				}
				
				if(directObjects.size() > 0) {
					Noun noun = directObjects.iterator().next();
					if (noun instanceof Location) {
						action += " to " + noun.getId();
					} else if (noun instanceof Character
							&& !noun.getIsCommon()) {
						action += " " + noun.getId();
					} else {
						action += " "
								+ SurfaceRealizer.determinerFixer(noun.getId());
					}
				}

				directive = directive.replace("<action>", action);

			}

			if (history.contains(directive)) {
				directive = null;
			}

		}

		if (predicates.isEmpty() && directive == null) {
			int randomCapableOfQuestion = Randomizer.random(1,
					this.causeEffectAlternative.length);
			directive = this.causeEffectAlternative[randomCapableOfQuestion
					- 1];
		}

		if (history.contains(directive)) {
			directive = null;
		}

		return directive;

	}

	public boolean checkAnswer(String input) {

		log.debug("answer in prompt: " + input);

		String temp = incompleteAnswer(input);

		Noun noun = asr.getNoun(currentId);

		answeredCorrect = false;

		if (currentPrompt instanceof GeneralPrompt) {
			//wrong answer
			if (!currentPrompt.checkAnswer(temp)) {

				//forever in general prompts, never add in restrictedGeneral because all specific answered
				if (!restrictedInSpecific.contains(currentId)) {
					// answered wrong, not yet completed q/a and not object or person
					if (!(noun instanceof Location
							|| noun instanceof Unknown)) {
						System.out.println(currentId);
						restrictedInGeneral.add(currentId);
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
			}
			else {
				((SpecificPrompt) currentPrompt).setIsWrongIgnored(true);
			}
		}

		return answeredCorrect;

	}
	
	public void ignored() {
		if(currentPrompt instanceof SpecificPrompt) {
			((SpecificPrompt) currentPrompt).setIsWrongIgnored(false);
		}
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
						+ listDependencies.get(0).dep().lemma().toLowerCase() + ".";
			}
		}

		return input;

	}

	private String findNounId() {

		StorySentence storySentence = asr.getCurrentStorySentence();
		List<String> nounId;

		for (int iterations = 0; iterations < 10; iterations++) {

			int randomNoun;
			String id;
			Noun noun;
			int threshold;

			nounId = storySentence.getAllNounsInStorySentence();

			while (!nounId.isEmpty()) {

				threshold = 0;
				randomNoun = Randomizer.random(1, nounId.size());
				id = nounId.remove(randomNoun - 1);
				noun = asr.getNoun(id);

				threshold += Utilities
						.countLists(noun.getAttributes().values());
				//threshold += Utilities.countLists(noun.getReferences().values());
				threshold += noun.getReferences().values().size();

				if (threshold < descriptionThreshold) {
					return id;
				}

			}

			nounId = new ArrayList<>(asr.getNounMap().keySet());
			nounId.removeAll(storySentence.getAllNounsInStorySentence());

			while (!nounId.isEmpty()) {

				threshold = 0;
				randomNoun = Randomizer.random(1, nounId.size());
				id = nounId.remove(randomNoun - 1);
				noun = asr.getNoun(id);

				threshold += Utilities
						.countLists(noun.getAttributes().values());
				//threshold += Utilities.countLists(noun.getReferences().values());
				threshold += noun.getReferences().values().size();

				if (threshold < descriptionThreshold) {
					return id;
				}

			}

			descriptionThreshold += 2;

		}

		return null;

	}

}
