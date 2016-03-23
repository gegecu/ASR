package model.text_understanding;

import java.util.Properties;

import model.story_representation.AbstractStoryRepresentation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class TextUnderstanding {
	private Preprocessing preprocessingModule;
	private Extractor extractionModule;

	public TextUnderstanding() {
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		preprocessingModule = new Preprocessing(props, pipeline);
		extractionModule = new Extractor(props, pipeline);
	}
	
	public void processInput(String text, AbstractStoryRepresentation asr) {
		extractionModule.extract(preprocessingModule.preprocess(text), asr);
	}
}

