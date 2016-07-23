package model.story_representation.story_element.noun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Noun {

	public enum TypeOfNoun {
		CHARACTER, LOCATION, OBJECT, UNKNOWN;
	}

	protected TypeOfNoun type;
	protected String id;
	protected boolean isCommon;
	protected Map<String, List<String>> attributes;
	protected Map<String, Map<String, Noun>> references;

	public Noun(String id) {
		this.id = id;
		this.attributes = new HashMap<String, List<String>>();
		this.references = new HashMap<String, Map<String, Noun>>();
		this.isCommon = true;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void addAttribute(String key, String attribute) {

		List<String> temp = this.attributes.get(key);
		if (temp == null) {
			temp = new ArrayList<String>();
			this.attributes.put(key, temp);
		}
		temp.remove(attribute);
		temp.add(attribute);

	}

	public Map<String, List<String>> getAttributes() {
		return this.attributes;
	}

	public List<String> getAttribute(String key) {
		return attributes.get(key);
	}

	public void addReference(String key, String nounId, Noun reference) {

		Map<String, Noun> temp = this.references.get(key);
		if (temp == null) {
			temp = new HashMap<>();
			this.references.put(key, temp);
		}
		temp.remove(nounId);
		temp.put(nounId, reference);

	}

	public Map<String, Map<String, Noun>> getReferences() {
		return this.references;
	}

	public Map<String, Noun> getReference(String key) {
		return references.get(key);
	}

	public void setProper() {
		this.isCommon = false;
	}

	public boolean getIsCommon() {
		return this.isCommon;
	}

	public TypeOfNoun getType() {
		return type;
	}

}
