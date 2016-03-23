package model.story_representation.story_sentence;

import java.util.HashMap;
import java.util.Map;

import model.story_representation.noun.Noun;

public class State extends StorySentence{
	
	private Map<String, Noun> subjects;
	private String state;
	
	public State() {
		super();
		subjects = new HashMap();
		state = null;
	}
	
	public void addSubject(String id, Noun noun) {
		this.subjects.put(id, noun);
	}
	
	public Noun getSubject(String id) {
		return this.subjects.get(id);
	}
	
	public Map<String, Noun> getManySubjects() {
		return this.subjects;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}
	
	
}
