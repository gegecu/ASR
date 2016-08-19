package model.story_representation.story_element.noun;

public class Character extends Noun {

	/**
	 * Sets the noun type to character
	 * 
	 * @param id
	 *            the surface text of the noun to set
	 */
	public Character(String id) {
		super(id);
		this.type = TypeOfNoun.CHARACTER;
	}

}
