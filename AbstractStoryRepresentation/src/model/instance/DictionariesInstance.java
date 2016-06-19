package model.instance;

import java.util.concurrent.Semaphore;

import edu.stanford.nlp.dcoref.Dictionaries;

public class DictionariesInstance {

	private static Semaphore semaphore;
	private static Dictionaries dictionaries;
	private static boolean processed;

	static {
		processed = false;
		semaphore = new Semaphore(0);
		dictionaries = new Dictionaries();
		semaphore.release(1);
		processed = true;
	}

	public static synchronized Dictionaries getInstance() {
		if (!processed) {
			try {
				semaphore.acquire();
				semaphore.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return dictionaries;
	}

}
