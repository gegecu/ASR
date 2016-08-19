package model.story_representation.story_element.noun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the noun related extracted information
 */
public abstract class Noun {

	/**
	 * Enum for types of nouns
	 */
	public enum TypeOfNoun {
		CHARACTER, LOCATION, OBJECT, UNKNOWN;
	}

	/**
	 * The type of noun
	 */
	protected TypeOfNoun type;
	/**
	 * The surface text of the noun
	 */
	protected String id;
	/**
	 * Set to know if the Noun is a common noun or a proper noun. True for
	 * common, false for proper.
	 */
	protected boolean isCommon;
	/**
	 * List of strings that describes the Noun that is divided according to the
	 * assertion of the Noun to the string, having their assertions as the key
	 * (e.g. CapableOf, HasProperty). <br>
	 * <br>
	 * The strings are not nouns.
	 */
	protected Map<String, List<String>> attributes;
	/**
	 * Map of nouns that has a assertion to the Noun that is divided according
	 * to the assertion of the Noun(class) to the nouns(map), having their
	 * assertion as the key (e.g. IsA, HasA, AtLocation).
	 */
	protected Map<String, Map<String, Noun>> references;

	/**
	 * initializes the variables
	 * 
	 * @param id
	 *            the surface text of the noun to set
	 */
	public Noun(String id) {
		this.id = id;
		this.attributes = new HashMap<String, List<String>>();
		this.references = new HashMap<String, Map<String, Noun>>();
		this.isCommon = true;
	}

	/**
	 * @param id
	 *            the surface text to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Adds the attribute to the list of attributes mapped by the key
	 * 
	 * @param key
	 *            the key to set
	 * @param attribute
	 *            the attribute to add
	 */
	public void addAttribute(String key, String attribute) {

		List<String> temp = this.attributes.get(key);
		if (temp == null) {
			temp = new ArrayList<String>();
			this.attributes.put(key, temp);
		}
		temp.remove(attribute);
		temp.add(attribute);

	}

	/**
	 * @return the attributes
	 */
	public Map<String, List<String>> getAttributes() {
		return this.attributes;
	}

	/**
	 * Returns list of strings from attributes map using the key param as the
	 * key
	 * 
	 * @param key
	 *            the key to filter
	 * @return list of strings from attributes map using the key param as the
	 *         key
	 */
	public List<String> getAttribute(String key) {
		return attributes.get(key);
	}

	/**
	 * Adds the noun, reference to the map of map of references mapped by the
	 * key
	 * 
	 * @param key
	 *            the key to use
	 * @param nounId
	 *            the nounId to add
	 * @param reference
	 *            the reference to add
	 */
	public void addReference(String key, String nounId, Noun reference) {

		Map<String, Noun> temp = this.references.get(key);
		if (temp == null) {
			temp = new HashMap<>();
			this.references.put(key, temp);
		}
		temp.remove(nounId);
		temp.put(nounId, reference);

	}

	/**
	 * @return the references
	 */
	public Map<String, Map<String, Noun>> getReferences() {
		return this.references;
	}

	/**
	 * Returns map of nouns from references map using the key param as the key
	 * 
	 * @param key
	 *            the key to filter
	 * @return map of nouns from references map using the key param as the key
	 */
	public Map<String, Noun> getReference(String key) {
		return references.get(key);
	}

	/**
	 * Sets the isCommon to true
	 */
	public void setProper() {
		this.isCommon = false;
	}

	/**
	 * @return the isCommon
	 */
	public boolean getIsCommon() {
		return this.isCommon;
	}

	/**
	 * @return the type
	 */
	public TypeOfNoun getType() {
		return type;
	}

}
