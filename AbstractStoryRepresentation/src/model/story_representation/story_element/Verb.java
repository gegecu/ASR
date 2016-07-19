package model.story_representation.story_element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class Verb {

	private static Logger log = Logger
			.getLogger(Verb.class.getName());

	private String[] futureTenseAuxs = {
			"will","must","shall","should","would","can","could", "need", "ought", "may", "might"
	};
	private String[] presentTenseAuxs = {//includes progressive
			"am", "are", "is", "do", "does" 
	};
	private String[] pastTenseAuxs = {//past participle considered as past
			"was", "were", "did", "had", "have", "has", "having", "been"
	};
	private String action;
	private String pos;
	private String form;
	private String tense;
	private Boolean negated;
	/** details separated into specific types */
	private Set<String> auxiliary;
	private Set<String> prepositionalPhrases;
	private Set<String> adverbs;
	private Set<String> clausalComplements;

	public ArrayList<String> getAdverbs() {
		ArrayList<String> list = new ArrayList();
		list.addAll(adverbs);
		return list;
	}
	public ArrayList<String> getPrepositionalPhrases() {
		ArrayList<String> list = new ArrayList();
		list.addAll(prepositionalPhrases);
		return list;
	}
	public ArrayList<String> getClausalComplements() {
		ArrayList<String> list = new ArrayList();
		list.addAll(clausalComplements);
		return list;
	}
	public Set<String> getAuxiliary() {
		return auxiliary;
	}

	public Verb(String action) {
		this.action = action;
		this.negated = false;
		this.form = "base";
		this.pos = "VB";
		this.tense = "past";
		this.auxiliary = new HashSet<String>();
		this.prepositionalPhrases = new HashSet<String>();
		this.adverbs = new HashSet<String>();
		this.clausalComplements = new HashSet<String>();
	}
	
	public void addPrepositionalPhrase(String phrase) {
		log.debug(action + " : " + "adding phrase: " + phrase);
		prepositionalPhrases.add(phrase);
	}
	
	public void addAdverb(String adverb) {
		log.debug(action + " : " + "adding adv: " + adverb);
		adverbs.add(adverb);
	}
	public void addClausalComplement(String comp) {
		log.debug(action + " : " + "adding comp: " + comp);
		clausalComplements.add(comp);
	}
	
	public void addAuxiliary(String aux) {
		this.auxiliary.add(aux);
	}
	public boolean isProgressive(){
		if(form.equals("gerund"))
			return true;
		return false;
	}
	public boolean isNegated(){
		return this.negated;
	}
	public void setNegated(Boolean b){
		this.negated = b;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public void setPOS(String pos){
		this.pos = pos;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public String getTense() {
		getVerbForm();
		//System.out.println("form/tense: " + form + ", " + tense);
		return this.tense;
	}
	
	private void getVerbForm(){
		if(pos.equals("VBG")){
			form = "gerund";
			evaluateTense();
		}
		else if(pos.equals("VBD")){
			form = "past";
			tense = "past";
		}
		else if(pos.equals("VBZ")){
			form = "present";
			tense = "present";
		}
		else{
			form = "base";
			evaluateTense();
		}
	}
	
	private void evaluateTense(){
		
		for(String aux: auxiliary){
			if(Arrays.asList(presentTenseAuxs).contains(aux)){
				//System.out.println("present: " + aux);
				tense = "present";
				break;
			}
		}
		for(String aux: auxiliary){
			if(Arrays.asList(pastTenseAuxs).contains(aux)){
				//System.out.println("past: " + aux);
				tense = "past";
				break;
			}
		}
		for(String aux: auxiliary){
			if(Arrays.asList(futureTenseAuxs).contains(aux)){
				//System.out.println("future: " + aux);
				tense = "future";
			}
		}
	}

}
