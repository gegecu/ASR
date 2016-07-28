package model.text_understanding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.instance.CRFClassifierInstance;
import model.instance.DictionariesInstance;
import model.instance.StanfordCoreNLPInstance;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Object;
import model.story_representation.story_element.noun.Unknown;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.extractors.AdvmodPropertyExtractor;
import model.text_understanding.extractors.AmodPropertyExtractor;
import model.text_understanding.extractors.AndConjuctionExtractorVB;
import model.text_understanding.extractors.AndConjunctionExtractorNNJJRB;
import model.text_understanding.extractors.AuxpassPropertyExtractor;
import model.text_understanding.extractors.CompActionExtractor;
import model.text_understanding.extractors.CompoundExtractor;
import model.text_understanding.extractors.DirectObjectExtractor;
import model.text_understanding.extractors.DoerExtractor;
import model.text_understanding.extractors.LocationExtractor;
import model.text_understanding.extractors.NegationExtractor;
import model.text_understanding.extractors.PossesionExtractor;
import model.text_understanding.extractors.ReceiverExtractor;
import model.text_understanding.extractors.XcompPropertyExtractor;
import model.utility.PartOfSpeechComparator;
import model.utility.TypedDependencyComparator;

@SuppressWarnings("rawtypes")
public class Extractor {

	private static Logger log = Logger.getLogger(Extractor.class.getName());

	private static StanfordCoreNLP pipeline;
	private static CRFClassifier classifier;
	private static ConceptParser cp;
	//	private SenticNetParser snp;
	private static Dictionaries dictionary;
	private AbstractStoryRepresentation asr;

	private Map<String, Integer> dobjMappingHasHave = new HashMap<>();
	private Map<String, String> compoundMapping = new HashMap<>();
	private Set<String> restrictedCapableOf = new HashSet<>();

	private static String[] SRL_ENTITY_LIST = {"person", "place", "object"};
	private static String[] NER_ENTITY_LIST = {"person", "location", "date",
			"organization", "time", "money", "percentage"};
	private static Pattern[] NER_ENTITY_LIST_COMPILED = new Pattern[NER_ENTITY_LIST.length];

	private static List<String> negatives = Arrays
			.asList(new String[]{"hate", "hates", "dislike", "dislikes"});
	private static List<String> positives = Arrays
			.asList(new String[]{"like", "likes", "love", "loves"});

	static {
		for (int i = 0, j = NER_ENTITY_LIST.length; i < j; i++) {
			String entity = NER_ENTITY_LIST[i] = NER_ENTITY_LIST[i]
					.toUpperCase();
			NER_ENTITY_LIST_COMPILED[i] = Pattern.compile("([a-zA-Z0-9.%]+(/"
					+ entity + ")[ ]*)*[a-zA-Z0-9.%]+(/" + entity + ")");
		}
		cp = new ConceptParser();
		pipeline = StanfordCoreNLPInstance.getInstance();
		classifier = CRFClassifierInstance.getInstance();
		dictionary = DictionariesInstance.getInstance();
	}

	public Extractor(AbstractStoryRepresentation asr) {
		this.asr = asr;
		this.restrictedCapableOf.add("has");
		this.restrictedCapableOf.add("have");
		this.restrictedCapableOf.addAll(dictionary.copulas);
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
			dobjMappingHasHave.clear();
			
			for (TypedDependency temp : listDependencies) {
				if (temp.reln().toString().equals("dobj")) {
					String tempId = (temp.gov().sentIndex() + 1) + " "
							+ temp.gov().index();
					
					if (coreference.get(tempId) != null) {
						tempId = coreference.get(tempId);
					}
					
					if (this.dobjMappingHasHave.get(tempId) == null) {
						this.dobjMappingHasHave.put(tempId, 1);
					} else {
						this.dobjMappingHasHave.put(tempId,
								this.dobjMappingHasHave.get(tempId) + 1);
					}
				}
			}
			
			//fail implementation idk how to remedy
			//compoundMapping.clear();
			
			for (TypedDependency temp : listDependencies) {
				if (temp.reln().toString().equals("compound")) {
					String tempId = (temp.gov().sentIndex() + 1) + " "
							+ temp.gov().index();
					
					if (coreference.get(tempId) != null) {
						tempId = coreference.get(tempId);
					}
					if (this.compoundMapping.get(tempId) == null) {
						this.compoundMapping.put(tempId, temp.dep().lemma() + " " + temp.gov().lemma());
					} else {
						this.compoundMapping.put(tempId, temp.dep().lemma() + " " + this.compoundMapping.get(tempId));
					}
				}
			}

			for (TypedDependency td : listDependencies) {
				extractDependency(coreference, td, storySentence,
						listDependencies);//extract based on dependency
			}

			removeClausesWithNoDoers(storySentence);
			extractedStorySentences.add(storySentence);

		}

		return extractedStorySentences;

	}

	private void removeClausesWithNoDoers(StorySentence storySentence) {

		List<String> forRemoval = new ArrayList<>();

		Map<String, Event> events = storySentence.getManyEvents();
		for (Entry<String, Event> e : events.entrySet()) {
			if (e.getValue().getManyDoers().isEmpty()) {
				forRemoval.add(e.getKey());
			}
		}
		for (String remove : forRemoval) {
			events.remove(remove);
		}
		forRemoval.clear();

		Map<String, Description> descriptions = storySentence
				.getManyDescriptions();
		for (Entry<String, Description> e : descriptions.entrySet()) {
			if (e.getValue().getManyDoers().isEmpty()) {
				forRemoval.add(e.getKey());
			}
		}
		for (String remove : forRemoval) {
			descriptions.remove(remove);
		}
		forRemoval.clear();

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
			System.out.println(td.reln().toString() + td.dep().lemma() + td.gov().lemma());
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
				CompoundExtractor.extract(asr, td, tdGovId, compoundMapping);
			}
			/** get doer or subject **/
			else if (tdReln.equals("nsubj") || tdReln.equals("nmod:agent")) {
				DoerExtractor.extract(asr, cp, td, storySentence, tdDepId,
						tdGovId, listDependencies, restrictedCapableOf);
			}
			/** get auxpass "HasProperty" (to create get + verb concepts) **/
			else if (tdReln.equals("auxpass")) {
				AuxpassPropertyExtractor.extract(asr, cp, td, storySentence,
						tdGovId, listDependencies);
			}
			/** get indirect object or receiver **/
			else if (tdReln.equals("iobj") || tdReln.equals("nmod:for")) {
				ReceiverExtractor.extract(asr, cp, td, storySentence, tdDepId,
						tdGovId);
			}
			/** get direct object **/
			else if (tdReln.equals("dobj") || tdReln.equals("nmod:with")
					|| tdReln.equals("nsubjpass")) {
				DirectObjectExtractor.extract(asr, cp, td, storySentence,
						tdDepId, tdGovId, dobjMappingHasHave);
			}
			/** extract xcomp "HasProperty" and xcomp action **/
			else if (tdReln.equals("xcomp")) {
				if (dictionary.copulas.contains(td.gov().lemma())) {
					XcompPropertyExtractor.extract(asr, cp, td, storySentence,
							tdDepId, tdGovId);
				} else {
					CompActionExtractor.extract(asr, cp, td, storySentence,
							tdDepId, tdGovId, listDependencies);
				}
			} else if (tdReln.equals("ccomp")) {
				CompActionExtractor.extract(asr, cp, td, storySentence, tdDepId,
						tdGovId, listDependencies);
			}
			/** extract amod "IsA" ('adjective noun' format) **/
			else if (tdReln.equals("amod")) {
				AmodPropertyExtractor.extract(asr, cp, td, storySentence,
						tdDepId, tdGovId);
			}
			/** extract advmod "HasProperty" **/
			else if (tdReln.equals("advmod")) {
				AdvmodPropertyExtractor.extract(asr, cp, td, storySentence,
						tdDepId, tdGovId);
			}
			/** extract location **/
			else if (tdReln.contains("nmod")) {
				/** extract possession **/
				if (tdReln.equals("nmod:poss") || tdReln.equals("nmod:of")) {
					PossesionExtractor.extract(asr, cp, td, storySentence,
							tdDepId, tdGovId);
				} else if (tdReln.equals("nmod:tmod")) {
					//temporal modifier, not sure what to do yet. Just to exclude in location check
				} else { //all prepositions that suggest location (at, in , to , under...)
					LocationExtractor.extract(asr, cp, td, storySentence,
							tdDepId, tdGovId, listDependencies);
				}
			}
			/** extract negation **/
			else if (tdReln.equals("neg")) {
				NegationExtractor.extract(asr, cp, td, storySentence, tdDepId,
						tdGovId, listDependencies);
			}
			/** extract conjuction **/
			else if (tdReln.contains("conj")) {
				/** for noun & adjective (adverb as adj) only **/
				if (tdReln.equals("conj:and")) {
					if (tdDepTag.contains("NN") || tdDepTag.contains("JJ")
							|| tdDepTag.equals("RB")) {
						AndConjunctionExtractorNNJJRB.extract(asr, cp, td,
								storySentence, tdDepId, tdGovId);
					} else if (tdDepTag.contains("VB")) {
						AndConjuctionExtractorVB.extract(asr, cp, td,
								storySentence, tdDepId, tdGovId,
								listDependencies, restrictedCapableOf);
					}
				}
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * create prepositional phrase from nmod dependency. provides surface type
	 * and concept type using boolean
	 */
	public static String createPrepositionalPhrase(TypedDependency td,
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

	/** if verb indicates emotion */
	public static int emotionIndicator(String word) {
		if (negatives.contains(word))
			return 1;
		else if (Arrays.asList(positives).contains(word)) {
			return 2;
		}
		return 0;
	}

	public static Noun extractCategory(String category, String word) {
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

	public static String getSRL(String text) {
		for (String entityValue : SRL_ENTITY_LIST) {
			if (ConceptNetDAO.checkSRL(text, "isA", entityValue)) {
				return entityValue.toUpperCase();
			}
		}
		return "UNKNOWN";
	}

	public static String getNER(String text) {
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

	public static List<TypedDependency> findDependencies(IndexedWord iw,
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
	public static boolean compareIndexedWord(IndexedWord arg1,
			IndexedWord arg2) {
		if (arg1.lemma() != null) {//sometimes when reln = ROOT
			if (arg1.lemma().equals(arg2.lemma())
					&& arg1.index() == arg2.index()) {
				return true;
			}
		}
		return false;
	}

}