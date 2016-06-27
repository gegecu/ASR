package model.text_understanding;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.Conflict;
import model.story_representation.story_element.Resolution;
import model.story_representation.story_element.Verb;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;

public class TextUnderstanding {

	private static Logger log = Logger
			.getLogger(TextUnderstanding.class.getName());

	private Preprocessing preprocessingModule;
	private Extractor extractionModule;
	private AbstractStoryRepresentation asr;

	public TextUnderstanding(AbstractStoryRepresentation asr) {
		this.asr = asr;
		preprocessingModule = new Preprocessing();
		extractionModule = new Extractor(asr);
	}

	public void processInput(String text) {

		log.debug("Input of User : " + text);

		List<StorySentence> extractedStorySentences = extractionModule
				.extract(text, preprocessingModule.preprocess(text));

		for (StorySentence storySentence : extractedStorySentences) {
			asr.addEvent(storySentence);
		}

		if (asr.getCurrentPartOfStory().equals("start")) {
			Conflict conflict = null;
			for (StorySentence storySentence : extractedStorySentences) {
				conflict = checkForConflict(storySentence);
			}
			asr.setConflict(conflict);
		} else if (asr.getCurrentPartOfStory().equals("end")) {
			Resolution resolution = asr.getResolution();
			if (resolution == null) {
				for (StorySentence storySentence : extractedStorySentences) {
					resolution = checkForResolution(storySentence);
					if (resolution == null) {
						break;
					}
				}
			}
			asr.setResolution(resolution);
		}

		extractedStorySentences = null;

	}

	private Conflict checkForConflict(StorySentence storySentence) {

		Conflict conflict = asr.getConflict();

		StorySentence possibleConflict = storySentence;

		List<Clause> clauses = new ArrayList<Clause>();
		clauses.addAll(possibleConflict.getManyPredicates().values());
		clauses.addAll(possibleConflict.getManyDescriptions().values());

		for (Clause clause : clauses) {
			if (conflict == null) {
				if (clause.getPolarity() <= -0.2) {
					conflict = new Conflict(clause, null);
				}
			} else {
				if ((clause.getPolarity() < conflict.getPolarity())) {
					conflict = new Conflict(clause, null);
				}
			}
		}

		return conflict;

	}

	private Resolution checkForResolution(StorySentence storySentence) {

		Conflict conflict = asr.getConflict();
		Resolution resolution = asr.getResolution();

		StorySentence possibleResolution = storySentence;

		List<Clause> clauses = new ArrayList<Clause>();
		clauses.addAll(possibleResolution.getManyPredicates().values());
		clauses.addAll(possibleResolution.getManyDescriptions().values());

		List<Noun> doersInConflict = new ArrayList<Noun>();
		doersInConflict.addAll(conflict.getClause().getManyDoers().values());
		for (Clause clause : clauses) {
			if (hasValidResolutionConcept(conflict, clause)) {
				List<Noun> doersInResolution = new ArrayList<Noun>();
				doersInResolution.addAll(clause.getManyDoers().values());
				doersInResolution.retainAll(doersInConflict);
				if (doersInResolution.size() > 0) {
					resolution = new Resolution(clause);
					break;
				}
			}
		}

		return resolution;

	}

	private boolean hasValidResolutionConcept(Conflict conflict,
			Clause resolutionClause) {
		
		//if conflict is from negation, resolution should be un-negated statement
		if (conflict.isNegation()){
			System.out.println("conflict is negation");
			List<String> concepts = resolutionClause.getConcepts();
			//System.out.println("size:" +concepts.size());
			for (String resolutionConcept : concepts) {
				for (String conflictConcept : conflict.getClause().getConcepts()) {
					if (conflictConcept.equals(resolutionConcept) && !((Event) resolutionClause).getVerb().isNegated()) {
						System.out.println("Resolution check, conf:" + conflictConcept + " reso: " +resolutionConcept);
						return true;
					}
				}
			}
		}
		else { //else check for 4 hops in conceptnet
			List<String> concepts = resolutionClause.getConcepts();
			for (String concept : concepts) {
				for (String conflict2 : conflict.getClause().getConcepts()) {
					if (ConceptNetDAO.getFourHops(conflict2, concept)) {
						return true;
					}
				}
			}
		}
		return false;

	}

}
