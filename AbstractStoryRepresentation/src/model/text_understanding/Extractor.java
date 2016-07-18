package model.text_understanding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.instance.AbstractSequenceClassifierInstance;
import model.instance.DictionariesInstance;
import model.instance.StanfordCoreNLPInstance;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
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
	//	private SenticNetParser snp;
	private Dictionaries dictionary;
	private AbstractStoryRepresentation asr;

	private Map<String, Integer> dobjMappingHasHave = new HashMap<>();

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
		//		this.snp = SenticNetParserInstance.getInstance();
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

			//fail implementation idk how to remedy
			for (TypedDependency temp : listDependencies) {
				if (temp.reln().toString().equals("dobj")) {
					String tempId = (temp.gov().sentIndex() + 1) + " "
							+ temp.gov().index();
					if (this.dobjMappingHasHave.get(tempId) == null) {
						this.dobjMappingHasHave.put(tempId, 1);
					} else {
						this.dobjMappingHasHave.put(tempId,
								this.dobjMappingHasHave.get(tempId) + 1);
					}
				}
			}

			for (TypedDependency td : listDependencies) {
				extractDependency(coreference, td, storySentence,
						listDependencies);//extract based on dependency
			}

			// predicate -> event
			//			for (Event event : storySentence.getManyPredicates().values()) {
			//				event.setPolarity(getPolarityOfEvent(event));
			//			}
			//
			//			for (Description description : storySentence.getManyDescriptions()
			//					.values()) {
			//				description.setPolarity(getPolarityOfEvent(description));
			//			}

			// asr.addEvent(storySentence);
			extractedStorySentences.add(storySentence);

		}

		return extractedStorySentences;

	}

	private void extractDependency(Map<String, String> coreference,
			TypedDependency td, StorySentence storySentence,
			List<TypedDependency> listDependencies) {

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
				extractDoerDependency(td, storySentence, tdDepId, tdGovId,
						listDependencies);
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
			/** extract xcomp "HasProperty" and xcomp action **/
			else if (tdReln.equals("xcomp")) {
				if (dictionary.copulas.contains(td.gov().lemma())) {
					extractXcompPropertyDependency(td, storySentence, tdDepId,
							tdGovId);
				} else {
					extractCompActionDependency(td, storySentence, tdDepId,
							tdGovId, listDependencies);
				}
			} else if (tdReln.equals("ccomp")) {
				extractCompActionDependency(td, storySentence, tdDepId, tdGovId,
						listDependencies);
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
			else if (tdReln.contains("nmod")) {
				/** extract possession **/
				if (tdReln.equals("nmod:poss") || tdReln.equals("nmod:of")) {
					extractPossesionDependency(td, storySentence, tdDepId,
							tdGovId);
				} else if (tdReln.equals("nmod:tmod")) {
					//temporal modifier, not sure what to do yet. Just to exclude in location check
				} else { //all prepositions that suggest location (at, in , to , under...)
					extractLocationDependency(td, storySentence, tdDepId,
							tdGovId, listDependencies);
				}
			}
			/** extract negation **/
			else if (tdReln.equals("neg")) {
				extractNegation(td, storySentence, tdDepId, tdGovId);
			}
			/** extract conjuction **/
			else if (tdReln.contains("conj")) {
				/** for noun & adjective only **/
				if (tdReln.equals("conj:and")) {
					if (tdDepTag.contains("NN") || tdDepTag.contains("JJ")
							|| tdDepTag.equals("RB")) {
						extractNNJJRBAndConjunction(td, storySentence, tdDepId,
								tdGovId);
					} else if (tdDepTag.contains("VB")) {
						extractVBAndConjuction(td, storySentence, tdDepId,
								tdGovId, listDependencies);
					}
				}
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void extractVBAndConjuction(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();
		String tdReln = td.reln().toString();

		Clause govClause = null;
		if (tdGovTag.contains("VB")) {
			govClause = storySentence.getEvent(tdGovId);
		} else {
			govClause = storySentence.getDescription(tdGovId);
		}

		Event depEvent = storySentence.getEvent(tdDepId);
		if (depEvent == null) {
			depEvent = new Event(tdDepLemma);
		}

		for (Map.Entry<String, Noun> entry : govClause.getManyDoers()
				.entrySet()) {
			Noun doer = entry.getValue();
			boolean hasARelation = tdDepLemma.equals("has")
					|| tdDepLemma.equals("have");
			if ((!hasARelation) || (!dictionary.copulas.contains(tdDepLemma))) {
				doer.addAttribute("CapableOf", tdDepLemma);
				log.debug(doer.getId() + " capable of " + tdDepLemma);
			}
			depEvent.getVerb().setPOS(tdDepTag);
			//find auxiliary
			List<TypedDependency> auxs = findDependencies(td.dep(), "gov",
					"aux", listDependencies);
			for (TypedDependency t : auxs) {
				depEvent.getVerb().addAuxiliary(t.dep().lemma());
			}
			depEvent.addDoer(entry.getKey(), doer);
			depEvent.addConcept(cp.createConceptAsVerb(tdDepLemma));
			log.debug(tdDepId);
			storySentence.addEvent(tdDepId, depEvent);
		}

	}

	private void extractNNJJRBAndConjunction(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();
		String tdReln = td.reln().toString();

		Clause govClause = null;

		String tdGovIdTemp = tdGovId;
		if (tdGovTag.contains("NN")) {
			tdGovIdTemp = (td.gov().sentIndex() + 1) + " " + td.gov().index();
		}
		if (tdGovTag.contains("VB")) {
			govClause = storySentence.getEvent(tdGovIdTemp);
		} else {
			govClause = storySentence.getDescription(tdGovIdTemp);
		}

		String tdDepIdTemp = tdDepId;
		if (tdDepTag.contains("NN")) {
			tdDepIdTemp = (td.dep().sentIndex() + 1) + " " + td.dep().index();
		}
		Noun conjNN = asr.getNoun(tdDepIdTemp);
		if (tdDepTag.contains("NN")) {
			if (conjNN == null) {
				if (tdDepTag.equals("NNP")) {
					conjNN = extractCategory(getNER(tdDepLemma), tdDepLemma);
					conjNN.setProper();
				} else if (tdDepTag.contains("NN")) {
					conjNN = extractCategory(getSRL(tdDepLemma), tdDepLemma);
				}
				asr.addNoun(tdDepIdTemp, conjNN);
			}
		}

		if (govClause != null) {
			Description d2 = new Description();
			for (Map.Entry<String, Noun> entry : govClause.getManyDoers()
					.entrySet()) {
				Noun doer = entry.getValue();
				if (tdDepTag.contains("NN")) {
					doer.addReference("IsA", tdDepIdTemp, conjNN);
					d2.addReference("IsA", tdDepIdTemp, conjNN);
					d2.addConcept(cp.createConceptAsAdjective(tdDepLemma));
					d2.addConcept(
							cp.createConceptAsPredicativeAdjective(tdDepLemma));
				} else if (tdDepTag.equals("JJ") || tdDepTag.equals("RB")) {
					doer.addAttribute("HasProperty", tdDepLemma);
					d2.addAttribute("HasProperty", tdDepLemma);
					d2.addConcept(cp.createConceptAsAdjective(tdDepLemma));
					d2.addConcept(
							cp.createConceptAsPredicativeAdjective(tdDepLemma));
				}
				d2.addDoer(entry.getKey(), entry.getValue());
			}
			storySentence.addDescription(tdDepIdTemp, d2);
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

			if (!(tdGovLemma.equalsIgnoreCase("has")
					|| tdGovLemma.equalsIgnoreCase("have"))) {
				Event p = storySentence.getEvent(tdGovId);
				log.debug(tdGovId + " " + p);
				p.getConcepts().clear();
				p.setNegated(true);

				if (!p.getDirectObjects().isEmpty()) {
					for (Map.Entry<String, Noun> dobj : p.getDirectObjects()
							.entrySet()) {
						p.addConcept(cp.createNegationVerbWithDirectObject(
								tdGovLemma, dobj.getValue().getId()));
					}
				} else {
					p.addConcept(cp.createNegationVerb(tdGovLemma));
				}

				//went to China
				if (tdGovLemma.equals("go")) {
					Description d = storySentence.getDescription(tdGovId);
					d.setNegated(true);
					for (Map.Entry<String, Noun> doerEntry : p.getManyDoers()
							.entrySet()) {
						Noun doer = doerEntry.getValue();
						for (Map.Entry<String, Noun> location : p.getLocations()
								.entrySet()) {
							doer.getReference("AtLocation")
									.remove(location.getKey());
							d.getReference("AtLocation")
									.remove(location.getKey());
							d.addReference("NotAtLocation", location.getKey(),
									location.getValue());
							d.addConcept(cp.createNegationVerbWithLocation(
									tdGovLemma, location.getValue().getId()));
						}
						if (doer.getReference("AtLocation").isEmpty()) {
							doer.getReferences().remove("AtLocation");
						}
					}
					if (d.getReference("AtLocation").isEmpty()) {
						d.getReferences().remove("AtLocation");
					}
				}
				storySentence.addEvent(tdGovId, p);
			} else {
				Description d = storySentence.getDescription(tdGovId);
				d.getConcepts().clear();
				d.setNegated(true);
				if (d.getReference("HasA") != null) {
					for (Map.Entry<String, Noun> possession : d
							.getReference("HasA").entrySet()) {
						for (Map.Entry<String, Noun> doer : d.getManyDoers()
								.entrySet()) {
							doer.getValue().getReference("HasA")
									.remove(possession.getKey());
							if (doer.getValue().getReference("HasA")
									.isEmpty()) {
								doer.getValue().getReferences().remove("HasA");
							}
							possession.getValue().getReference("IsOwnedBy")
									.remove(doer.getKey());

							if (possession.getValue().getReference("IsOwnedBy")
									.isEmpty()) {
								possession.getValue().getReferences()
										.remove("IsOwnedBy");
							}

							d.addReference("NotHasA", possession.getKey(),
									possession.getValue());
						}

						d.addConcept(cp.createNegationVerbWithDirectObject(
								tdGovLemma, possession.getValue().getId()));

						//						Noun possessor = possession.getValue();
						//						if (possessor.getReference("HasA") != null
						//								&& possessor.getReference("HasA").isEmpty()) {
						//							possessor.getReferences().remove("HasA");
						//						}
					}

					for (Map.Entry<String, Noun> entry : d
							.getReference("NotHasA").entrySet()) {
						d.getReference("HasA").remove(entry.getKey());
					}

					if (d.getReference("HasA") != null
							&& d.getReference("HasA").isEmpty()) {
						d.getReferences().remove("HasA");
					}
				}
				storySentence.addDescription(tdGovId, d);
			}
		} else if (tdGovTag.contains("NN")) {

			String temp = tdGovId;

			Description d = storySentence.getDescription(temp);

			//chances are restricted coref
			if (d == null) {
				temp = (td.gov().sentIndex() + 1) + " " + td.gov().index();

				d = storySentence.getDescription(temp);
			}

			d.getConcepts().clear();
			d.setNegated(true);

			d.addReference("NotIsA", temp, d.getReference("IsA").remove(temp));

			if (d.getReference("IsA").isEmpty()) {
				d.getReferences().remove("IsA");
			}

			for (Noun noun : d.getManyDoers().values()) {
				noun.addReference("NotIsA", temp,
						noun.getReference("IsA").remove(temp));
				if (noun.getReference("IsA").isEmpty()) {
					noun.getReferences().remove("IsA");
				}
			}

			d.addConcept(cp.createConceptAsRoleNegation(tdGovLemma));

			storySentence.addDescription(temp, d);
		}

		else if (tdGovTag.contains("JJ") || tdGovTag.equals("RB")) {
			Description d = storySentence.getDescription(tdGovId);
			d.getAttribute("HasProperty").remove(tdGovLemma);
			d.addAttribute("NotHasProperty", tdGovLemma);

			d.getConcepts().clear();
			d.setNegated(true);

			if (d.getAttribute("HasProperty").isEmpty()) {
				d.getAttributes().remove("HasProperty");
			}

			for (Noun noun : d.getManyDoers().values()) {
				noun.getAttribute("HasProperty").remove(tdGovLemma);
				noun.addAttribute("NotHasProperty", tdGovLemma);

				if (noun.getAttribute("HasProperty").isEmpty()) {
					noun.getAttributes().remove("HasProperty");
				}
			}

			d.addConcept(cp.createConceptAsAdjectiveNegated(tdGovLemma));
			d.addConcept(
					cp.createConceptAsPredicativeAdjectiveNegated(tdGovLemma));

			storySentence.addDescription(tdGovId, d);
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

		log.debug(tdGovId);

		noun.addReference("HasA", tdDepId, noun2);
		noun2.addReference("IsOwnedBy", tdGovId, noun);

		if (noun.getReference("NotHasA") != null) {
			noun.getReference("NotHasA").remove(tdDepId);

			if (noun.getReference("NotHasA").isEmpty()) {
				noun.getReferences().remove("NotHasA");
			}
		}

		//we're not storing NotHasA anyway	

	}

	//only handles existing verbs(predicates) in asr
	private void extractLocationDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

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

		if (noun != null) {

			Event event = storySentence.getEvent(tdGovId);

			if (event != null) {

				Description description = storySentence.getDescription(tdGovId);

				if (description == null) {
					description = new Description();
				}
				if (noun.getType() == TypeOfNoun.LOCATION) {
					for (Map.Entry<String, Noun> entry : storySentence
							.getEvent(tdGovId).getManyDoers().entrySet()) {
						entry.getValue().addReference("AtLocation", tdDepId,
								noun);
						description.addReference("AtLocation", tdDepId, noun);
						description.addDoer(entry.getKey(), entry.getValue());
					}

					event.addLocation(tdDepId, noun); //changed to locations in storysentence
					log.debug("Location: " + tdDepLemma);
					event.addConcept(cp.createConceptAsInfinitive(tdGovLemma,
							tdDepLemma)); //using to as preposition
				}

				//if not a location still add details anyway
				event.getVerb().addPrepositionalPhrase(
						createPrepositionalPhrase(td, listDependencies, true));

				event.addConcept(tdGovLemma + " " + createPrepositionalPhrase(
						td, listDependencies, false));
				event.addConcept(tdDepLemma); //object itself as concept	

				//unsure with id
				storySentence.addDescription(tdGovId, description);

			}
		}

		// predicate -> event
		//		if (tdReln.equals("nmod:to") && tdGovTag.contains("VB")) {
		//			//create concept
		//			Event predicate = storySentence.getPredicate(tdGovId);
		//			if (predicate == null) {
		//				predicate = new Event(tdGovLemma);
		//				storySentence.addPredicate(tdGovId, predicate);
		//			}
		//		}

	}

	/**
	 * create prepositional phrase from nmod dependency. provides surface type
	 * and concept type using boolean
	 */
	private String createPrepositionalPhrase(TypedDependency td,
			List<TypedDependency> listDependencies, Boolean surface) {

		String preposition = td.reln().toString().replace("nmod", "");
		preposition = preposition.replace(":", "");
		if (preposition.equals("")) {
			preposition = "to";
		}
		if (surface) {
			String det = "the";
			List<TypedDependency> dets = findDependencies(td.dep(), "gov",
					"det", listDependencies);
			for (TypedDependency t : dets) {
				det = t.dep().lemma();
				return preposition + " " + det + " " + td.dep().lemma();
			}
		}
		return preposition + " " + td.dep().lemma();
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
						.getEvent(tdGovId).getManyDoers().entrySet()) {
					entry.getValue().addAttribute("HasProperty", tdDepLemma);

					if (entry.getValue()
							.getAttribute("NotHasProperty") != null) {
						entry.getValue().getAttribute("NotHasProperty")
								.remove(tdDepLemma);

						if (entry.getValue().getAttribute("NotHasProperty")
								.isEmpty()) {
							entry.getValue().getAttributes()
									.remove("NotHasProperty");
						}

					}

					description.addDoer(entry.getKey(), entry.getValue());
				}

				description.addAttribute("HasProperty", tdDepLemma);
				description.addConcept(cp.createConceptAsAdjective(tdDepLemma));
				storySentence.getManyEvents().remove(tdGovId);

				//still unsure with id
				storySentence.addDescription(tdDepId, description);

			} else { //add as adverb in verb class		
				Event event = storySentence.getEvent(tdGovId);
				if (event == null) { //verify if create new event is conflicting
					event = new Event(tdGovLemma);
					storySentence.addEvent(tdGovId, event);
				}
				event.getVerb().addAdverb(tdDepLemma);
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

		if (noun.getAttribute("NotHasProperty") != null) {
			noun.getAttribute("NotHasProperty").remove(tdDepLemma);

			if (noun.getAttribute("NotHasProperty").isEmpty()) {
				noun.getAttributes().remove("NotHasProperty");
			}
		}

		//log.debug("amod " + noun.getId());

		Description description = storySentence.getDescription(tdDepId);

		if (description == null) {
			description = new Description();
		}

		description.addAttribute("HasProperty", tdDepLemma);
		description.addConcept(cp.createConceptAsAdjective(tdDepLemma));
		description
				.addConcept(cp.createConceptAsPredicativeAdjective(tdDepLemma));

		storySentence.addDescription(tdDepId, description);

	}

	private void extractXcompPropertyDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();

		Description description = storySentence.getDescription(tdDepId);

		if (description == null) {
			description = new Description();
		}

		for (Map.Entry<String, Noun> entry : storySentence.getEvent(tdGovId)
				.getManyDoers().entrySet()) {

			if (tdDepTag.equals("JJ") || tdDepTag.equals("RB")) {

				entry.getValue().addAttribute("HasProperty", tdDepLemma);

				if (entry.getValue().getAttribute("NotHasProperty") != null) {
					entry.getValue().getAttribute("NotHasProperty")
							.remove(tdDepLemma);

					if (entry.getValue().getAttribute("NotHasProperty")
							.isEmpty()) {
						entry.getValue().getAttributes()
								.remove("NotHasProperty");
					}
				}

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

					if (entry.getValue().getAttribute("NotIsA") != null) {
						entry.getValue().getAttribute("NotIsA").remove(tdDepId);

						if (entry.getValue().getAttribute("NotIsA").isEmpty()) {
							entry.getValue().getAttributes().remove("NotIsA");
						}
					}

					description.addReference("IsA", tdDepId, noun2);
					description.addConcept(cp.createConceptAsRole(tdDepLemma));
				}

			}
		}

		storySentence.getManyEvents().remove(tdGovId);

		storySentence.addDescription(tdDepId, description);

	}

	//handles verbs found in complements (would not exist as event in asr, just as 'details')
	private void extractCompActionDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

		String tdDepLemma = td.dep().lemma();
		String tdDepTag = td.dep().tag();
		String tdGovLemma = td.gov().lemma();

		Event event = storySentence.getEvent(tdGovId);
		if (event == null) {
			event = new Event(tdGovLemma);
			storySentence.addEvent(tdGovId, event);
		}

		if (tdDepTag.contains("VB")) {

			//exhaust details of complement
			List<TypedDependency> nmodTags = findDependencies(td.dep(), "gov",
					"nmod", listDependencies);
			for (TypedDependency t : nmodTags) {
				// add as noun
				String nounLemma = t.dep().lemma();
				String tDepId = (t.dep().sentIndex() + 1) + " "
						+ t.dep().index();
				Noun noun = asr.getNoun(tDepId);
				if (noun == null) {
					if (t.dep().tag().equals("NNP")) {
						noun = extractCategory(getNER(nounLemma), nounLemma);
						noun.setProper();
					} else if (t.dep().tag().contains("NN")) {
						noun = extractCategory(getSRL(nounLemma), nounLemma);
					}
					asr.addNoun(tDepId, noun);
				}

				event.getVerb().addClausalComplement(td.dep().originalText()
						+ " "
						+ createPrepositionalPhrase(t, listDependencies, true));
				event.addConcept(t.gov().lemma());
				event.addConcept(tdDepLemma + " " + createPrepositionalPhrase(t,
						listDependencies, false));
			}

			//check for negation/positives
			int emotion = emotionIndicator(tdGovLemma);
			if (emotion != 0) {
				if (emotion == 1) {//negative
					//to do polarity modifications
					event.setNegated(true);
				} else if (emotion == 2) {//positive
					//to do polarity modifications
				}
			}

		} else if (tdDepTag.contains("NN")) { //for example: wants to 'be friends' (cop + noun format)
			List<TypedDependency> copulaTags = findDependencies(td.dep(), "gov",
					"cop", listDependencies);
			for (TypedDependency t : copulaTags) {
				event.addConcept(t.dep().lemma() + " " + t.gov().lemma());
			}
		}
	}

	/** if verb indicates emotion */
	private int emotionIndicator(String word) {
		//remove array here in the future
		String[] negatives = {"hate", "hates", "dislike", "dislikes"};
		String[] positives = {"like", "likes", "love", "loves"};
		if (Arrays.asList(negatives).contains(word))
			return 1;
		else if (Arrays.asList(positives).contains(word)) {
			return 2;
		}
		return 0;
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

		if (noun != null) {

			// we do not store NotHasA anyway
			if (tdGovLemma.equals("has") || tdGovLemma.equals("have")) {

				Description description = storySentence.getDescription(tdGovId);

				if (description == null) {
					description = new Description();
				}

				for (Map.Entry<String, Noun> entry : storySentence
						.getEvent(tdGovId).getManyDoers().entrySet()) {

					if (noun != null) {

						entry.getValue().addReference("HasA", tdDepId, noun);

						if (entry.getValue().getReference("NotHasA") != null) {
							entry.getValue().getReference("NotHasA")
									.remove(tdDepId);

							if (entry.getValue().getReference("NotHasA")
									.isEmpty()) {
								entry.getValue().getReferences()
										.remove("NotHasA");
							}
						}

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

				storySentence.addDescription(tdGovId, description);

				//				if (counterTotal == counterCurrent)
				//					storySentence.getManyEvents().remove(tdGovId);
				if (this.dobjMappingHasHave.get(tdGovId) != null) {
					this.dobjMappingHasHave.put(tdGovId,
							this.dobjMappingHasHave.get(tdGovId) - 1);
					if (this.dobjMappingHasHave.get(tdGovId) == 0) {
						storySentence.getManyEvents().remove(tdGovId);
					}
				}

			} else {

				Event event = storySentence.getEvent(tdGovId);

				if (event == null) {
					event = new Event(tdGovLemma);
					storySentence.addEvent(tdGovId, event);
				}

				//create concept
				event.addDirectObject(tdDepId, noun);
				storySentence.addEvent(tdGovId, event);

				if (noun.getType() == TypeOfNoun.CHARACTER) //if direct object is a person, change to someone
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

			Event event = storySentence.getEvent(tdGovId);

			if (event == null) {
				event = new Event(td.gov().lemma());
			}

			event.addReceiver(tdDepId, noun);
			storySentence.addEvent(tdGovId, event);

			//log.debug("iobj: " + asr.getNoun(td.dep().lemma()).getId());
		}

	}

	private void extractAuxpassPropertyDependency(TypedDependency td,
			StorySentence storySentence, String tdGovId) {

		String tdDepLemma = td.dep().lemma();
		String tdGovLemma = td.gov().lemma();

		Event event = storySentence.getEvent(tdGovId);

		if (tdDepLemma.equals("get")) {
			//create concept
			if (event == null) {
				event = new Event(tdGovLemma);
				storySentence.addEvent(tdGovId, event);
			}

			event.addConcept(tdDepLemma + " " + td.gov().originalText());

		}

		event.addConcept(cp.createConceptAsVerb(tdGovLemma));
		event.addConcept(
				cp.createConceptWithDirectObject(tdGovLemma, "someone"));

	}

	private void extractDoerDependency(TypedDependency td,
			StorySentence storySentence, String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

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
			if (tdGovTag.equals("JJ") || tdGovTag.equals("RB")) {

				noun.addAttribute("HasProperty", tdGovLemma);

				if (noun.getAttribute("NotHasProperty") != null) {
					noun.getAttribute("NotHasProperty").remove(tdGovLemma);

					if (noun.getAttribute("NotHasProperty").isEmpty()) {
						noun.getAttributes().remove("NotHasProperty");
					}
				}

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

				if ((!hasARelation)
						|| (!dictionary.copulas.contains(tdGovLemma))) {
					noun.addAttribute("CapableOf", tdGovLemma);
					log.debug(noun.getId() + " capable of " + tdGovLemma);
				}

				Event event = storySentence.getEvent(tdGovId);

				if (event == null) {
					event = new Event(tdGovLemma);
				}

				event.getVerb().setPOS(tdGovTag);
				//find auxiliary
				List<TypedDependency> auxs = findDependencies(td.dep(), "gov",
						"aux", listDependencies);
				for (TypedDependency t : auxs) {
					event.getVerb().addAuxiliary(t.dep().lemma());
				}
				event.addDoer(tdDepId, noun);
				event.addConcept(cp.createConceptAsVerb(tdGovLemma));
				log.debug(tdGovId);
				storySentence.addEvent(tdGovId, event);

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

				if (noun.getAttribute("NotIsA") != null) {
					noun.getAttribute("NotIsA").remove(tdGovIdNN);

					if (noun.getAttribute("NotIsA").isEmpty()) {
						noun.getAttributes().remove("NotIsA");
					}
				}

				Description description = storySentence
						.getDescription(tdGovIdNN);

				if (description == null) {
					description = new Description();
				}

				description.addDoer(tdDepId, noun);
				description.addReference("IsA", tdGovIdNN, noun2);

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

	//	private float getPolarityOfEvent(Clause clause) {
	//
	//		List<String> concepts = clause.getConcepts();
	//
	//		if (concepts == null) {
	//			return (float) 0;
	//		}
	//
	//		float negated = -1;
	//		float sumPolarity = 0;
	//		for (String concept : concepts) {
	//
	//			if (!concept.contains("not")) {
	//				sumPolarity += snp.getPolarity(concept.replace(" ", "_"));
	//
	//			} else {
	//				String temp = concept.replace("not ", "");
	//				temp = temp.replace(" ", "_");
	//				sumPolarity += snp.getPolarity(temp) * negated;
	//			}
	//			log.debug(sumPolarity);
	//		}
	//
	//		return sumPolarity / concepts.size();
	//	}

	private List<TypedDependency> findDependencies(IndexedWord iw,
			String inputType, String rel, List<TypedDependency> list) {
		List<TypedDependency> returnList = new ArrayList<>();
		if (inputType.equals("gov")) {
			for (TypedDependency td : list) {
				if (compareIndexedWord(td.gov(), iw)
						&& td.reln().toString().contains(rel)) {
					returnList.add(td);
				}
			}
		} else if (inputType.equals("dep")) {
			for (TypedDependency td : list) {
				if (compareIndexedWord(td.dep(), iw)
						&& td.reln().toString().contains(rel)) {
					returnList.add(td);
				}
			}
		}
		return returnList;
	}

	/** checks if indexed words are equal */
	private boolean compareIndexedWord(IndexedWord arg1, IndexedWord arg2) {
		if (arg1.lemma() != null) {//sometimes when reln = ROOT
			if (arg1.lemma().equals(arg2.lemma())
					&& arg1.index() == arg2.index()) {
				return true;
			}
		}
		return false;
	}

}