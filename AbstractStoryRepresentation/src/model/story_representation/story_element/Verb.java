package model.story_representation.story_element;

import java.util.ArrayList;
import java.util.List;

public class Verb {
	private String action;
	private String auxiliary;
	private List<String> details; //in format can function as direct object
	
	public Verb(String action) {
		this.action = action;
		this.auxiliary = "";
		this.details = new ArrayList<String>();
	}
	
	public String getAuxiliary(){
		return auxiliary;
	}
	public List<String> getDetails(){
		return details;
	}
	public void addDetail(String detail){
		details.add(detail);
	}
	public void setAuxiliary(String aux){
		this.auxiliary = aux;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
}
