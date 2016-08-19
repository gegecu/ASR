package model.story_representation.story_element.story_sentence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import model.knowledge_base.data.DataDAO;

/**
 * Used to store extracted verb information
 */
public class Verb {

	private static Logger log = Logger.getLogger(Verb.class.getName());

	/**
	 * list of future tense auxiliary verbs
	 */
	private static final String[] futureTenseAuxs = DataDAO
			.getData("futureTenseAuxs");

	/**
	 * list of present tense auxiliary verbs, includes present progressive
	 */
	private static final String[] presentTenseAuxs = DataDAO
			.getData("presentTenseAuxs");

	/**
	 * list of past tense auxiliary verbs, inclues past participle
	 */
	private static final String[] pastTenseAuxs = DataDAO
			.getData("pastTenseAuxs");

	/**
	 * the verb
	 */
	private String action;
	/**
	 * the part of speech of the verb
	 */
	private String pos;
	/**
	 * the form of the verb
	 */
	private String form;
	/**
	 * the tense of the verb
	 */
	private String tense;

	/** details separated into specific types */

	/**
	 * The auxiliaries of the verb
	 */
	private Set<String> auxiliary;
	/**
	 * The prepositional phrases with the verb
	 */
	private Set<String> prepositionalPhrases;
	/**
	 * The adverbs of the verb
	 */
	private Set<String> adverbs;
	/**
	 * The clausal complements of the verb
	 */
	private Set<String> clausalComplements;

	/**
	 * initialize the variables
	 * 
	 * @param action
	 *            the action to set
	 */
	public Verb(String action) {
		this.action = action;
		//		this.negated = false;
		this.form = "base";
		this.pos = "VB";
		this.tense = "past";
		this.auxiliary = new HashSet<String>();
		this.prepositionalPhrases = new HashSet<String>();
		this.adverbs = new HashSet<String>();
		this.clausalComplements = new HashSet<String>();
	}

	/**
	 * @return the adverbs in list form
	 */
	public ArrayList<String> getAdverbs() {
		ArrayList<String> list = new ArrayList<>();
		list.addAll(adverbs);
		return list;
	}

	/**
	 * @return the prepositionalPhrases in list form
	 */
	public ArrayList<String> getPrepositionalPhrases() {
		ArrayList<String> list = new ArrayList<>();
		list.addAll(prepositionalPhrases);
		return list;
	}

	/**
	 * @return the clausalComplements in list form
	 */
	public ArrayList<String> getClausalComplements() {
		ArrayList<String> list = new ArrayList<>();
		list.addAll(clausalComplements);
		return list;
	}

	/**
	 * @return the auxiliary
	 */
	public Set<String> getAuxiliary() {
		return auxiliary;
	}

	/**
	 * @param phrase
	 *            the phrase to add to prepositionalPhrases
	 */
	public void addPrepositionalPhrase(String phrase) {
		log.debug(action + " : " + "adding phrase: " + phrase);
		prepositionalPhrases.add(phrase);
	}

	/**
	 * @param adverb
	 *            the adverb to add to adverbs
	 */
	public void addAdverb(String adverb) {
		log.debug(action + " : " + "adding adv: " + adverb);
		adverbs.add(adverb);
	}

	/**
	 * @param comp
	 *            the complement to add to clausalComplements
	 */
	public void addClausalComplement(String comp) {
		log.debug(action + " : " + "adding comp: " + comp);
		clausalComplements.add(comp);
	}

	/**
	 * @param aux
	 *            the auxiliary to add to auxiliary
	 */
	public void addAuxiliary(String aux) {
		this.auxiliary.add(aux);
	}

	/**
	 * @return returns true if form of verb is gerund
	 */
	public boolean isProgressive() {
		if (form.equals("gerund"))
			return true;
		return false;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @param pos
	 *            the part of speech to set
	 */
	public void setPOS(String pos) {
		this.pos = pos;
	}

	/**
	 * @param form
	 *            the form to set
	 */
	public void setForm(String form) {
		this.form = form;
	}

	/**
	 * @return the tense
	 */
	public String getTense() {
		evaluateVerbForm();
		return this.tense;
	}

	/**
	 * Evaluate the verb
	 */
	private void evaluateVerbForm() {
		if (pos.equals("VBG")) {
			form = "gerund";
			evaluateTense();
		} else if (pos.equals("VBD")) {
			form = "past";
			tense = "past";
		} else if (pos.equals("VBZ")) {
			form = "present";
			tense = "present";
		} else {
			form = "base";
			evaluateTense();
		}
	}

	/**
	 * Evaluate the tense of the verb
	 */
	private void evaluateTense() {

		for (String aux : auxiliary) {
			if (Arrays.asList(presentTenseAuxs).contains(aux)) {
				//System.out.println("present: " + aux);
				tense = "present";
				break;
			}
		}
		for (String aux : auxiliary) {
			if (Arrays.asList(pastTenseAuxs).contains(aux)) {
				//System.out.println("past: " + aux);
				tense = "past";
				break;
			}
		}
		for (String aux : auxiliary) {
			if (Arrays.asList(futureTenseAuxs).contains(aux)) {
				//System.out.println("future: " + aux);
				tense = "future";
			}
		}
	}

}
