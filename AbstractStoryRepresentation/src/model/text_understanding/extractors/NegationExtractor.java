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

	/**
	 * Processes the “neg” TypedDependency relation. (calls the other Negation
	 * Extractors)
	 * 
	 * @param asr
	 *            Used to retrieve and store information.
	 * @param cp
	 *            Used to construct strings to be used as concepts.
	 * @param td
	 *            Dependency relation from the CoreNLP tool dependency parsing
	 * @param storySentence
	 *            Story sentence object to store or retrieve the extracted
	 *            relations
	 * @param tdDepId
	 *            Position id of the dependency
	 * @param tdGovId
	 *            Position id of the governor
	 * @param listDependencies
	 *            List of dependencies parsed by the CoreNLP tool.
	 */
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
