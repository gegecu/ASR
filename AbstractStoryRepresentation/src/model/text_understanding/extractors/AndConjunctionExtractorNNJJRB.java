package model.text_understanding.extractors;

import java.util.Map;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

public class AndConjunctionExtractorNNJJRB {

	private static Logger log = Logger
			.getLogger(AndConjunctionExtractorNNJJRB.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();

		Clause govClause = null;

		String tdGovIdTemp = tdGovId;
		if (tdGovTag.contains("NN")) {
			tdGovIdTemp = (td.gov().sentIndex() + 1) + " " + td.gov().index();
		} else if (tdGovTag.contains("VB")) {
			govClause = storySentence.getEvent(tdGovIdTemp);
		} else {
			govClause = storySentence.getDescription(tdGovIdTemp);
		}

		String tdDepIdTemp = tdDepId;
		if (tdDepTag.contains("NN")) {
			tdDepIdTemp = (td.dep().sentIndex() + 1) + " " + td.dep().index();
		}
		
		Noun conjNN = asr.getNoun(tdDepIdTemp);
		if (tdDepTag.contains("NN")) {
			if (conjNN == null) {
				if (tdDepTag.equals("NNP")) {
					conjNN = Extractor.extractCategory(
							Extractor.getNER(tdDepLemma), tdDepLemma);
					conjNN.setProper();
				} else if (tdDepTag.contains("NN")) {
					conjNN = Extractor.extractCategory(
							Extractor.getSRL(tdDepLemma), tdDepLemma);
				}
				asr.addNoun(tdDepIdTemp, conjNN);
			}
		}

		if (govClause != null) {
			Description d2 = new Description();
			for (Map.Entry<String, Noun> entry : govClause.getManyDoers()
					.entrySet()) {
				Noun doer = entry.getValue();
				if (tdDepTag.contains("NN")) {
					doer.addReference("IsA", tdDepIdTemp, conjNN);
					d2.addReference("IsA", tdDepIdTemp, conjNN);
					d2.addConcept(cp.createConceptAsAdjective(tdDepLemma));
					d2.addConcept(
							cp.createConceptAsPredicativeAdjective(tdDepLemma));
				} else if (tdDepTag.equals("JJ") || tdDepTag.equals("RB")) {
					doer.addAttribute("HasProperty", tdDepLemma);
					d2.addAttribute("HasProperty", tdDepLemma);
					d2.addConcept(cp.createConceptAsAdjective(tdDepLemma));
					d2.addConcept(
							cp.createConceptAsPredicativeAdjective(tdDepLemma));
				}
				d2.addDoer(entry.getKey(), entry.getValue());
			}
			storySentence.addDescription(tdDepIdTemp, d2);
		}

	}

}
