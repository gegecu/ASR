package model.text_understanding.extractors;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.text_understanding.Extractor;

public class CompoundExtractor {

	private static Logger log = Logger
			.getLogger(CompoundExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			TypedDependency td, String tdGovId) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();
		String tdGovLemma = td.gov().lemma();

		boolean properNouns = tdDepTag.equals("NNP") && tdGovTag.equals("NNP");
		boolean commonNouns = tdDepTag.equals("NN") && tdGovTag.contains("NN");
		String name = tdDepLemma + " " + tdGovLemma;

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
