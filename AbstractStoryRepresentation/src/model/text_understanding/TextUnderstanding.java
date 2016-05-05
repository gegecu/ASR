package model.text_understanding;

import model.story_representation.AbstractStoryRepresentation;

public class TextUnderstanding {

	private Preprocessing preprocessingModule;
	private Extractor extractionModule;
	private AbstractStoryRepresentation asr;

	public TextUnderstanding(AbstractStoryRepresentation asr) {
		this.asr = asr;
		preprocessingModule = new Preprocessing(asr);
		extractionModule = new Extractor(asr);
	}

	public void processInput(String text) {
		extractionModule.extract(text, preprocessingModule.preprocess(text));
	}

}
