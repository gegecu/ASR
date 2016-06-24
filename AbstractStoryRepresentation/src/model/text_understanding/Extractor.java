package model.text_understanding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.instance.AbstractSequenceClassifierInstance;
import model.instance.DictionariesInstance;
import model.instance.SenticNetParserInstance;
import model.instance.StanfordCoreNLPInstance;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.senticnet.ConceptParser;
import model.knowledge_base.senticnet.SenticNetParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Object;
import model.story_representation.story_element.noun.Unknown;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.PartOfSpeechComparator;
import model.utility.TypedDependencyComparator;

@SuppressWarnings("rawtypes")
public class Extractor {

	private static Logger log = Logger.getLogger(Extractor.class.getName());

	private StanfordCoreNLP pipeline;
	private AbstractSequenceClassifier classifier;
	private ConceptParser cp;
	private SenticNetParser snp;
	private Dictionaries dictionary;
	private AbstractStoryRepresentation asr;

	private static String[] SRL_ENTITY_LIST = {"person", "place", "object"};
	private static String[] NER_ENTITY_LIST = {"person", "location", "date",
			"organization", "time", "money", "percentage"};
	private static Pattern[] NER_ENTITY_LIST_COMPILED = new Pattern[NER_ENTITY_LIST.length];

	static {
		for (int i = 0, j = NER_ENTITY_LIST.length; i < j; i++) {
			String entity = NER_ENTITY_LIST[i] = NER_ENTITY_LIST[i]
					.toUpperCase();
			NER_ENTITY_LIST_COMPILED[i] = Pattern.compile("([a-zA-Z0-9.%]+(/"
					+ entity + ")[ ]*)*[a-zA-Z0-9.%]+(/" + entity + ")");
		}
	}

	public Extractor(AbstractStoryRepresentation asr) {
		this.asr = asr;
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.classifier = AbstractSequenceClassifierInstance.getInstance();
		this.cp = new ConceptParser();
		this.snp = SenticNetParserInstance.getInstance();
		this.dictionary = DictionariesInstance.getInstance();
	}

	public List<StorySentence> extract(String text,
			Map<String, String> coreference) {

		List<StorySentence> extractedStorySentences = new ArrayList<>();

		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		int prevSentenceCount = 0;

		for (List<StorySentence> storySentences : asr.getStorySentencesMap()
				.values()) {
			prevSentenceCount += storySentences.size();
		}

		int i = 0;
		for (CoreMap sentence : sentences) {

			if (i++ < prevSentenceCount)
				continue;

			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);

			log.debug(sentence.toString());

			StorySentence storySentence = new StorySentence();
			List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
					dependencies.typedDependencies());
			Collections.sort(listDependencies, new TypedDependencyComparator());
			Collections.sort(listDependencies, new PartOfSpeechComparator());

			for (TypedDependency td : listDependencies) {
				extractDependency(coreference, td, storySentence);//extract based on dependency
			}

			for (Event event : storySentence.getManyPredicates().values()) {
				event.setPolarity(getPolarityOfEvent(event));
			}

			for (Description description : storySentence.getManyDescriptions()
					.values()) {
				description.setPolarity(getPolarityOfEvent(description));
			}

			// asr.addEvent(storySentence);
			extractedStorySentences.add(storySentence);

		}

		return extractedStorySentences;

	}

	private void extractDependency(Map<String, String> coreference,
			TypedDependency td, StorySentence storySentence) {

		try {

			String tdDepTag = td.dep().tag();
			String tdGovTag = td.gov().tag();
			String tdReln = td.reln().toString();
			String tdDepId = (td.dep().sentIndex() + 1) + " "
					+ td.dep().index();
			String tdGovId = (td.gov().sentIndex() + 1) + " "
					+ td.gov().index();

			//log.debug(td.dep().lemma() + " before : " + tdDepId);
			//log.debug(td.gov().lemma() + "before : " + tdGovId);

			if (coreference.get(tdDepId) != null) {
				tdDepId = coreference.get(tdDepId);
				log.debug("after : " + tdDepId);
			}

			if (coreference.get(tdGovId) != null) {
				tdGovId = coreference.get(tdGovId);
				log.debug("after : " + tdGovId);
			}

			log.debug("extracting:..." + td.dep().originalText() + ":"
					+ tdDepTag + ", " + td.gov().originalText() + ":" + tdGovTag
					+ ",  " + tdReln);

			/** get compound words ? **/
			if (tdReln.equals("compound")) {
				extractCompoundDependency(td, tdGovId);
				//log.debug("compound: " + asr.getNoun(td.gov().lemma()).getId());
			}
			/** get doer or subject **/
			else if (tdReln.contains("nsubj")) {
				extractDoerDependency(td, storySentence, tdDepId, tdGovId);
			}
			/** get auxpass "HasProperty" (to create get + verb concepts) **/
			else if (tdReln.equals("auxpass")) {
				extractAuxpassPropertyDependency(td, storySentence, tdGovId);
			}
			/** get indirect object or receiver **/
			else if (tdReln.equals("iobj")) {
				extractReceiverDependency(td, storySentence, tdDepId, tdGovId);
			}
			/** get direct object **/
			else if (tdReln.equals("dobj") || tdReln.equals("nmod:for")
					|| tdReln.equals("nmod:agent")) {
				extractDirectObjectDependency(td, storySentence, tdDepId,
						tdGovId);
			}
			/** extract xcomp "HasProperty" **/
			else if (tdReln.equals("xcomp")
					&& dictionary.copulas.contains(td.gov().lemma())) {
				extractXcompPropertyDependency(td, storySentence, tdDepId,
						tdGovId);
			}
			/** extract amod "IsA" ('adjective noun' format) **/
			else if (tdReln.equals("amod")) {
				extractAmodPropertyDependency(td, storySentence, tdDepId,
						tdGovId);
			}
			/** extract advmod "HasProperty" **/
			else if (tdReln.equals("advmod")) {
				extractAdvmodPropertyDependency(td, storySentence, tdDepId,
						tdGovId);
			}
			/** extract location **/
			else if (tdReln.equals("nmod:at") || tdReln.equals("nmod:near")
					|| tdReln.equals("nmod:to") || tdReln.equals("nmod:in")
					|| tdReln.equals("nmod:on")) {
				extractLocationDependency(td, storySentence, tdDepId, tdGovId);
			}
			/** extract possession **/
			else if (tdReln.equals("nmod:poss")) {
				extractPossesionDependency(td, storySentence, tdDepId, tdGovId);
			}

			/** extract negation **/
			else if (tdReln.equals("neg")) {
				extractNegation(td, storySentence, tdDepId, tdGovId);
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	private void extractNegation(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {
		//neg ( give-4 , not-3 )  gov, dep
		//neg ( doctor-5 , not-3 ) 
		// neg ( cute-4 , not-3 ) 

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();
		String tdReln = td.reln().toString();

		if (tdGovTag.contains("VB")) {
			Event p = storySentence.getPredicate(tdGovId);
			p.getConcepts().clear();
			p.getVerb().setNegated(true);

			if (!p.getDirectObjects().isEmpty()) {
				for (Noun dobj : p.getDirectObjects().values()) {
					p.addConcept(cp.createNegationVerbWithDirectObject(
							tdGovLemma, dobj.getId()));
				}
			} else {
				p.addConcept(cp.createNegationVerb(tdGovLemma));
			}
		}

		else if (tdGovTag.contains("NN")) {
			Description d = storySentence.getDescription(tdGovId);

			d.getConcepts().clear();

			d.addReference("NotIsA", tdGovId,
					d.getReference("IsA").remove(tdGovId));

			if (d.getReference("IsA").isEmpty()) {
				d.getReferences().remove("IsA");
			}

			for (Noun noun : d.getManyDoers().values()) {
				noun.addReference("NotIsA", tdGovId,
						noun.getReference("IsA").remove(tdGovId));

				if (noun.getReference("IsA").isEmpty()) {
					noun.getReferences().remove("IsA");
				}
			}

			d.addConcept(cp.createConceptAsRoleNegation(tdGovLemma));
		}

		else if (tdGovTag.contains("JJ")) {
			Description d = storySentence.getDescription(tdGovId);
			d.getAttribute("HasProperty").remove(tdGovLemma);
			d.addAttribute("NotHasProperty", tdGovLemma);

			d.getConcepts().clear();

			if (d.getAttribute("HasProperty").isEmpty()) {
				d.getAttributes().remove("HasProperty");
			}

			for (Noun noun : d.getManyDoers().values()) {
				noun.getAttribute("HasProperty").remove(tdGovLemma);
				noun.addAttribute("NotHasProperty", tdGovLemma);

				if (noun.getAttribute("HasProperty").isEmpty()) {
					noun.getAttributes().remove("IsA");
				}
			}

			d.addConcept(cp.createConceptAsAdjectiveNegated(tdGovLemma));
			d.addConcept(
					cp.createConceptAsPredicativeAdjectiveNegated(tdGovLemma));
		}
	}

	private void extractPossesionDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {
		//nmod:poss ( ball-5 , John-3 )  gov, dep
		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();
		String tdReln = td.reln().toString();

		Noun noun = asr.getNoun(tdDepId);

		if (noun == null) {

			if (tdDepTag.equals("NNP")) {
				noun = extractCategory(getNER(tdDepLemma), tdDepLemma);
				noun.setProper();
			} else if (tdDepTag.contains("NN")) {
				noun = extractCategory(getSRL(tdDepLemma), tdDepLemma);
			}

			asr.addNoun(tdDepId, noun);
		}

		Noun noun2 = asr.getNoun(tdGovId);

		if (noun2 == null) {

			if (tdGovTag.equals("NNP")) {
				noun2 = extractCategory(getNER(tdGovLemma), tdGovLemma);
				noun2.setProper();
			} else if (tdGovTag.contains("NN")) {
				noun2 = extractCategory(getSRL(tdGovLemma), tdGovLemma);
			}

			asr.addNoun(tdGovId, noun2);
		}

		noun.addReference("HasA", tdDepId, noun2);
		noun2.addReference("IsOwnedBy", tdGovId, noun);
	}

	private void extractLocationDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();
		String tdReln = td.reln().toString();

		Noun noun = asr.getNoun(tdDepId);

		if (noun == null) {

			if (tdDepTag.equals("NNP")) {
				noun = extractCategory(getNER(tdDepLemma), tdDepLemma);
				noun.setProper();
			} else if (tdDepTag.contains("NN")) {
				noun = extractCategory(getSRL(tdDepLemma), tdDepLemma);
			}

			asr.addNoun(tdDepId, noun);

		}

		if (noun != null && noun instanceof Location) {

			Event predicate = storySentence.getPredicate(tdGovId);

			if (!predicate.getVerb().isNegated() && predicate != null) {

				Description description = storySentence.getDescription(tdDepId);

				if (description == null) {
					description = new Description();
				}

				for (Map.Entry<String, Noun> entry : storySentence
						.getPredicate(tdGovId).getManyDoers().entrySet()) {
					entry.getValue().addReference("AtLocation", tdDepId, noun);
					description.addReference("AtLocation", tdDepId, noun);
					description.addDoer(entry.getKey(), entry.getValue());
				}

				predicate.addDirectObject(tdDepId, noun);
				log.debug("Location: " + tdDepLemma);

				predicate.addConcept(
						cp.createConceptAsInfinitive(tdGovLemma, tdDepLemma));
			}
		}

		//		if (tdReln.equals("nmod:to") && tdGovTag.contains("VB")) {
		//			//create concept
		//			Event predicate = storySentence.getPredicate(tdGovId);
		//			if (predicate == null) {
		//				predicate = new Event(tdGovLemma);
		//				storySentence.addPredicate(tdGovId, predicate);
		//			}
		//		}

	}

	private void extractAdvmodPropertyDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovLemma = td.gov().lemma();

		if (tdDepTag.equals("RB")) {
			if (dictionary.copulas.contains(tdGovLemma)) {

				Description description = storySentence.getDescription(tdDepId);

				if (description == null) {
					description = new Description();
				}

				for (Map.Entry<String, Noun> entry : storySentence
						.getPredicate(tdGovId).getManyDoers().entrySet()) {
					entry.getValue().addAttribute("HasProperty", tdDepLemma);
					description.addDoer(entry.getKey(), entry.getValue());
				}

				description.addAttribute("HasProperty", tdDepLemma);
				description.addConcept(cp.createConceptAsAdjective(tdDepLemma));
				storySentence.getManyPredicates().remove(tdGovId);

			}
		}

	}

	private void extractAmodPropertyDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {

		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();
		Noun noun = asr.getNoun(tdGovId);

		if (noun == null) {

			if (tdGovTag.equals("NNP")) {
				noun = extractCategory(getNER(tdGovLemma), tdGovLemma);
				noun.setProper();
			} else if (tdGovTag.contains("NN")) {
				noun = extractCategory(getSRL(tdGovLemma), tdGovLemma);
			}

			asr.addNoun(tdGovId, noun);

		}

		noun.addAttribute("HasProperty", tdDepLemma);
		//log.debug("amod " + noun.getId());

		Description description = storySentence.getDescription(tdDepId);

		if (description == null) {
			description = new Description();
		}

		description.addAttribute("HasProperty", tdDepLemma);
		description.addConcept(cp.createConceptAsAdjective(tdDepLemma));
		description
				.addConcept(cp.createConceptAsPredicativeAdjective(tdDepLemma));

	}

	private void extractXcompPropertyDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();

		Description description = storySentence.getDescription(tdDepId);

		if (description == null) {
			description = new Description();
		}

		for (Map.Entry<String, Noun> entry : storySentence.getPredicate(tdGovId)
				.getManyDoers().entrySet()) {

			if (tdDepTag.equals("JJ")) {

				entry.getValue().addAttribute("HasProperty", tdDepLemma);
				description.addDoer(entry.getKey(), entry.getValue());
				description.addAttribute("HasProperty", tdDepLemma);
				description.addConcept(cp.createConceptAsAdjective(tdDepLemma));
				description.addConcept(
						cp.createConceptAsPredicativeAdjective(tdDepLemma));

			} else if (tdDepTag.contains("NN")) {
				// n.addAttribute("IsA", td.dep().lemma());
				Noun noun2 = asr.getNoun(tdDepId);

				if (noun2 == null) {
					if (tdDepTag.equals("NNP")) {
						noun2 = extractCategory(getNER(tdDepLemma), tdDepLemma);
						noun2.setProper();
					} else if (tdDepTag.contains("NN")) {
						noun2 = extractCategory(getSRL(tdDepLemma), tdDepLemma);
					}

					asr.addNoun(tdDepId, noun2);

				}

				if (noun2 != null) {
					entry.getValue().addReference("IsA", tdDepId, noun2);
					description.addReference("IsA", tdDepId, noun2);
					description.addConcept(cp.createConceptAsRole(tdDepLemma));
				}

			}

		}

		storySentence.getManyPredicates().remove(tdGovId);

	}

	private void extractDirectObjectDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovLemma = td.gov().lemma();

		Noun noun = asr.getNoun(tdDepId);

		if (noun == null) {

			if (tdDepTag.equals("NNP")) {
				noun = extractCategory(getNER(tdDepLemma), tdDepLemma);
				noun.setProper();
			} else if (tdDepTag.contains("NN")) {
				noun = extractCategory(getSRL(tdDepLemma), tdDepLemma);
			}

			asr.addNoun(tdDepId, noun);

		}

		Event event = storySentence.getPredicate(tdGovId);

		if (event == null) {
			event = new Event(tdGovLemma);
		}

		if (noun != null) {

			if (tdGovLemma.equals("has") || tdGovLemma.equals("have")) {

				Description description = storySentence.getDescription(tdDepId);

				if (description == null) {
					description = new Description();
				}

				for (Map.Entry<String, Noun> entry : storySentence
						.getPredicate(tdGovId).getManyDoers().entrySet()) {

					if (noun != null) {

						entry.getValue().addReference("HasA", tdDepId, noun);
						description.addDoer(entry.getKey(), entry.getValue());
						description.addReference("HasA", tdDepId, noun);

						noun.addReference("IsOwnedBy", entry.getKey(),
								entry.getValue());
						//log.debug(entry.getKey() + ", " + entry.getValue().getId());

						description.addConcept(cp.createConceptWithDirectObject(
								tdGovLemma, tdDepLemma));
						description.addConcept(tdDepLemma);

					}

				}

				storySentence.addDescription(tdDepId, description);
				storySentence.getManyPredicates().remove(tdGovId);

			} else {

				//create concept
				event.addDirectObject(tdDepId, noun);
				storySentence.addPredicate(tdGovId, event);

				if (noun instanceof Character) //if direct object is a person, change to someone
					event.addConcept(cp.createConceptWithDirectObject(
							tdGovLemma, "someone"));
				else {
					event.addConcept(cp.createConceptWithDirectObject(
							tdGovLemma, tdDepLemma));
					event.addConcept(tdDepLemma);
				}

				//						log.debug("dobj: "
				//								+ storySentence.getPredicate(tdGovId).getDirectObject(tdDepId).getId());
			}

		}

	}

	private void extractReceiverDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();

		if (tdDepTag.contains("NN") && tdGovTag.contains("VB")) {

			Noun noun = asr.getNoun(tdDepId);

			if (noun == null) {

				if (tdDepTag.equals("NNP")) {
					noun = extractCategory(getNER(tdDepLemma), tdDepLemma);
					noun.setProper();
				} else if (tdDepTag.contains("NN")) {
					noun = extractCategory(getSRL(tdDepLemma), tdDepLemma);
				}

				asr.addNoun(tdDepId, noun);

			}

			Event event = storySentence.getPredicate(tdGovId);

			if (event == null) {
				event = new Event(td.gov().lemma());
			}

			event.addReceiver(tdDepId, noun);
			storySentence.addPredicate(tdGovId, event);

			//log.debug("iobj: " + asr.getNoun(td.dep().lemma()).getId());
		}

	}

	private void extractAuxpassPropertyDependency(TypedDependency td,
			StorySentence storySentence, String tdGovId) {

		String tdDepLemma = td.dep().lemma();
		String tdGovLemma = td.gov().lemma();

		Event event = storySentence.getPredicate(tdGovId);

		if (tdDepLemma.equals("get")) {
			//create concept
			if (event == null) {
				event = new Event(tdGovLemma);
				storySentence.addPredicate(tdGovId, event);
			}

			event.addConcept(tdDepLemma + " " + td.gov().originalText());

		}

		event.addConcept(cp.createConceptAsVerb(tdGovLemma));
		event.addConcept(
				cp.createConceptWithDirectObject(tdGovLemma, "someone"));

	}

	private void extractDoerDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();

		Noun noun = asr.getNoun(tdDepId);

		if (noun == null) {
			if (tdDepTag.equals("NNP")) {
				noun = extractCategory(getNER(tdDepLemma), tdDepLemma);
				noun.setProper();
			} else if (tdDepTag.contains("NN")) {
				noun = extractCategory(getSRL(tdDepLemma), tdDepLemma);
			}

			//log.debug(noun.getClass().toString());

			if (noun != null) {
				asr.addNoun(tdDepId, noun);
			}

		}

		/** if noun exists **/
		if (noun != null) {

			/** if 'noun is adjective' format **/
			if (tdGovTag.equals("JJ")) {

				noun.addAttribute("HasProperty", tdGovLemma);

				Description description = storySentence.getDescription(tdGovId);

				if (description == null) {
					description = new Description();
				}

				description.addDoer(tdDepId, noun);
				description.addAttribute("HasProperty", tdGovLemma);
				description.addConcept(cp.createConceptAsAdjective(tdGovLemma));
				description.addConcept(
						cp.createConceptAsPredicativeAdjective(tdGovLemma));

				storySentence.addDescription(tdGovId, description);

				log.debug(noun.getId() + " hasProperty " + tdGovLemma);

			}
			/** if verb **/
			else if (tdGovTag.contains("VB")) {

				boolean hasARelation = tdGovLemma.equals("has")
						|| tdGovLemma.equals("have");

				if (!hasARelation || !dictionary.copulas.contains(tdGovLemma)) {
					noun.addAttribute("CapableOf", tdGovLemma);
					log.debug(noun.getId() + " capable of " + tdGovLemma);
				}

				Event event = storySentence.getPredicate(tdGovId);

				if (event == null) {
					event = new Event(tdGovLemma);
				}

				event.addDoer(tdDepId, noun);
				event.addConcept(cp.createConceptAsVerb(tdGovLemma));
				storySentence.addPredicate(tdGovId, event);

			}
			/** for isA type relation **/
			else if (tdGovTag.contains("NN")) {

				//because of John isA John...
				String tdGovIdNN = (td.gov().sentIndex() + 1) + " "
						+ td.gov().index();
				
				Noun noun2 = asr.getNoun(tdGovIdNN);
				
				if (noun2 == null) {

					if (tdGovTag.equals("NNP")) {
						noun2 = extractCategory(getNER(tdGovLemma), tdGovLemma);
						noun2.setProper();
					} else if (tdGovTag.contains("NN")) {
						noun2 = extractCategory(getSRL(tdGovLemma), tdGovLemma);
					}

					asr.addNoun(tdGovIdNN, noun2);

				}

				noun.addReference("IsA", tdGovIdNN, noun2);

				Description description = storySentence.getDescription(tdGovIdNN);

				if (description == null) {
					description = new Description();
				}

				description.addDoer(tdDepId, noun);
				description.addReference("IsA", tdDepId, noun2);

				description.addConcept(cp.createConceptAsAdjective(tdGovLemma));
				description.addConcept(
						cp.createConceptAsPredicativeAdjective(tdGovLemma));

				storySentence.addDescription(tdGovIdNN, description);

			}

		}

	}

	private void extractCompoundDependency(TypedDependency td, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();

		boolean properNouns = tdDepTag.equals("NNP") && tdGovTag.equals("NNP");
		boolean commonNouns = tdDepTag.equals("NN") && tdGovTag.contains("NN");
		String name = tdDepLemma + " " + tdGovLemma;

		Noun noun = asr.getNoun(tdGovId);

		if (noun == null) {

			if (properNouns) {
				noun = extractCategory(getNER(name), name);
				noun.setProper();
			} else if (commonNouns) {
				noun = extractCategory(getSRL(name), name);
			}

		} else {
			noun.setId(name);
		}

		if (noun != null) {
			asr.addNoun(tdGovId, noun);
		}

	}

	private Noun extractCategory(String category, String word) {

		switch (category) {
			case "PERSON" :
				return new Character(word);
			case "ORGANIZATION" :
				return new Location(word);
			case "LOCATION" :
				return new Location(word);
			case "PLACE" :
				return new Location(word);
			case "OBJECT" :
				return new Object(word);
			case "UNKNOWN" :
				return new Unknown(word);
			default :
				return null;
		}

	}

	private String getSRL(String text) {
		for (String entityValue : SRL_ENTITY_LIST) {
			if (ConceptNetDAO.checkSRL(text, "isA", entityValue)) {
				return entityValue.toUpperCase();
			}
		}
		return "UNKNOWN";
	}

	private String getNER(String text) {
		// http://blog.thedigitalgroup.com/sagarg/2015/06/26/named-entity-recognition/
		String output = classifier.classifyToString(text);

		try {

			for (int i = 0, j = NER_ENTITY_LIST.length; i < j; i++) {

				String entity = NER_ENTITY_LIST[i];
				Pattern pattern = NER_ENTITY_LIST_COMPILED[i];
				Matcher matcher = pattern.matcher(output);
				if (matcher.find()) {
					log.debug(entity);
					return entity;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "UNKNOWN";

	}

	private float getPolarityOfEvent(Clause clause) {

		List<String> concepts = clause.getConcepts();

		if (concepts == null) {
			return (float) 0;
		}

		float negated = -1;
		float worstPolarity = Float.MAX_VALUE;
		float polarity = 0;
		for (String concept : concepts) {

			if (!concept.contains("not")) {
				polarity = snp.getPolarity(concept.replace(" ", "_"));
				if (polarity < worstPolarity) {
					worstPolarity = polarity;
				}
			} else {

				String temp = concept.replace("not ", "");
				temp = temp.replace(" ", "_");
				log.debug(temp);
				polarity = snp.getPolarity(temp);
				polarity *= negated;
				if (polarity < worstPolarity) {
					worstPolarity = polarity;
				}
			}
			log.debug(worstPolarity);
		}

		return worstPolarity;

	}

}