package model.text_understanding.extractors;

import java.util.List;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.extractors.negation.NegationExtractorJJRB;
import model.text_understanding.extractors.negation.NegationExtractorNN;
import model.text_understanding.extractors.negation.NegationExtractorVB;

public class NegationExtractor {

	private static Logger log = Logger
			.getLogger(NegationExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

		// neg ( give-4 , not-3 )  gov, dep
		// neg ( doctor-5 , not-3 ) 
		// neg ( cute-4 , not-3 ) 

		String tdGovTag = td.gov().tag();

		if (tdGovTag.contains("VB")) {
			NegationExtractorVB.extract(asr, cp, td, storySentence, tdDepId,
					tdGovId, listDependencies);
		} else if (tdGovTag.contains("NN")) {
			NegationExtractorNN.extract(asr, cp, td, storySentence, tdDepId,
					tdGovId, listDependencies);
		} else if (tdGovTag.contains("JJ") || tdGovTag.equals("RB")) {
			NegationExtractorJJRB.extract(asr, cp, td, storySentence, tdDepId,
					tdGovId, listDependencies);
		}

	}

}
