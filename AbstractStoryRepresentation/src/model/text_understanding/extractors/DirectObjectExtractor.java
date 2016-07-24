package model.text_understanding.extractors;

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

public class DirectObjectExtractor {

	private static Logger log = Logger
			.getLogger(DirectObjectExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId,
			Map<String, Integer> dobjMappingHasHave) {

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

						description.addConcept(cp.createConceptWithDirectObject(
								tdGovLemma, tdDepLemma));
						description.addConcept(tdDepLemma);

					}

				}

				storySentence.addDescription(tdGovId, description);

				if (dobjMappingHasHave.get(tdGovId) != null) {
					dobjMappingHasHave.put(tdGovId,
							dobjMappingHasHave.get(tdGovId) - 1);
					if (dobjMappingHasHave.get(tdGovId) == 0) {
						storySentence.getManyEvents().remove(tdGovId);
					}
				}

			} else {

				//unsure sometimes has no doer
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

			}

		}

	}

}
