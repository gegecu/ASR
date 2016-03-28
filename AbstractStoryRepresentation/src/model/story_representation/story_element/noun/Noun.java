package model.story_representation.story_element.noun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import model.story_representation.story_element.StoryElement;
import model.utility.States;

public abstract class Noun extends StoryElement{
	protected String id;
	protected boolean isCommon;
	protected Map<String, Set<String>> attributes;
	protected Map<String, Set<Noun>> references;
	protected Stack<String> states;
	
	public Noun(String id) {
		this.id = id;
		this.attributes = new HashMap<String, Set<String>>();
		this.references = new HashMap<String, Set<Noun>>();
		this.isCommon = true;
		states = new Stack();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
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
	
	public void setProper() {
		this.isCommon = false;
	}
	
	public boolean getIsCommon() {
		return this.isCommon;
	}
	
	public void setState(String state) {
		if(States.CONFLICT_RESOLUTION.containsKey(state))
			states.push(state);
		else {
			String temp = states.peek();
			if(temp != null && States.CONFLICT_RESOLUTION.get(temp).equals(state)) {
				states.pop();
			}
		}
	}
	
	public Stack getStates() {
		return this.states;
	}

	public boolean hasConflict() {
		return !this.states.isEmpty();
	}
}

