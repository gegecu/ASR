package model.text_understanding.extractors.negation;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;

public class NegationExtractorVB {

	private static Logger log = Logger
			.getLogger(NegationExtractorVB.class.getName());

	/**
	 * Processes the “neg” TypedDependency relation for verbs.
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

		String tdGovLemma = td.gov().lemma();

		if (!(tdGovLemma.equalsIgnoreCase("has")
				|| tdGovLemma.equalsIgnoreCase("have"))) {

			Event p = storySentence.getEvent(tdGovId);
			log.debug(tdGovId + " " + p);
			p.getConcepts().clear();
			p.setNegated(true);

			if (!p.getDirectObjects().isEmpty()) {
				for (Map.Entry<String, Noun> dobj : p.getDirectObjects()
						.entrySet()) {
					p.addConcept(cp.createNegationVerbWithDirectObject(
							tdGovLemma, dobj.getValue().getId()));
				}
			} else {
				p.addConcept(cp.createNegationVerb(tdGovLemma));
			}

			//went to China
			if (tdGovLemma.equals("go")) {

				Description d = storySentence.getDescription(tdGovId);
				d.setNegated(true);

				for (Map.Entry<String, Noun> doerEntry : p.getManyDoers()
						.entrySet()) {

					Noun doer = doerEntry.getValue();

					for (Map.Entry<String, Noun> location : p.getLocations()
							.entrySet()) {
						doer.getReference("AtLocation")
								.remove(location.getKey());
						d.getReference("AtLocation").remove(location.getKey());
						d.addReference("NotAtLocation", location.getKey(),
								location.getValue());
						d.addConcept(cp.createNegationVerbWithLocation(
								tdGovLemma, location.getValue().getId()));
					}

					if (doer.getReference("AtLocation").isEmpty()) {
						doer.getReferences().remove("AtLocation");
					}

				}

				if (d.getReference("AtLocation").isEmpty()) {
					d.getReferences().remove("AtLocation");
				}

			}

			storySentence.addEvent(tdGovId, p);

		} else {

			Description d = storySentence.getDescription(tdGovId);
			d.getConcepts().clear();
			d.setNegated(true);

			if (d.getReference("HasA") != null) {

				for (Map.Entry<String, Noun> possession : d.getReference("HasA")
						.entrySet()) {

					for (Map.Entry<String, Noun> doer : d.getManyDoers()
							.entrySet()) {

						doer.getValue().getReference("HasA")
								.remove(possession.getKey());
						if (doer.getValue().getReference("HasA").isEmpty()) {
							doer.getValue().getReferences().remove("HasA");
						}

						possession.getValue().getReference("IsOwnedBy")
								.remove(doer.getKey());
						if (possession.getValue().getReference("IsOwnedBy")
								.isEmpty()) {
							possession.getValue().getReferences()
									.remove("IsOwnedBy");
						}

						d.addReference("NotHasA", possession.getKey(),
								possession.getValue());

					}

					d.addConcept(cp.createNegationVerbWithDirectObject(
							tdGovLemma, possession.getValue().getId()));

				}

				for (Map.Entry<String, Noun> entry : d.getReference("NotHasA")
						.entrySet()) {
					d.getReference("HasA").remove(entry.getKey());
				}

				if (d.getReference("HasA") != null
						&& d.getReference("HasA").isEmpty()) {
					d.getReferences().remove("HasA");
				}

			}

			storySentence.addDescription(tdGovId, d);

		}

	}

}
