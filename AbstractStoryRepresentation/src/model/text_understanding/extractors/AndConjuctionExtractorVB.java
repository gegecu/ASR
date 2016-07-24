package model.text_understanding.extractors;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.trees.TypedDependency;
import model.instance.DictionariesInstance;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

public class AndConjuctionExtractorVB {

	private static Logger log = Logger
			.getLogger(AndConjuctionExtractorVB.class.getName());

	private static Set<String> restrictedCapableOf = new HashSet<>();
	private static Dictionaries dictionary;

	static {
		dictionary = DictionariesInstance.getInstance();
		restrictedCapableOf.add("has");
		restrictedCapableOf.add("have");
		restrictedCapableOf.addAll(dictionary.copulas);
	}

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdDepId, String tdGovId,
			List<TypedDependency> listDependencies) {

		String tdDepTag = td.dep().tag();
		String tdDepLemma = td.dep().lemma();
		String tdGovTag = td.gov().tag();

		Clause govClause = null;
		if (tdGovTag.contains("VB")) {
			govClause = storySentence.getEvent(tdGovId);
		} else {
			govClause = storySentence.getDescription(tdGovId);
		}

		Event depEvent = storySentence.getEvent(tdDepId);
		if (depEvent == null) {
			depEvent = new Event(tdDepLemma);
		}

		for (Map.Entry<String, Noun> entry : govClause.getManyDoers()
				.entrySet()) {
			Noun doer = entry.getValue();
			if (!restrictedCapableOf.contains(tdDepLemma.toLowerCase())) {
				doer.addAttribute("CapableOf", tdDepLemma);
				log.debug(doer.getId() + " capable of " + tdDepLemma);
			}
			depEvent.getVerb().setPOS(tdDepTag);
			//find auxiliary
			List<TypedDependency> auxs = Extractor.findDependencies(td.dep(),
					"gov", "aux", listDependencies);
			for (TypedDependency t : auxs) {
				depEvent.getVerb().addAuxiliary(t.dep().lemma());
			}
			depEvent.addDoer(entry.getKey(), doer);
			depEvent.addConcept(cp.createConceptAsVerb(tdDepLemma));
			log.debug(tdDepId);
			storySentence.addEvent(tdDepId, depEvent);
		}

	}

}
