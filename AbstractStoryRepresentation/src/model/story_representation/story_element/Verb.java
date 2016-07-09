package model.story_representation.story_element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Verb {
	private String action;
	private String auxiliary;
	
	/** details separated into specific types */
	private Set<String> prepositionalPhrases;
	private Set<String> adverbs; 
	private Set<String> clausalComplements;

	public Set<String> getAdverbs() {
		return adverbs;
	}
	public Set<String> getPrepositionalPhrases(){
		return prepositionalPhrases;
	}
	public Set<String> getClausalComplements() {
		return clausalComplements;
	}
	public String getAuxiliary(){
		return auxiliary;
	}
	
	public Verb(String action) {
		this.action = action;
		this.auxiliary = "";
		this.prepositionalPhrases = new HashSet<String>();
		this.adverbs = new HashSet<String>();
		this.clausalComplements = new HashSet<String>();
	}
	
	
	public void addPrepositionalPhrase(String phrase){
		System.out.println(action + " : " + "adding phrase: " + phrase);
		prepositionalPhrases.add(phrase);
	}
	public void addAdverb(String adverb){
		System.out.println(action + " : " + "adding adv: " + adverb);
		adverbs.add(adverb);
	}
	public void addClausalComplement(String comp){
		System.out.println(action + " : " + "adding comp: " + comp);
		clausalComplements.add(comp);
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
