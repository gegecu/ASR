package model.story_representation.story_element.noun;

public class Location extends Noun {

	/**
	 * Sets the noun type to location
	 * 
	 * @param id
	 *            the surface text of the noun to set
	 */
	public Location(String id) {
		super(id);
		this.type = TypeOfNoun.LOCATION;
	}

}