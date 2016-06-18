package model.instance;

import java.util.concurrent.Semaphore;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

@SuppressWarnings("rawtypes")
public class AbstractSequenceClassifierInstance {

	private static Semaphore semaphore;
	private static AbstractSequenceClassifier abstractSequenceClassifier;

	static {
		semaphore = new Semaphore(0);
		abstractSequenceClassifier = CRFClassifier.getDefaultClassifier();
		semaphore.release(1);
	}

	public static synchronized AbstractSequenceClassifier getInstance() {
		try {
			semaphore.acquire();
			semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return abstractSequenceClassifier;
	}

}
