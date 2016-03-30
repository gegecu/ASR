package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.story_representation.story_element.noun.Noun;

public class Description {
	private Map<String, Set<String>> attributes;
	private Map<String, Set<Noun>> references;
	
	public Description() {
		this.attributes = new HashMap<String, Set<String>>();
		this.references = new HashMap<String, Set<Noun>>();
	}
	
	public void addAttribute(String key, String attribute) {
		Set<String> temp = this.attributes.get(key);
		
		if(temp == null) {
			temp = new HashSet<String>();
		}
		temp.add(attribute);
		this.attributes.put(key, temp);	
	}
	
	public Map<String, Set<String>> getAttributes() {
		return this.attributes;
	}
	
	public List<String> getAttribute(String key) {
		if(attributes.get(key) != null) {
			return new ArrayList(attributes.get(key));
		}
		return null;
	}
	
	public void addReference(String key, Noun reference) {
		Set<Noun> temp = this.references.get(key);
		
		if(temp == null) {
			temp = new HashSet<Noun> ();
		}
		temp.add(reference);
		this.references.put(key, temp);	
	}
	
	public Map<String, Set<Noun>> getReferences() {
		return this.references;
	}
	
	public List<Noun> getReference(String key) {
		if(references.get(key) != null) {
			return new ArrayList(references.get(key));
		}
		return null;
	}
}
