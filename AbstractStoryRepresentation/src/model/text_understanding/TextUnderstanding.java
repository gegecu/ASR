package model.text_understanding;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import model.instance.SenticNetParserInstance;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.senticnet.SenticNetParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.SpecialClause;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Clause;
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
			asr.addEvent(storySentence);
		}

		if (asr.getCurrentPartOfStory().equals("start")) {
			SpecialClause conflict = null;
			for (StorySentence storySentence : extractedStorySentences) {
				conflict = checkForConflict(storySentence);
			}
			asr.setConflict(conflict);
		} else if (asr.getCurrentPartOfStory().equals("end")) {
			SpecialClause resolution = asr.getResolution();
			if (resolution == null) {
				for (StorySentence storySentence : extractedStorySentences) {
					resolution = checkForResolution(storySentence);
					//					System.out.println(resolution.getMainConcept());
					//					if (resolution == null) {
					//						break;
					//					}
				}
			}
			asr.setResolution(resolution);
		}
		
		
		if(asr.getConflict() != null) {
			log.debug(asr.getConflict().getMainConcept() + ", " + asr.getConflict().getPolarity());
		}
		
		if(asr.getResolution() != null) {
			log.debug(asr.getResolution().getMainConcept() + ", " + asr.getResolution().getPolarity());
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

			List<String> concepts = clause.getConcepts();

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
							&& (ConceptNetDAO.resolutionExists(concept) || clause.isNegated())) {
						System.out.println(ConceptNetDAO.resolutionExists(concept));
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

			if(!conflict.getClause().isNegated()) {
				List<String> concepts = clause.getConcepts();
	
				if (concepts != null) {
	
					for (String concept : concepts) {
						if (concept.contains("not")) {
							polarity = snp.getPolarity(concept.replace(" ", "_"))
									* -1;
						} else {
							polarity = snp.getPolarity(concept.replace(" ", "_"));
						}
	
						if (polarity > bestPolarity && this.hasValidResolutionConcept(conflict, clause)) {
							bestPolarity = polarity;
							mainConcept = concept;
							mainClause = clause;
						}
					}
				}
			}
			else {
				if(this.hasValidResolutionConcept(conflict, clause)) {
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
				return new SpecialClause(mainClause, mainConcept,
						bestPolarity);
			}
		}
		return null;

	}

	private boolean hasValidResolutionConcept(SpecialClause conflict,
			Clause resolutionClause) {

		//if conflict is from negation, resolution should be un-negated statement
		if (conflict.getClause().isNegated()) {
			List<String> concepts = resolutionClause.getConcepts();
			//if(resolutionClause.isNegated())
			for (String resolutionConcept : concepts) {
				for (String conflictConcept : conflict.getClause()
						.getConcepts()) {
					String temp = conflictConcept.replace("not ", "");
					if (resolutionConcept.equals(temp)) {
						return true;
					}
				}
			}
		} else { //else check for 4 hops in conceptnet
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
