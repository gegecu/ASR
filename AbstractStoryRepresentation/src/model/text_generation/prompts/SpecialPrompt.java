package model.text_generation.prompts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Preprocessing;
import model.utility.Randomizer;
import model.utility.SurfaceRealizer;
import model.utility.TypedDependencyComparator;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class SpecialPrompt {

	private static Logger log = Logger.getLogger(SpecialPrompt.class.getName());

	private String[] causeEffectDirective = {
			"Tell me more why <noun> <action>.",
			"Write more about why <noun> <action>.",
			"Write the reason why <noun> <action>."};

	private String[] causeEffectAlternative = {"Tell me more what happened."};

	private AbstractStoryRepresentation asr;
	private Queue<String> history;
	private NLGFactory nlgFactory;
	private Realiser realiser;
	private StanfordCoreNLP pipeline;
	private Preprocessing preprocess;
	private List<Noun> doers;
	private String currentPrompt;

	public SpecialPrompt(Queue<String> history, AbstractStoryRepresentation asr,
			NLGFactory nlgFactory, Realiser realiser) {
		this.asr = asr;
		this.nlgFactory = nlgFactory;
		this.realiser = realiser;
		this.history = history;
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.preprocess = new Preprocessing();
		this.doers = new ArrayList();
	}

	public String capableOf() {

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

			while (!directives.isEmpty() && directive == null) {

				int randomCapableOfQuestion = Randomizer.random(1,
						directives.size());
				directive = directives.remove(randomCapableOfQuestion - 1);

				List<Noun> doers = new ArrayList<>(
						predicate.getManyDoers().values());

				this.doers = doers;

				log.debug(doers.size());

				directive = directive.replace("<noun>",
						SurfaceRealizer.wordsConjunction(doers));

				String action = "";

				Collection<Noun> directObjects = predicate.getDirectObjects()
						.values();

				if (!predicate.isNegated()) {

					VPPhraseSpec verb = nlgFactory
							.createVerbPhrase(predicate.getVerb().getAction());

					if (directObjects.size() > 0) {
						verb.setFeature(Feature.TENSE, Tense.PAST);
						action = realiser.realise(verb).toString();

					} else {
						verb.setFeature(Feature.PROGRESSIVE, true);
						action = realiser.realise(verb).toString();
					}
				} else {
					VPPhraseSpec verb = nlgFactory
							.createVerbPhrase(predicate.getVerb().getAction());
					verb.setFeature(Feature.TENSE, Tense.PRESENT);
					verb.setFeature(Feature.NEGATED, true);
					action = realiser.realise(verb).toString();
				}

				if (directObjects.size() > 0) {
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

				if (history.contains(directive)) {
					directive = null;
				}

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

		currentPrompt = directive;

		return directive;

	}

	public boolean checkAnswer(String input) {
		int counter = 0;

		Set<String> currentDoerNames = new HashSet();

		for (Noun doer : doers) {
			currentDoerNames.add(doer.getId());
		}

		Map<String, String> coref;

		preprocess.preprocess(currentPrompt + " " + input);
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
