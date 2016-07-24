package model.text_understanding.extractors;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

public class AmodPropertyExtractor {

	private static Logger log = Logger
			.getLogger(AmodPropertyExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId) {

		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();

		Noun noun = asr.getNoun(tdGovId);

		if (noun == null) {

			if (tdGovTag.equals("NNP")) {
				noun = Extractor.extractCategory(Extractor.getNER(tdGovLemma),
						tdGovLemma);
				noun.setProper();
			} else if (tdGovTag.contains("NN")) {
				noun = Extractor.extractCategory(Extractor.getSRL(tdGovLemma),
						tdGovLemma);
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

}
