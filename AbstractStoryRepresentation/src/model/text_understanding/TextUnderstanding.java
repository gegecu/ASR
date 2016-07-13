package model.text_understanding;

import java.util.ArrayList;
import java.util.List;

import model.instance.SenticNetParserInstance;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.senticnet.SenticNetParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.SpecialClause;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.StorySentence;

public class TextUnderstanding {

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

		extractedStorySentences = null;

	}

	private SpecialClause checkForConflict(StorySentence storySentence) {
		
		List<Clause> clauses = new ArrayList();
		clauses.addAll(storySentence.getManyDescriptions().values());
		clauses.addAll(storySentence.getManyPredicates().values());
		
		float worstPolarity = 0;
		float polarity = 0;
		String mainConcept = null;
		Clause mainClause = null;
		
		//compare from previously set
		if(asr.getConflict() != null) {
			if(asr.getConflict().getPolarity() < polarity) {
				polarity = asr.getConflict().getPolarity();
			}
		}

		for(Clause clause: clauses) {
		
			List<String> concepts = clause.getConcepts();
			
			if(concepts != null) {
				for(String concept: concepts) {
					if(concept.contains("not")) {
						polarity = snp.getPolarity(concept.replace(" ", "_")) * -1;
					}
					else {
						polarity = snp.getPolarity(concept.replace(" ", "_"));
					}
					
					if(polarity <= worstPolarity && polarity <= -0.2  && ConceptNetDAO.resolutionExists(concept)) {
						worstPolarity = polarity;
						mainConcept = concept;
						mainClause = clause;
					}
				}
			}
		}
		
		if(mainClause != null) {
			return new SpecialClause(mainClause, mainConcept, worstPolarity);
		}
		else {
			return null;
		}
	}

	private SpecialClause checkForResolution(StorySentence storySentence) {

		List<Clause> clauses = new ArrayList();
		clauses.addAll(storySentence.getManyDescriptions().values());
		clauses.addAll(storySentence.getManyPredicates().values());
		
		float bestPolarity = 0;
		float polarity = 0;
		String mainConcept = null;
		Clause mainClause = null;
		
		for(Clause clause: clauses) {
		
			List<String> concepts = clause.getConcepts();
		
			if(concepts != null) {
				
				for(String concept: concepts) {
					if(concept.contains("not")) {
						polarity = snp.getPolarity(concept.replace(" ", "_")) * -1;
					}
					else {
						polarity = snp.getPolarity(concept.replace(" ", "_"));
					}
					
					if(polarity > bestPolarity) {
						bestPolarity = polarity;
						mainConcept = concept;
						mainClause = clause;
					}
				}
			}
		}
		
		if(mainClause != null) {
			SpecialClause conflict = asr.getConflict();
			
			boolean hasValidResolution = this.hasValidResolutionConcept(conflict, mainClause);
			if(hasValidResolution) {
				
				List<Noun> doersInResolution = new ArrayList(mainClause.getManyDoers().values());
				List<Noun> doersInConflict = new ArrayList(conflict.getClause().getManyDoers().values());
				doersInResolution.retainAll(doersInConflict);
				
				if(doersInResolution.size() > 0) {
					return new SpecialClause(mainClause, mainConcept, bestPolarity);
				}
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
