package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.story_representation.story_element.noun.Noun;

/**
 * Used to stores the descriptive clause information
 */
public class Description extends Clause {

	/**
	 * The attributes extracted
	 */
	private Map<String, List<String>> attributes;
	/**
	 * The nouns extracted
	 */
	private Map<String, Map<String, Noun>> references;

	/**
	 * initialize the variablesS
	 */
	public Description() {
		this.attributes = new HashMap<String, List<String>>();
		this.references = new HashMap<String, Map<String, Noun>>();
	}

	/**
	 * Adds the attribute to the list of attributes using the key param as key
	 * 
	 * @param key
	 *            the key to use
	 * @param attribute
	 *            the attribute to add
	 */
	public void addAttribute(String key, String attribute) {

		List<String> temp = this.attributes.get(key);

		if (temp == null) {
			temp = new ArrayList<String>();
		}

		temp.remove(attribute);
		temp.add(attribute);
		this.attributes.put(key, temp);

	}

	/**
	 * @return the attributes
	 */
	public Map<String, List<String>> getAttributes() {
		return this.attributes;
	}

	/**
	 * Returns list of attributes using the key param as key
	 * 
	 * @param key
	 *            the key to use
	 * @return list of attributes using the key param as key
	 */
	public List<String> getAttribute(String key) {
		return this.attributes.get(key);
	}

	/**
	 * Adds the noun to the references map of map using the key param as key
	 * 
	 * @param key
	 *            the key to use
	 * @param nounId
	 *            the nounId to add
	 * @param reference
	 *            the noun object to add
	 */
	public void addReference(String key, String nounId, Noun reference) {

		Map<String, Noun> temp = this.references.get(key);

		if (temp == null) {
			temp = new HashMap<>();
		}

		temp.remove(nounId);
		temp.put(nounId, reference);
		this.references.put(key, temp);

	}

	/**
	 * @return the references
	 */
	public Map<String, Map<String, Noun>> getReferences() {
		return this.references;
	}

	/**
	 * Returns the map of noun references using the key param as key
	 * 
	 * @param key
	 *            the key to use
	 * @return the map of noun references
	 */
	public Map<String, Noun> getReference(String key) {
		return this.references.get(key);
	}

}
