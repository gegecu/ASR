package model.story_representation.story_element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class Verb {

	private static Logger log = Logger
			.getLogger(Verb.class.getName());

	private String action;
	private String auxiliary;

	/** details separated into specific types */
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
	public String getAuxiliary() {
		return auxiliary;
	}

	public Verb(String action) {
		this.action = action;
		this.auxiliary = "";
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
	
	public void setAuxiliary(String aux) {
		this.auxiliary = aux;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

}
