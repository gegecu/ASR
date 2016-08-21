package model.text_understanding.extractors;

import java.util.Map;

import org.apache.log4j.Logger;

import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.trees.TypedDependency;
import model.instance.DictionariesInstance;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;

public class AdvmodPropertyExtractor {

	private static Logger log = Logger
			.getLogger(AdvmodPropertyExtractor.class.getName());

	/**
	 * Provides accessors for various grammatical, semantic, and world knowledge
	 * lexicons and word lists primarily used by the Sieve coreference system
	 * <br>
	 * <br>
	 * Primarily used for checking copulas (linking verb)
	 */
	private static Dictionaries dictionary;

	static {
		dictionary = DictionariesInstance.getInstance();
	}

	/**
	 * Processes the “advmod” TypedDependency relation, for “HasProperty”
	 * assertion of nouns.
	 * 
	 * @param asr
	 *            Used to retrieve and store information.
	 * @param cp
	 *            Used to construct strings to be used as concepts.
	 * @param td
	 *            Dependency relation from the CoreNLP tool dependency parsing
	 * @param storySentence
	 *            Story sentence object to store or retrieve the extracted
	 *            relations
	 * @param tdDepId
	 *            Position id of the dependency
	 * @param tdGovId
	 *            Position id of the governor
	 */
	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId) {

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

			}
			//add as adverb in verb class
			else {
				Event event = storySentence.getEvent(tdGovId);
				if (event == null) { //verify if create new event is conflicting
					event = new Event(tdGovLemma);
					storySentence.addEvent(tdGovId, event);
				}
				event.getVerb().addAdverb(tdDepLemma);
			}

		}

	}

}
