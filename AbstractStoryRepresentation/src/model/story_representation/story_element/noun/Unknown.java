package model.story_representation.story_element.noun;

public class Unknown extends Noun {

	/**
	 * Sets the noun type to unknown
	 * 
	 * @param id
	 *            the surface text of the noun to set
	 */
	public Unknown(String id) {
		super(id);
		this.type = TypeOfNoun.UNKNOWN;
	}

}
