package model.text_understanding.extractors;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

public class ReceiverExtractor {

	private static Logger log = Logger
			.getLogger(ReceiverExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();

		if ((tdDepTag.contains("NN") || asr.getNoun(tdDepId) != null)
				&& tdGovTag.contains("VB")) {

			Noun noun = asr.getNoun(tdDepId);

			if (noun == null) {
				if (tdDepTag.equals("NNP")) {
					noun = Extractor.extractCategory(
							Extractor.getNER(tdDepLemma), tdDepLemma);
					noun.setProper();
				} else if (tdDepTag.contains("NN")) {
					noun = Extractor.extractCategory(
							Extractor.getSRL(tdDepLemma), tdDepLemma);
				}
			}

			if (noun != null) {

				asr.addNoun(tdDepId, noun);

				Event event = storySentence.getEvent(tdGovId);
				if (event == null) {
					event = new Event(td.gov().lemma());
				}
				event.addReceiver(tdDepId, noun);
				storySentence.addEvent(tdGovId, event);

			} else {
				log.debug("Error for " + tdDepLemma + " " + tdGovLemma);
			}

		}

	}

}
