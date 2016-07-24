package model.text_understanding.extractors.negation;

import java.util.List;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.StorySentence;

public class NegationExtractorJJRB {

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

		String tdGovLemma = td.gov().lemma();

		Description d = storySentence.getDescription(tdGovId);
		d.getAttribute("HasProperty").remove(tdGovLemma);
		d.addAttribute("NotHasProperty", tdGovLemma);

		d.getConcepts().clear();
		d.setNegated(true);

		if (d.getAttribute("HasProperty").isEmpty()) {
			d.getAttributes().remove("HasProperty");
		}

		for (Noun noun : d.getManyDoers().values()) {
			noun.getAttribute("HasProperty").remove(tdGovLemma);
			noun.addAttribute("NotHasProperty", tdGovLemma);

			if (noun.getAttribute("HasProperty").isEmpty()) {
				noun.getAttributes().remove("HasProperty");
			}
		}

		d.addConcept(cp.createConceptAsAdjectiveNegated(tdGovLemma));
		d.addConcept(
				cp.createConceptAsPredicativeAdjectiveNegated(tdGovLemma));

		storySentence.addDescription(tdGovId, d);
		
	}

}
