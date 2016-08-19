package model.story_representation.story_element.noun;

public class Object extends Noun {

	/**
	 * Sets the noun type to object
	 * 
	 * @param id
	 *            the surface text of the noun to set
	 */
	public Object(String id) {
		super(id);
		this.type = TypeOfNoun.OBJECT;
	}

}
