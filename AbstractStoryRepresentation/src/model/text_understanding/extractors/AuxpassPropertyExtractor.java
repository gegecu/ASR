package model.text_understanding.extractors;

import java.util.List;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.TypedDependency;
import model.knowledge_base.senticnet.ConceptParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_understanding.Extractor;

public class AuxpassPropertyExtractor {

	private static Logger log = Logger
			.getLogger(AuxpassPropertyExtractor.class.getName());

	public static void extract(AbstractStoryRepresentation asr,
			ConceptParser cp, TypedDependency td, StorySentence storySentence,
			String tdGovId, List<TypedDependency> list) {

		String tdDepLemma = td.dep().lemma();
		String tdGovLemma = td.gov().lemma();

		Event event = storySentence.getEvent(tdGovId);
		
		if (event == null) {
			event = new Event(tdGovLemma);
			storySentence.addEvent(tdGovId, event);
		}
		event.addConcept(tdDepLemma + " " + tdGovLemma);

		event.addConcept(cp.createConceptAsVerb(tdGovLemma));
		List<TypedDependency> subj = Extractor.findDependencies(td.gov(), "gov", "nsubjpass", list);
		for(TypedDependency t: subj){
			if(t.dep().tag().equals("NNP")){
				event.addConcept(
						cp.createConceptWithDirectObject(tdGovLemma, "someone"));
			}
			else{
				event.addConcept(
						cp.createConceptWithDirectObject(tdGovLemma, "something"));
			}
		}
		

	}

}
