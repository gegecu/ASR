package model.text_understanding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import model.instance.SenticNetParserInstance;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.senticnet.SenticNetParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.SpecialClause;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;

public class TextUnderstanding {

	private static Logger log = Logger
			.getLogger(TextUnderstanding.class.getName());

	private static final double conflictMinimumPolarity = -0.2;

	private Preprocessing preprocessingModule;
	private Extractor extractionModule;
	private AbstractStoryRepresentation asr;
	private SenticNetParser snp;

	public TextUnderstanding(AbstractStoryRepresentation asr) {
		this.asr = asr;
		preprocessingModule = new Preprocessing();
		extractionModule = new Extractor(asr);
		snp = SenticNetParserInstance.getInstance();
	}

	public void processInput(String text) {

		preprocessingModule.preprocess(text);

		List<StorySentence> extractedStorySentences = extractionModule.extract(
				preprocessingModule.getUpdatedString(),
				preprocessingModule.getCoref());

		for (StorySentence storySentence : extractedStorySentences) {
			asr.addStorySentence(storySentence);
		}

		if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.start)) {
			for (StorySentence storySentence : extractedStorySentences) {
				SpecialClause conflict = checkForConflict(storySentence);
				if (conflict != null) {
					asr.setConflict(conflict);
				}
			}
		} else if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.end)) {
			SpecialClause resolution = asr.getResolution();
			if (resolution == null) {
				for (StorySentence storySentence : extractedStorySentences) {
					resolution = checkForResolution(storySentence);
				}
			}
			asr.setResolution(resolution);
		}

		if (asr.getConflict() != null) {
			log.debug(asr.getConflict().getMainConcept() + ", "
					+ asr.getConflict().getPolarity());
		}

		if (asr.getResolution() != null) {
			log.debug(asr.getResolution().getMainConcept() + ", "
					+ asr.getResolution().getPolarity());
		}

		extractedStorySentences = null;

	}

	private SpecialClause checkForConflict(StorySentence storySentence) {

		List<Clause> clauses = new ArrayList<>();
		clauses.addAll(storySentence.getManyDescriptions().values());
		clauses.addAll(storySentence.getManyEvents().values());

		float worstPolarity = 0;
		float polarity = 0;
		String mainConcept = null;
		Clause mainClause = null;

		//compare from previously set
		if (asr.getConflict() != null) {
			if (asr.getConflict().getPolarity() < polarity) {
				polarity = asr.getConflict().getPolarity();
			}
		}

		for (Clause clause : clauses) {

			Set<String> concepts = clause.getConcepts();

			if (concepts != null) {
				for (String concept : concepts) {
					if (concept.contains("not")) {
						String temp = concept.replace("not ", "");
						temp = temp.replace(" ", "_");
						polarity = snp.getPolarity(temp) * -1;
					} else {
						polarity = snp.getPolarity(concept.replace(" ", "_"));
					}

					if (polarity <= worstPolarity
							&& polarity <= conflictMinimumPolarity
							&& (ConceptNetDAO.checkResolutionExists(concept)
									|| clause.isNegated())) {
						System.out.println(
								ConceptNetDAO.checkResolutionExists(concept));
						System.out.println(polarity + ", " + concept);

						worstPolarity = polarity;
						mainConcept = concept;
						mainClause = clause;
					}
				}
			}
		}

		if (mainClause != null) {
			return new SpecialClause(mainClause, mainConcept, worstPolarity);
		} else {
			return null;
		}
	}

	private SpecialClause checkForResolution(StorySentence storySentence) {

		List<Clause> clauses = new ArrayList<>();
		clauses.addAll(storySentence.getManyDescriptions().values());
		clauses.addAll(storySentence.getManyEvents().values());

		float bestPolarity = 0;
		float polarity = 0;
		String mainConcept = null;
		Clause mainClause = null;
		SpecialClause conflict = asr.getConflict();

		for (Clause clause : clauses) {

			System.out.println(conflict.getClause());

			if (!conflict.getClause().isNegated()) {
				Set<String> concepts = clause.getConcepts();

				if (concepts != null) {

					for (String concept : concepts) {
						if (concept.contains("not")) {
							polarity = snp.getPolarity(
									concept.replace(" ", "_")) * -1;
						} else {
							polarity = snp
									.getPolarity(concept.replace(" ", "_"));
						}

						if (polarity > bestPolarity && this
								.hasValidResolutionConcept(conflict, clause)) {
							bestPolarity = polarity;
							mainConcept = concept;
							mainClause = clause;
						}
					}
				}
			} else {
				if (this.hasValidResolutionConcept(conflict, clause)) {
					bestPolarity = conflict.getPolarity() * -1;
					mainConcept = conflict.getMainConcept().replace("not ", "");
					mainClause = clause;
					break;
				}
			}
		}

		if (mainClause != null) {

			List<Noun> doersInResolution = new ArrayList<>(
					mainClause.getManyDoers().values());
			List<Noun> doersInConflict = new ArrayList<>(
					conflict.getClause().getManyDoers().values());
			doersInResolution.retainAll(doersInConflict);

			if (doersInResolution.size() > 0) {
				return new SpecialClause(mainClause, mainConcept, bestPolarity);
			}
		}
		return null;

	}

	private boolean hasValidResolutionConcept(SpecialClause conflict,
			Clause resolutionClause) {

		//if conflict is from negation, resolution should be un-negated statement
		if (conflict.getClause().isNegated()) {
			Set<String> resoConcepts = resolutionClause.getConcepts();
			if(resolutionClause.isNegated()){ //may change to isNegative instead (e.g. hates fighting or hates lying can be a resolution)
				return false;
			}
			Set<String> conflictConcepts = conflict.getClause().getConcepts();
			if(conflict.getClause() instanceof Event){
				//System.out.println("main verb to remove" + ((Event) conflict.getClause()).getVerb().getAction());
				String emotionVerb = ((Event) conflict.getClause()).getVerb().getAction();
				Iterator<String> it = conflictConcepts.iterator();
				while(it.hasNext()){
					String concept = it.next();
					if(concept.startsWith(emotionVerb)){
						it.remove();
					}
				}
			}
			//all conflict concepts must be found in the resolution
			for (String conflictConcept : conflictConcepts) {
				Boolean hasMatch = false;
				for (String resolutionConcept : resoConcepts) {
					String temp = conflictConcept.replace("not ", "");
					System.out.println("checking-" + temp);
					if (resolutionConcept.equals(temp)) {
						hasMatch = true;
						break;
					}
				}
				if(!hasMatch){
					return false;
				}
			}
			//if all concepts in conflict matched
			return true;
		} 
		else { //else check for 4 hops in conceptnet
			Set<String> concepts = resolutionClause.getConcepts();
			for (String resoConcept : concepts) {
				String confConcept = conflict.getMainConcept();
				if (ConceptNetDAO.checkFourHops(confConcept, resoConcept)) {
					return true;
				}
			}
		}
		return false;

	}

}
