package model.text_generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.Randomizer;
import model.utility.SurfaceRealizer;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.phrasespec.VPPhraseSpec;

public class DirectivesGenerator extends TextGeneration {

	// <= 7 hasproperty, capableof, isA, hasA

	private String[] nounStartDirective = {"Describe <noun>.",
			"Tell me more about <noun>.", "Write more about <noun>.",
			"I want to hear more about <noun>.",
			"Tell something more about <noun>."};

	private String[] nounStartDirectiveAlternative = {
			"You have mentioned <noun> awhile ago, tell me more about <noun>"};

	private String[] causeEffectDirective = {
			"Tell me more why <noun> <action>.",
			"Write more about why <noun> <action>.",
			"Write the reason why <noun> <action>."};

	private String[] causeEffectAlternative = {"Tell me more what happened."};

	private int descriptionThreshold = 7;

	private Queue<String> history;

	public DirectivesGenerator(AbstractStoryRepresentation asr) {
		super(asr);
		history = new LinkedList<>();
	}

	@Override
	public String generateText() {

		List<String> response = new ArrayList<>();
		String output = null;

		String directiveNoun, directiveCapableOf;
		if (asr.getCurrentPartOfStory().equals("start")
				&& (directiveNoun = this.directiveNoun()) != null) {
			response.add(directiveNoun);
		} else if ((directiveCapableOf = this.capableOf()) != null) {
			response.add(directiveCapableOf);
		}

		if (!response.isEmpty()) {
			int random = Randomizer.random(1, response.size());
			output = response.get(random - 1);
			history.add(output);
		}

		if (history.size() > 3) {
			System.out.println(history.size());
			history.remove();
		}

		return output;

	}

	private String directiveNoun() {
		// nouns
		StorySentence storySentence = asr.getCurrentStorySentence();
		String directive = null;
		List<String> nounId, directives;
		int iterations = 0;

		while (iterations++ < 10 && (directive == null
				|| (directive != null && !history.contains(directive)))) {

			nounId = storySentence.getAllNounsInStorySentence();
			directives = new ArrayList<>(
					Arrays.asList(this.nounStartDirective));

			directive = findDirective(storySentence, nounId, directives,
					directive);

			if (history.contains(directive)) {
				directive = null;
			}

			if (directive == null && storySentence != null) {

				nounId = new ArrayList<>(asr.getNounMap().keySet());
				nounId.removeAll(storySentence.getAllNounsInStorySentence());
				directives = new ArrayList<>(
						Arrays.asList(this.nounStartDirectiveAlternative));

				directive = findDirective(storySentence, nounId, directives,
						directive);

				descriptionThreshold += 2;

			}

			if (history.contains(directive)) {
				directive = null;
			}

		}

		return directive;

	}

	private String findDirective(StorySentence storySentence,
			List<String> nounId, List<String> directives, String directive) {

		while (!nounId.isEmpty() && storySentence != null) {

			int threshold = 0;
			int randomNoun = Randomizer.random(1, nounId.size());
			Noun noun = asr.getNoun(nounId.remove(randomNoun - 1));

			threshold += Utilities.countLists(noun.getAttributes().values());
			threshold += Utilities.countLists(noun.getReferences().values());

			// find other noun if the number of properties of the current
			// noun is greater than descriptionThreshold
			if (threshold < descriptionThreshold) {
				while (!directives.isEmpty()
						&& (directive == null || history.contains(directive))) {
					int randomNounDirective = Randomizer.random(1,
							directives.size());
					directive = directives.remove(randomNounDirective - 1);
					directive = directive.replace("<noun>",
							(noun.getIsCommon() ? "the " : "") + noun.getId());
				}
			}

		}

		return directive;

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

				VPPhraseSpec verb = nlgFactory
						.createVerbPhrase(predicate.getAction());
				verb.setFeature(Feature.TENSE, Tense.PAST);
				String action = realiser.realise(verb).toString();

				Collection<Noun> directObjects = predicate.getDirectObjects()
						.values();
				if (predicate.getDirectObjects().size() > 0) {
					Noun noun = directObjects.iterator().next();
					if (noun instanceof Location) {
						action = "is in " + noun.getId();
						//action += " to " + noun.getId();
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

		return directive;

	}

}