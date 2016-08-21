package model.text_understanding.extractors;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

public class PossesionExtractor {

	private static Logger log = Logger
			.getLogger(PossesionExtractor.class.getName());

	/**
	 * Processes the “nmod:poss” or “nmod:of” TypedDependency relation, for
	 * “HasA” and “IsOwnedBy” assertion of nouns.
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
	 */
	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId) {

		//nmod:poss ( ball-5 , John-3 )  gov, dep
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
		}

		Noun noun2 = asr.getNoun(tdGovId);
		if (noun2 == null) {
			if (tdGovTag.equals("NNP")) {
				noun2 = Extractor.extractCategory(Extractor.getNER(tdGovLemma),
						tdGovLemma);
				noun2.setProper();
			} else if (tdGovTag.contains("NN")) {
				noun2 = Extractor.extractCategory(Extractor.getSRL(tdGovLemma),
						tdGovLemma);
			}
		}

		if (noun == null || noun2 == null) {
			log.debug("Error for " + tdDepLemma + " " + tdGovLemma);
			return;
		}

		log.debug(tdGovId);

		asr.addNoun(tdDepId, noun);
		asr.addNoun(tdGovId, noun2);
		noun.addReference("HasA", tdGovId, noun2);
		noun2.addReference("IsOwnedBy", tdDepId, noun);

		if (noun.getReference("NotHasA") != null) {
			noun.getReference("NotHasA").remove(tdGovId);

			if (noun.getReference("NotHasA").isEmpty()) {
				noun.getReferences().remove("NotHasA");
			}
		}

		//we're not storing NotHasA anyway	

	}

}
