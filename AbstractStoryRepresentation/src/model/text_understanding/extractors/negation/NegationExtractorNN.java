package model.text_understanding.extractors.negation;

import java.util.List;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.StorySentence;

public class NegationExtractorNN {

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

		String tdGovLemma = td.gov().lemma();

		String temp = tdGovId;

		Description d = storySentence.getDescription(temp);

		//chances are restricted coref
		if (d == null) {
			temp = (td.gov().sentIndex() + 1) + " " + td.gov().index();

			d = storySentence.getDescription(temp);
		}

		d.getConcepts().clear();
		d.setNegated(true);

		d.addReference("NotIsA", temp, d.getReference("IsA").remove(temp));

		if (d.getReference("IsA").isEmpty()) {
			d.getReferences().remove("IsA");
		}

		for (Noun noun : d.getManyDoers().values()) {
			noun.addReference("NotIsA", temp,
					noun.getReference("IsA").remove(temp));
			if (noun.getReference("IsA").isEmpty()) {
				noun.getReferences().remove("IsA");
			}
		}

		d.addConcept(cp.createConceptAsRoleNegation(tdGovLemma));

		storySentence.addDescription(temp, d);

	}

}
