package model.text_understanding.extractors;

import java.util.List;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

/**
 * handles verbs found in complements (would not exist as event in asr, just as
 * 'details')
 */
public class CompActionExtractor {

	private static Logger log = Logger
			.getLogger(CompActionExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovLemma = td.gov().lemma();

		Event event = storySentence.getEvent(tdGovId);
		if (event == null) {
			event = new Event(tdGovLemma);
		}

		if (tdDepTag.contains("VB")) {

			//get mark, mark -> 'to' swim
			List<TypedDependency> marks = Extractor.findDependencies(td.dep(),
					"gov", "mark", listDependencies);
			String mark = "";
			if (!marks.isEmpty()) {
				mark = marks.get(0).dep().originalText();
			}

			//exhaust details of complement
			List<TypedDependency> nmodTags = Extractor.findDependencies(
					td.dep(), "gov", "nmod", listDependencies);
			for (TypedDependency t : nmodTags) {

				// add as noun
				String nounLemma = t.dep().lemma();
				String tDepId = (t.dep().sentIndex() + 1) + " "
						+ t.dep().index();

				Noun noun = asr.getNoun(tDepId);
				if (noun == null) {
					if (t.dep().tag().equals("NNP")) {
						noun = Extractor.extractCategory(
								Extractor.getNER(nounLemma), nounLemma);
						noun.setProper();
					} else if (t.dep().tag().contains("NN")) {
						noun = Extractor.extractCategory(
								Extractor.getSRL(nounLemma), nounLemma);
					}
					asr.addNoun(tDepId, noun);
				}

				event.getVerb()
						.addClausalComplement(td.dep().originalText() + " "
								+ Extractor.createPrepositionalPhrase(t,
										listDependencies, true));
				event.addConcept(t.gov().lemma());
				event.addConcept(tdDepLemma + " " + Extractor
						.createPrepositionalPhrase(t, listDependencies, false));

			}

			if (nmodTags.isEmpty()) {
				event.getVerb().addClausalComplement(
						mark + " " + td.dep().originalText());
			}

			//check for negation/positives
			int emotion = Extractor.emotionIndicator(tdGovLemma);
			if (emotion != 0) {
				if (emotion == 1) {//negative
					//to do polarity modifications
					event.setNegated(true);
				} else if (emotion == 2) {//positive
					//to do polarity modifications
				}
			}

			storySentence.addEvent(tdGovId, event);

		}

		//for example: wants to 'be friends' (cop + noun format)
		else if (tdDepTag.contains("NN")) {

			List<TypedDependency> copulaTags = Extractor
					.findDependencies(td.dep(), "gov", "cop", listDependencies);
			List<TypedDependency> marks = Extractor.findDependencies(td.dep(),
					"gov", "mark", listDependencies);

			String mark = "";
			if (!marks.isEmpty()) {
				mark = marks.get(0).dep().originalText();
			}

			for (TypedDependency t : copulaTags) {
				event.addConcept(t.dep().lemma() + " " + t.gov().lemma());
				event.getVerb().addClausalComplement(
						mark + " " + t.dep().lemma() + " " + t.gov().lemma());
			}

			storySentence.addEvent(tdGovId, event);

		}

	}

}
