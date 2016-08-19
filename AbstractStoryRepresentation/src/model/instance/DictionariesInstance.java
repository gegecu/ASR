package model.instance;

import edu.stanford.nlp.dcoref.Dictionaries;

/**
 * Singleton implementation of Stanford CoreNLP Dictionaries
 */
public class DictionariesInstance {

	/**
	 * The singleton Stanford CoreNLP Dictionaries instance
	 */
	private static Dictionaries dictionaries;

	static {
		dictionaries = new Dictionaries();
	}

	/**
	 * Returns the singleton Stanford CoreNLP Dictionaries instance
	 * 
	 * @return Stanford CoreNLP Dictionaries instance
	 */
	public static synchronized Dictionaries getInstance() {
		return dictionaries;
	}

}
