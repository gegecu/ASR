package model.story_representation.story_element.noun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import model.utility.States;

public abstract class Noun {
	protected String id;
	protected boolean isCommon;
	protected Map<String, List<String>> attributes;
	protected Map<String, List<Noun>> references;
//	protected Stack<String> states;
	
	public Noun(String id) {
		this.id = id;
		this.attributes = new HashMap<String, List<String>>();
		this.references = new HashMap<String, List<Noun>>();
		this.isCommon = true;
		//states = new Stack();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
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
			temp = new ArrayList<Noun> ();
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
	
	public void setProper() {
		this.isCommon = false;
	}
	
	public boolean getIsCommon() {
		return this.isCommon;
	}
//	
//	public void setState(String state) {
//		if(States.CONFLICT_RESOLUTION.containsKey(state))
//			states.push(state);
//		else {
//			if(!this.states.isEmpty()) {
//				String temp = states.peek();
//				if(temp != null && States.CONFLICT_RESOLUTION.get(temp).equals(state)) {
//					states.pop();
//				}
//			}
//		}
//	}
//	
//	public Stack getStates() {
//		return this.states;
//	}
//
//	public boolean hasConflict() {
//		return !this.states.isEmpty();
//	}
}

