package model.text_understanding.extractors;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;

public class AuxpassPropertyExtractor {

	private static Logger log = Logger
			.getLogger(AuxpassPropertyExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdGovId) {

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

}
