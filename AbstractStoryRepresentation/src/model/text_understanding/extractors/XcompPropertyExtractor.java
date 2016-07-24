package model.text_understanding.extractors;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

public class XcompPropertyExtractor {

	private static Logger log = Logger
			.getLogger(XcompPropertyExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovLemma = td.gov().lemma();

		Description description = storySentence.getDescription(tdDepId);
		if (description == null) {
			description = new Description();
		}

		for (Map.Entry<String, Noun> entry : storySentence.getEvent(tdGovId)
				.getManyDoers().entrySet()) {

			if (tdDepTag.equals("JJ") || tdDepTag.equals("RB")) {

				entry.getValue().addAttribute("HasProperty", tdDepLemma);

				List<String> notHasPropertyAttr = entry.getValue()
						.getAttribute("NotHasProperty");

				if (notHasPropertyAttr != null) {
					notHasPropertyAttr.remove(tdDepLemma);
					if (notHasPropertyAttr.isEmpty()) {
						entry.getValue().getAttributes()
								.remove("NotHasProperty");
					}
				}

				description.addDoer(entry.getKey(), entry.getValue());
				description.addAttribute("HasProperty", tdDepLemma);
				description.addConcept(cp.createConceptAsAdjective(tdDepLemma));
				description.addConcept(
						cp.createConceptAsPredicativeAdjective(tdDepLemma));

				storySentence.getManyEvents().remove(tdGovId);

				storySentence.addDescription(tdDepId, description);

			} else if (tdDepTag.contains("NN")) {

				Noun noun2 = asr.getNoun(tdDepId);
				if (noun2 == null) {
					if (tdDepTag.equals("NNP")) {
						noun2 = Extractor.extractCategory(
								Extractor.getNER(tdDepLemma), tdDepLemma);
						noun2.setProper();
					} else if (tdDepTag.contains("NN")) {
						noun2 = Extractor.extractCategory(
								Extractor.getSRL(tdDepLemma), tdDepLemma);
					}
				}

				if (noun2 != null) {

					asr.addNoun(tdDepId, noun2);

					entry.getValue().addReference("IsA", tdDepId, noun2);

					if (entry.getValue().getAttribute("NotIsA") != null) {
						entry.getValue().getAttribute("NotIsA").remove(tdDepId);

						if (entry.getValue().getAttribute("NotIsA").isEmpty()) {
							entry.getValue().getAttributes().remove("NotIsA");
						}
					}

					description.addReference("IsA", tdDepId, noun2);
					description.addConcept(cp.createConceptAsRole(tdDepLemma));

					storySentence.getManyEvents().remove(tdGovId);
					storySentence.addDescription(tdDepId, description);

				} else {
					log.debug("Error for " + tdDepLemma + " " + tdGovLemma);
				}

			}
		}

	}

}
