package model.text_understanding.extractors;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

public class DoerExtractor {

	private static Logger log = Logger.getLogger(DoerExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies,
			Set<String> restrictedCapableOf) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
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

				if (!restrictedCapableOf.contains(tdGovLemma.toLowerCase())) {
					noun.addAttribute("CapableOf", tdGovLemma);
					log.debug(noun.getId() + " capable of " + tdGovLemma);
				}

				Event event = storySentence.getEvent(tdGovId);

				if (event == null) {
					event = new Event(tdGovLemma);
				}

				event.getVerb().setPOS(tdGovTag);
				//find auxiliary
				List<TypedDependency> auxs = Extractor.findDependencies(
						td.dep(), "gov", "aux", listDependencies);
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
						noun2 = Extractor.extractCategory(
								Extractor.getNER(tdGovLemma), tdGovLemma);
						noun2.setProper();
					} else if (tdGovTag.contains("NN")) {
						noun2 = Extractor.extractCategory(
								Extractor.getSRL(tdGovLemma), tdGovLemma);
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

}
