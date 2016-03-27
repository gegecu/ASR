package model.story_representation.noun;

import java.util.ArrayList;
import java.util.List;

import model.utility.States;

public class Character extends Noun{

	private List<String> states;
	
	public Character(String id) {
		super(id);
		states = new ArrayList();
	}

	public String getFirstConflictState() {
		for(String state: this.states) {
			if(States.CONFLICT_RESOLUTION.containsKey(state)) {
				return state;
			}
		}
		return null;
	}
	
	public String getCurrentState() {
		return this.states.get(this.states.size()-1);
	}

	public void addState(String state) {
		this.states.add(state);
	}
	
	public List<String> getStates() {
		return this.states;
	}

}
