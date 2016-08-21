package model.text_understanding.extractors;

import java.util.Map;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.text_understanding.Extractor;

public class CompoundExtractor {

	private static Logger log = Logger
			.getLogger(CompoundExtractor.class.getName());

	/**
	 * Processes the “compound” TypedDependency relation, concatenates words
	 * that are identified as compound
	 * 
	 * @param asr
	 *            Used to retrieve and store information
	 * @param td
	 *            Dependency relation from the CoreNLP tool dependency parsing
	 * @param tdGovId
	 *            Position id of the governor
	 * @param compoundMapping
	 *            Map of compound words
	 */
	public static void extract(AbstractStoryRepresentation asr,
			TypedDependency td, String tdGovId,
			Map<String, String> compoundMapping) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();

		boolean properNouns = tdDepTag.equals("NNP") && tdGovTag.equals("NNP");
		boolean commonNouns = tdDepTag.equals("NN") && tdGovTag.contains("NN");
		String name = compoundMapping.get(tdGovId);

		System.out.println(name);

		Noun noun = asr.getNoun(tdGovId);

		if (noun == null) {

			if (properNouns) {
				noun = Extractor.extractCategory(Extractor.getNER(name), name);
				noun.setProper();
			} else if (commonNouns) {
				noun = Extractor.extractCategory(Extractor.getSRL(name), name);
			}

		} else {
			noun.setId(name);
		}

		if (noun != null) {
			asr.addNoun(tdGovId, noun);
		} else {
			log.debug("Error for " + tdDepLemma + " " + tdGovLemma);
		}

	}

}
