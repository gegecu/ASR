package model.text_generation.prompts.special;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import model.knowledge_base.data.DataDAO;
import model.knowledge_base.template.TemplateDAO;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.utility.Randomizer;
import model.utility.SurfaceRealizer;
import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 * Prompt generator for special prompts
 */
public class SpecialPromptGenerator {

	private static Logger log = Logger
			.getLogger(SpecialPromptGenerator.class.getName());

	//used to improve grammar across different verb forms
	/**
	 * Template sentences used by the generator for generating causes special
	 * prompts.
	 */
	private String[] causeEffectDirectivePhraseFormat = TemplateDAO
			.getTemplates("causeEffectDirectivePhraseFormat");

	//	private String[] causeEffectAlternative = {
	//			"Tell me more about what happened.", "Tell me what happened next.",
	//			"Then what happened?"};
	//Why doesn't she have a ball? Why isn't she getting a ball? How can she get a ball?
	//
	/**
	 * Template sentences used by the generator for generating has property
	 * special prompts.
	 */
	private String[] hasPropertyMiddleDirective = TemplateDAO
			.getTemplates("hasPropertyMiddleDirective");
	/**
	 * Template sentences used by the generator for generating is a special
	 * prompts.
	 */
	private String[] isAMiddleDirective = TemplateDAO
			.getTemplates("isAMiddleDirective");
	/**
	 * Template sentences used by the generator for generating has a special
	 * prompts.
	 */
	private String[] hasAMiddleDirective = TemplateDAO
			.getTemplates("hasAMiddleDirective");
	/**
	 * Template sentences used by the generator for generating location special
	 * prompts.
	 */
	private String[] atLocationMiddleDirective = TemplateDAO
			.getTemplates("atLocationMiddleDirective");
	/**
	 * Template sentences used by the generator for generating not has property
	 * special prompts.
	 */
	private String[] notHasPropertyMiddleDirective = TemplateDAO
			.getTemplates("notHasPropertyMiddleDirective");
	/**
	 * Template sentences used by the generator for generating not is a special
	 * prompts.
	 */
	private String[] notIsAMiddleDirective = TemplateDAO
			.getTemplates("notIsAMiddleDirective");
	/**
	 * Template sentences used by the generator for generating not has a special
	 * prompts.
	 */
	private String[] notHasAMiddleDirective = TemplateDAO
			.getTemplates("notHasAMiddleDirective");
	/**
	 * Template sentences used by the generator for generating not at location
	 * special prompts.
	 */
	private String[] notAtLocationMiddleDirective = TemplateDAO
			.getTemplates("notAtLocationMiddleDirective");

	/**
	 * Template sentences used by the generator for generating verbs with
	 * location special prompts.
	 */
	private String[] locationVerbs = DataDAO.getData("locationVerbs");

	/**
	 * Used to store the previously generated prompts.
	 */
	private Queue<String> history;
	/**
	 * A tool used to construct sentences.
	 */
	private NLGFactory nlgFactory;
	/**
	 * A tool used to construct sentences.
	 */
	private Realiser realiser;
	/**
	 * The data that is used for generating prompts and answer checking
	 */
	private SpecialPromptData specialPromptData;

	/**
	 * @param specialPromptData
	 *            the specialPromptData to set
	 * @param nlgFactory
	 *            the nlgFactory to set
	 * @param realiser
	 *            the realiser to set
	 */
	public SpecialPromptGenerator(SpecialPromptData specialPromptData,
			NLGFactory nlgFactory, Realiser realiser) {
		this.nlgFactory = nlgFactory;
		this.realiser = realiser;
		this.history = specialPromptData.getHistory();
		this.specialPromptData = specialPromptData;
	}

	/**
	 * @param clause
	 *            the clause to use
	 * @return Generates prompts according to the rules for special prompt
	 *         generation.
	 */
	public String generateText(Clause clause) {

		//		StorySentence storySentence = asr.getCurrentStorySentence();
		//		List<Event> predicates = new ArrayList<>(
		//				storySentence.getManyPredicates().values());
		List<String> directives = new ArrayList<>(
				Arrays.asList(this.causeEffectDirectivePhraseFormat));//changed
		//
		String directive = null;
		//
		//		while (!predicates.isEmpty() && (directive == null
		//				|| (directive != null && history.contains(directive)))) {
		//
		//			int randomPredicate = Randomizer.random(1, predicates.size());
		//			Event predicate = predicates.remove(randomPredicate - 1);
		//
		if (clause instanceof Description)
			directive = generatePromptDescription((Description) clause);
		else
			directive = generatePromptEvent(directives, (Event) clause);
		//
		//			if (predicates.isEmpty() && directive == null) {
		//				return null;
		//			}
		//
		if (history.contains(directive)) {
			directive = null;
		}
		//
		specialPromptData.setCurrentPrompt(directive);
		//
		//		}

		return directive;

	}

	/**
	 * @param description
	 *            the description to use
	 * @return Generates prompts according to the rules for special prompt has
	 *         property generation.
	 */
	private String hasProperty(Description description) {
		String directive = null;
		List<String> properties = description.getAttribute("HasProperty");
		List<String> directives = new ArrayList<>(
				Arrays.asList(hasPropertyMiddleDirective));
		List<Noun> doers = new ArrayList<>(description.getManyDoers().values());
		while (properties != null && !properties.isEmpty()
				&& directive == null) {
			int randomProperty = Randomizer.random(1, properties.size());
			String property = properties.remove(randomProperty - 1);
			while (!directives.isEmpty() && directive == null) {
				int randomTemplate = Randomizer.random(1, directives.size());
				directive = directives.remove(randomTemplate - 1);
				directive = (doers.size() > 1)
						? directive.replace("<mainverb>", "are")
						: directive.replace("<mainverb>", "is");
				directive = directive.replace("<noun>",
						SurfaceRealizer.nounFixer(doers));
				directive = directive.replace("<property>", property);
				this.specialPromptData.setDoers(doers);
				if (history.contains(directive)) {
					directive = null;
				}
			}
		}
		return directive;

	}

	/**
	 * @param description
	 *            the description to use
	 * @return Generates prompts according to the rules for special prompt not
	 *         has property generation.
	 */
	private String notHasProperty(Description description) {
		String directive = null;
		List<String> properties = description.getAttribute("NotHasProperty");
		List<String> directives = new ArrayList<>(
				Arrays.asList(notHasPropertyMiddleDirective));
		List<Noun> doers = new ArrayList<>(description.getManyDoers().values());
		while (properties != null && !properties.isEmpty()
				&& directive == null) {
			int randomProperty = Randomizer.random(1, properties.size());
			String property = properties.remove(randomProperty - 1);
			while (!directives.isEmpty() && directive == null) {
				int randomTemplate = Randomizer.random(1, directives.size());
				directive = directives.remove(randomTemplate - 1);
				directive = (doers.size() > 1)
						? directive.replace("<mainverb>", "are")
						: directive.replace("<mainverb>", "is");
				directive = directive.replace("<noun>",
						SurfaceRealizer.nounFixer(doers));
				directive = directive.replace("<property>", property);
				this.specialPromptData.setDoers(doers);
				if (history.contains(directive)) {
					directive = null;
				}
			}
		}
		return directive;

	}

	/**
	 * @param description
	 *            the description to use
	 * @return Generates prompts according to the rules for special prompt is a
	 *         generation.
	 */
	private String isA(Description description) {
		String directive = null;
		if (description.getReference("IsA") == null) {
			return null;
		}
		List<Noun> roles = new ArrayList<>(
				description.getReference("IsA").values());
		List<String> directives = new ArrayList<>(
				Arrays.asList(isAMiddleDirective));
		List<Noun> doers = new ArrayList<>(description.getManyDoers().values());
		while (roles != null && !roles.isEmpty() && directive == null) {
			int randomRole = Randomizer.random(1, roles.size());
			Noun role = roles.remove(randomRole - 1);
			while (!directives.isEmpty() && directive == null) {
				int randomTemplate = Randomizer.random(1, directives.size());
				directive = directives.remove(randomTemplate - 1);
				directive = (doers.size() > 1)
						? directive.replace("<mainverb>", "are")
						: directive.replace("<mainverb>", "is");
				directive = directive.replace("<noun>",
						SurfaceRealizer.nounFixer(doers));
				directive = (doers.size() == 1)
						? directive.replace("<role>",
								SurfaceRealizer.determinerFixer(role.getId()))
						: directive.replace("<role>",
								SurfaceRealizer.pluralNoun(role));
				this.specialPromptData.setDoers(doers);
				if (history.contains(directive)) {
					directive = null;
				}
			}
		}
		return directive;
	}

	/**
	 * @param description
	 *            the description to use
	 * @return Generates prompts according to the rules for special prompt not
	 *         is a generation.
	 */
	private String notIsA(Description description) {
		String directive = null;
		if (description.getReference("NotIsA") == null) {
			return null;
		}
		List<Noun> roles = new ArrayList<>(
				description.getReference("NotIsA").values());
		List<String> directives = new ArrayList<>(
				Arrays.asList(notIsAMiddleDirective));
		List<Noun> doers = new ArrayList<>(description.getManyDoers().values());
		while (roles != null && !roles.isEmpty() && directive == null) {
			int randomRole = Randomizer.random(1, roles.size());
			Noun role = roles.remove(randomRole - 1);
			while (!directives.isEmpty() && directive == null) {
				int randomTemplate = Randomizer.random(1, directives.size());
				directive = directives.remove(randomTemplate - 1);
				directive = (doers.size() > 1)
						? directive.replace("<mainverb>", "are")
						: directive.replace("<mainverb>", "is");
				directive = directive.replace("<noun>",
						SurfaceRealizer.nounFixer(doers));
				directive = (doers.size() == 1)
						? directive.replace("<role>",
								SurfaceRealizer.determinerFixer(role.getId()))
						: directive.replace("<role>",
								SurfaceRealizer.pluralNoun(role));
				this.specialPromptData.setDoers(doers);
				if (history.contains(directive)) {
					directive = null;
				}
			}
		}
		return directive;
	}

	/**
	 * @param description
	 *            the description to use
	 * @return Generates prompts according to the rules for special prompt has a
	 *         generation.
	 */
	private String hasA(Description description) {
		String directive = null;
		if (description.getReference("HasA") == null) {
			return null;
		}
		List<Noun> objects = new ArrayList<>(
				description.getReference("HasA").values());
		List<String> directives = new ArrayList<>(
				Arrays.asList(hasAMiddleDirective));
		List<Noun> doers = new ArrayList<>(description.getManyDoers().values());
		while (objects != null && !objects.isEmpty() && directive == null) {
			int randomObject = Randomizer.random(1, objects.size());
			Noun object = objects.remove(randomObject - 1);
			while (!directives.isEmpty() && directive == null) {
				int randomTemplate = Randomizer.random(1, directives.size());
				directive = directives.remove(randomTemplate - 1);
				directive = (doers.size() > 1)
						? directive.replace("<mainverb>", "are")
						: directive.replace("<mainverb>", "is");
				directive = (doers.size() > 1)
						? directive.replace("<minorverb>", "have")
						: directive.replace("<minorverb>", "has");
				directive = directive.replace("<noun>",
						SurfaceRealizer.nounFixer(doers));
				directive = directive.replace("<object>",
						SurfaceRealizer.determinerFixer(object.getId()));
				this.specialPromptData.setDoers(doers);

				if (history.contains(directive)) {
					directive = null;
				}
			}
		}
		return directive;
	}

	/**
	 * @param description
	 *            the description to use
	 * @return Generates prompts according to the rules for special prompt not
	 *         has a generation.
	 */
	private String notHasA(Description description) {
		String directive = null;
		if (description.getReference("NotHasA") == null) {
			return null;
		}
		List<Noun> objects = new ArrayList<>(
				description.getReference("NotHasA").values());
		List<String> directives = new ArrayList<>(
				Arrays.asList(notHasAMiddleDirective));
		List<Noun> doers = new ArrayList<>(description.getManyDoers().values());
		while (objects != null && !objects.isEmpty() && directive == null) {
			int randomObject = Randomizer.random(1, objects.size());
			Noun object = objects.remove(randomObject - 1);
			while (!directives.isEmpty() && directive == null) {
				int randomTemplate = Randomizer.random(1, directives.size());
				directive = directives.remove(randomTemplate - 1);
				directive = (doers.size() > 1)
						? directive.replace("<mainverb>", "are")
						: directive.replace("<mainverb>", "is");
				directive = (doers.size() > 1)
						? directive.replace("<minorverb>", "have")
						: directive.replace("<minorverb>", "has");
				directive = directive.replace("<noun>",
						SurfaceRealizer.nounFixer(doers));
				directive = directive.replace("<object>",
						SurfaceRealizer.determinerFixer(object.getId()));
				this.specialPromptData.setDoers(doers);

				if (history.contains(directive)) {
					directive = null;
				}
			}
		}
		return directive;
	}

	/**
	 * @param description
	 *            the description to use
	 * @return Generates prompts according to the rules for special prompt at
	 *         location generation.
	 */
	private String atLocation(Description description) {
		String directive = null;
		if (description.getReference("AtLocation") == null) {
			return null;
		}
		List<Noun> locations = new ArrayList<>(
				description.getReference("AtLocation").values());
		List<String> directives = new ArrayList<>(
				Arrays.asList(atLocationMiddleDirective));
		List<Noun> doers = new ArrayList<>(description.getManyDoers().values());
		while (locations != null && !locations.isEmpty() && directive == null) {
			int randomLocation = Randomizer.random(1, locations.size());
			Noun location = locations.remove(randomLocation - 1);
			while (!directives.isEmpty() && directive == null) {
				int randomTemplate = Randomizer.random(1, directives.size());
				directive = directives.remove(randomTemplate - 1);
				directive = (doers.size() > 1)
						? directive.replace("<mainverb>", "are")
						: directive.replace("<mainverb>", "is");
				directive = directive.replace("<noun>",
						SurfaceRealizer.nounFixer(doers));
				directive = (location.getIsCommon())
						? directive.replace("<location>", SurfaceRealizer
								.determinerFixer(location.getId()))
						: directive.replace("<location>", location.getId());
				this.specialPromptData.setDoers(doers);

				if (history.contains(directive)) {
					directive = null;
				}
			}
		}
		return directive;
	}

	/**
	 * @param description
	 *            the description to use
	 * @return Generates prompts according to the rules for special prompt not
	 *         at location generation.
	 */
	private String notAtLocation(Description description) {
		String directive = null;
		if (description.getReference("NotAtLocation") == null) {
			return null;
		}
		List<Noun> locations = new ArrayList<>(
				description.getReference("NotAtLocation").values());
		List<String> directives = new ArrayList<>(
				Arrays.asList(notAtLocationMiddleDirective));
		List<Noun> doers = new ArrayList<>(description.getManyDoers().values());
		while (locations != null && !locations.isEmpty() && directive == null) {
			int randomLocation = Randomizer.random(1, locations.size());
			Noun location = locations.remove(randomLocation - 1);
			while (!directives.isEmpty() && directive == null) {
				int randomTemplate = Randomizer.random(1, directives.size());
				directive = directives.remove(randomTemplate - 1);
				directive = (doers.size() > 1)
						? directive.replace("<mainverb>", "are")
						: directive.replace("<mainverb>", "is");
				directive = directive.replace("<noun>",
						SurfaceRealizer.nounFixer(doers));
				directive = (location.getIsCommon())
						? directive.replace("<location>", SurfaceRealizer
								.determinerFixer(location.getId()))
						: directive.replace("<location>", location.getId());
				this.specialPromptData.setDoers(doers);

				if (history.contains(directive)) {
					directive = null;
				}
			}
		}
		return directive;
	}

	/**
	 * 
	 * 
	 * @param description
	 *            the description to use
	 * @return Generates prompts according to the rules for special prompt
	 *         generation.
	 */
	private String generatePromptDescription(Description description) {
		List<String> directives = new ArrayList<>();

		String hasProperty = this.hasProperty(description);
		if (hasProperty != null) {
			directives.add(hasProperty);
		}

		String notHasProperty = this.notHasProperty(description);
		if (notHasProperty != null) {
			directives.add(notHasProperty);
		}

		String isA = this.isA(description);
		if (isA != null) {
			directives.add(isA);
		}

		String notIsA = this.notIsA(description);
		if (notIsA != null) {
			directives.add(notIsA);
		}

		String hasA = this.hasA(description);
		if (hasA != null) {
			directives.add(hasA);
		}

		String notHasA = this.notHasA(description);
		if (notHasA != null) {
			directives.add(notHasA);
		}

		String atLocation = this.atLocation(description);
		if (atLocation != null) {
			directives.add(atLocation);
		}

		String notAtLocation = this.notAtLocation(description);
		if (notAtLocation != null) {
			directives.add(notAtLocation);
		}

		if (!directives.isEmpty()) {
			int randomDirective = Randomizer.random(1, directives.size());
			return directives.get(randomDirective - 1);
		}

		return null;

	}

	/**
	 * @param directives
	 *            the directives to use
	 * @param event
	 *            the event to use
	 * @return Generates prompts according to the rules for special prompt
	 *         generation.
	 */
	private String generatePromptEvent(List<String> directives, Event event) {

		String directive = null;
		ArrayList<String> complements = event.getVerb().getClausalComplements();
		ArrayList<String> adverbs = event.getVerb().getAdverbs();
		ArrayList<String> prepositionals = event.getVerb()
				.getPrepositionalPhrases();

		//put directobjects as strings
		Collection<Noun> dobjs = event.getDirectObjects().values();
		ArrayList<String> directobjects = new ArrayList<String>();
		if (!dobjs.isEmpty()) {
			for (Noun noun : dobjs) {
				if (noun.getType() == TypeOfNoun.CHARACTER
						&& !noun.getIsCommon()) {
					directobjects.add(noun.getId());
				} else {
					//add if condition(if not a gerund)
					directobjects.add("the " + noun.getId());//'the' generally works best
				}
			}
		}

		ArrayList<String> details = new ArrayList<>();
		details.addAll(adverbs);
		details.addAll(prepositionals);
		for (String s : details) {
			System.out.print(s + ",");
		}
		System.out.println("***");
		ArrayList<String> verbObjects = new ArrayList<>();
		verbObjects.addAll(complements);
		verbObjects.addAll(directobjects);
		for (String s : verbObjects) {
			System.out.print(s + ",");
		}

		List<Noun> doers = new ArrayList<>(event.getManyDoers().values());

		specialPromptData.setDoers(doers);

		log.debug(doers.size());

		//			directive = directive.replace("<noun>",
		//					SurfaceRealizer.wordsConjunction(doers)); //changed to p.setsubj

		SPhraseSpec p = nlgFactory.createClause();
		p.setSubject(SurfaceRealizer.nounFixer(doers));

		VPPhraseSpec verb = nlgFactory
				.createVerbPhrase(event.getVerb().getAction());

		//determine tense/form
		String tense = event.getVerb().getTense();
		if (event.getVerb().isProgressive()) {
			p.setFeature(Feature.PROGRESSIVE, true);
		}

		p.setFeature(Feature.TENSE, Tense.PAST);

		if (tense.equals("present")) {
			p.setFeature(Feature.TENSE, Tense.PRESENT);
		} else if (tense.equals("future")) {
			p.setFeature(Feature.TENSE, Tense.FUTURE);
			p.setFeature(Feature.PROGRESSIVE, false);//future tense progressive sounds confusing
		}
		if (event.isNegated()) {
			verb.setFeature(Feature.NEGATED, true);
		}

		//add details
		int random = 0;
		//adverbs and prepositionals combined into a single list
		if (!details.isEmpty()) {
			random = Randomizer.random(1, details.size());
			verb.addModifier(details.remove(random - 1));
		}

		//if something is found to function as direct object
		if (!verbObjects.isEmpty()) {

			//			if (!complements.isEmpty()) {//show complement if exists
			//				random = Randomizer.random(1, complements.size());
			//				verb.addComplement(complements.get(random - 1));
			//			}
			random = Randomizer.random(1, verbObjects.size());
			p.setObject(verbObjects.get(random - 1));

			p.setVerb(verb);
			String action = "";
			while (!directives.isEmpty() && directive == null) {
				int randomCapableOfQuestion = Randomizer.random(1,
						directives.size());
				directive = directives.remove(randomCapableOfQuestion - 1);
				action = realiser.realise(p).toString();
				directive = directive.replace("<phrase>", action);
				if (history.contains(directive)) {
					directive = null;
				}
			}
		} else {
			p.setVerb(verb);
			if (Arrays.asList(locationVerbs)
					.contains(event.getVerb().getAction())) {
				p.setFeature(Feature.INTERROGATIVE_TYPE,
						InterrogativeType.WHERE);
			} else {
				p.setFeature(Feature.INTERROGATIVE_TYPE,
						InterrogativeType.WHAT_OBJECT);
			}
			directive = realiser.realiseSentence(p);
			if (history.contains(directive)) {
				directive = null;
			}
		}

		return directive;

	}

}
