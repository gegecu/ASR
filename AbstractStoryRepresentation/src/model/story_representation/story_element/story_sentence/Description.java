package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.story_representation.story_element.noun.Noun;

public class Description extends Clause{
	private Map<String, List<String>> attributes;
	private Map<String, List<Noun>> references;
	private List<String> concepts;
	
	public Description() {
		this.attributes = new HashMap<String, List<String>>();
		this.references = new HashMap<String, List<Noun>>();
		this.concepts = new ArrayList<String>();
		this.polarity = 0;
	}
	public void addConcept(String concept){
		this.concepts.add(concept);
	}
	public List<String> getConcepts(){
		if(this.concepts.isEmpty()){
			return null;
		}
		return this.concepts;
	}
	
	public void addAttribute(String key, String attribute) {
		List<String> temp = this.attributes.get(key);
		
		if(temp == null) {
			temp = new ArrayList<String>();
		}
		temp.remove(attribute);
		temp.add(attribute);
		this.attributes.put(key, temp);	
	}
	
	public Map<String, List<String>> getAttributes() {
		return this.attributes;
	}
	
	public List<String> getAttribute(String key) {
		if(attributes.get(key) != null) {
			return attributes.get(key);
		}
		return null;
	}
	
	public void addReference(String key, Noun reference) {
		List<Noun> temp = this.references.get(key);
		
		if(temp == null) {
			temp = new ArrayList<Noun>();
		}
		
		temp.remove(reference);
		temp.add(reference);
		this.references.put(key, temp);	
	}
	
	public Map<String, List<Noun>> getReferences() {
		return this.references;
	}
	
	public List<Noun> getReference(String key) {
		if(references.get(key) != null) {
			return references.get(key);
		}
		return null;
	}
}
