package model.instance;

import java.util.concurrent.Semaphore;

import edu.stanford.nlp.dcoref.Dictionaries;

public class DictionariesInstance {

	private static Semaphore semaphore;
	private static Dictionaries dictionaries;

	static {
		semaphore = new Semaphore(0);
		dictionaries = new Dictionaries();
		semaphore.release(1);
	}

	public static synchronized Dictionaries getInstance() {
		try {
			semaphore.acquire();
			semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return dictionaries;
	}

}
