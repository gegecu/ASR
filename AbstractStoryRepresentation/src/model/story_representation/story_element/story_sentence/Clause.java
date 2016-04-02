package model.story_representation.story_element.story_sentence;

import java.util.List;

public abstract class Clause {

	protected float polarity;
	
	public void setPolarity(float polarity) {
		this.polarity = polarity;
	}

	public float getPolarity() {
		return this.polarity;
	}
	public abstract List<String> getConcepts();
	
}
