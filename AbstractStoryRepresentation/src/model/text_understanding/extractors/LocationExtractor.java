package model.text_understanding.extractors;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

/**
 * only handles existing verbs(predicates) in asr
 */
public class LocationExtractor {

	private static Logger log = Logger
			.getLogger(LocationExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovLemma = td.gov().lemma();

		Noun noun = asr.getNoun(tdDepId);
		if (noun == null) {
			if (tdDepTag.equals("NNP")) {
				noun = Extractor.extractCategory(Extractor.getNER(tdDepLemma),
						tdDepLemma);
				noun.setProper();
			} else if (tdDepTag.contains("NN")) {
				noun = Extractor.extractCategory(Extractor.getSRL(tdDepLemma),
						tdDepLemma);
			}
		}

		if (noun != null) {

			asr.addNoun(tdDepId, noun);

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

					storySentence.addDescription(tdGovId, description);
				}

				//if not a location still add details anyway
				event.getVerb().addPrepositionalPhrase(Extractor
						.createPrepositionalPhrase(td, listDependencies, true));

				event.addConcept(
						tdGovLemma + " " + Extractor.createPrepositionalPhrase(
								td, listDependencies, false));
				event.addConcept(tdDepLemma); //object itself as concept	

				//unsure with id
			}

		} else {
			log.debug("Error for " + tdDepLemma + " " + tdGovLemma);
		}

	}

}
