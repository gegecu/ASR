package model.instance;

import edu.stanford.nlp.dcoref.Dictionaries;

public class DictionariesInstance {

	private static Dictionaries dictionaries;

	static {
		dictionaries = new Dictionaries();
	}

	public static synchronized Dictionaries getInstance() {
		return dictionaries;
	}

}
