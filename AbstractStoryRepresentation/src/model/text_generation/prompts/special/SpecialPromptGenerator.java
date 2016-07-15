package model.text_generation.prompts.special;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.Randomizer;
import model.utility.SurfaceRealizer;
import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class SpecialPromptGenerator {

	private static Logger log = Logger
			.getLogger(SpecialPromptGenerator.class.getName());

	//fixed some grammar issues

	//strictly noun + action format
	private String[] causeEffectDirective = {"Tell me why <noun> <action>.",
			"Explain why <noun> <action>.",
			"Write more about why <noun> <action>.",
			"Write the reason why <noun> <action>."};

	//used to improve grammar across different verb forms
	private String[] causeEffectDirectivePhraseFormat = {
			"Tell me why <phrase>.", "Explain why <phrase>.",
			"Write more about why <phrase>.", "Write the reason why <phrase>."};

//	private String[] causeEffectAlternative = {
//			"Tell me more about what happened.", "Tell me what happened next.",
//			"Then what happened?"};

	private String[] locationVerbs = {"go", "climb", "run", "walk", "swim",
			"travel"};

	private AbstractStoryRepresentation asr;
	private Queue<String> history;
	private NLGFactory nlgFactory;
	private Realiser realiser;
	private SpecialPromptData specialPromptData;

	public SpecialPromptGenerator(SpecialPromptData specialPromptData,
			NLGFactory nlgFactory, Realiser realiser) {
		this.nlgFactory = nlgFactory;
		this.realiser = realiser;
		this.asr = specialPromptData.getASR();
		this.history = specialPromptData.getHistory();
		this.specialPromptData = specialPromptData;
	}

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
			if(clause instanceof Description)
				directive = generatePromptDescription((Description)clause);
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
	
	private String generatePromptDescription(Description description) {
		String directive = null;
		String before = "";
		String after = "";
		
		List<String> relations = new ArrayList();
		
		relations.addAll(description.getAttributes().keySet());
		relations.addAll(description.getReferences().keySet());
		
		while(!relations.isEmpty() && directive == null) {
			
			System.out.println("in");
			
			int randomRelations = Randomizer.random(1, relations.size());
			
			String relation = relations.remove(randomRelations - 1);
			
			String doers = SurfaceRealizer.wordsConjunction(new ArrayList(description.getManyDoers().values()));
			boolean plural = false;
			
			if(description.getManyDoers().size() > 1) {
				plural = true;
			}
			
			before = "Why ";
			
			if(!relation.contains("HasA")) {
				if(plural) {
					before += "are ";
				}
				else {
					before += "is ";
				}
			}
			else {
				if(plural) {
					before += "do ";
				}
				else {
					before += "does ";
				}
				
				if(relation.contains("Not")) {
					before += "not ";
				}
			}
			
			before += doers + " ";
			
			if(relation.contains("IsA") || relation.contains("AtLocation") || relation.contains("HasA")) {
				
				Map<String, Noun> mapNouns = description.getReference(relation);
				List<Noun> nouns = new ArrayList(mapNouns.values());
				
				while(!nouns.isEmpty() && directive == null) {
					int randomNoun = Randomizer.random(1, nouns.size());
					Noun noun = nouns.remove(randomNoun - 1);
					if(relation.equals("IsA")) {
						after = SurfaceRealizer.determinerFixer(noun.getId());
					}
					else if(relation.equals("NotIsA")) {
						after = "not " + SurfaceRealizer.determinerFixer(noun.getId());
					}
					else if(relation.equals("AtLocation")) {
						after = "in ";
						if(noun.getIsCommon()) {
							after += "the ";
						}
						
						after += noun.getId();	
					}
					else if(relation.equals("NotHasA") || relation.equals("HasA")) {
						//implement soon
						after += "have " + SurfaceRealizer.determinerFixer(noun.getId());
						
					}
					
					directive = before + after + "?";
					
					if(history.contains(directive)) {
						directive = null;
						continue;
					}
				}

			}
			else if (relation.contains("HasProperty")) {
				List<String> adjectives = description.getAttribute(relation);
				while(!adjectives.isEmpty() && directive == null) {
					int randomAdj = Randomizer.random(1, adjectives.size());
					if(relation.equals("NotHasProperty")) {
						after += "not " + adjectives.remove(randomAdj-1);
					}
					else {
						after += adjectives.remove(randomAdj-1);
					}
					
					directive = before + after + "?";
					if(history.contains(directive)) {
						directive = null;
						continue;
					}
				}
			}
			
			if(history.contains(directive)) {
				directive = null;
			}
			
			System.out.println(directive);
			
		}
		return directive;
	}
	

	private String generatePromptEvent(List<String> directives, Event predicate) {

		String directive = null;
		ArrayList<String> complements = predicate.getVerb()
				.getClausalComplements();
		ArrayList<String> adverbs = predicate.getVerb().getAdverbs();
		ArrayList<String> prepositionals = predicate.getVerb()
				.getPrepositionalPhrases();
		ArrayList<String> details = new ArrayList<>();
		//details.addAll(complements);
		details.addAll(adverbs);
		details.addAll(prepositionals);

		List<Noun> doers = new ArrayList<>(predicate.getManyDoers().values());

		specialPromptData.setDoers(doers);

		log.debug(doers.size());

		//			directive = directive.replace("<noun>",
		//					SurfaceRealizer.wordsConjunction(doers)); //changed to p.setsubj

		Collection<Noun> directObjects = predicate.getDirectObjects().values();
		SPhraseSpec p = nlgFactory.createClause();
		p.setSubject(SurfaceRealizer.wordsConjunction(doers));

		VPPhraseSpec verb = nlgFactory
				.createVerbPhrase(predicate.getVerb().getAction());

		//determine tense/form
		String tense = predicate.getVerb().getTense();
		if (predicate.getVerb().isProgressive()) {
			p.setFeature(Feature.PROGRESSIVE, true);
		}

		p.setFeature(Feature.TENSE, Tense.PAST);

		if (tense.equals("present")) {
			p.setFeature(Feature.TENSE, Tense.PRESENT);
		} else if (tense.equals("future")) {
			p.setFeature(Feature.TENSE, Tense.FUTURE);
			p.setFeature(Feature.PROGRESSIVE, false);//future tense progressive sounds confusing
		}
		if (predicate.isNegated()) {
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
		if (!directObjects.isEmpty()) {
			Noun noun = directObjects.iterator().next();
			if (noun.getType() == TypeOfNoun.CHARACTER && !noun.getIsCommon()) {
				p.setObject(noun.getId());
			} else {
				p.setObject("the " + noun.getId());//'the' generally works best
			}

			if (!complements.isEmpty()) {//show complement if exists
				random = Randomizer.random(1, complements.size());
				verb.addComplement(complements.get(random - 1));
			}
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
					.contains(predicate.getVerb().getAction())) {
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
