package model.utility;

import java.util.List;

import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.story_element.story_sentence.Clause;

public class ResolutionFinder {

	public static String findExpectedResolutionConcept(Clause conflict) {

		String expectedResolutionConcept = null;

		if (conflict.getConcepts().isEmpty()) {
			return null;
		}

		for (String concept : conflict.getConcepts()) {

			List<String> path = ConceptNetDAO.getExpectedResolution(concept);

			if (!path.isEmpty()) {
				expectedResolutionConcept = path.get(path.size() - 1);
			}

		}

		return expectedResolutionConcept;

	}

}
