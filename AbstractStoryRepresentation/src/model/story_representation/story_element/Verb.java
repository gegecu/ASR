package model.story_representation.story_element;

public class Verb {
	private String action;
	private boolean negated;
	
	public Verb(String action) {
		this.action = action;
		this.negated = false;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean isNegated() {
		return negated;
	}
	public void setNegated(boolean negated) {
		this.negated = negated;
	}
	
	
}
