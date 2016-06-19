package model.instance;

import java.util.concurrent.Semaphore;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

@SuppressWarnings("rawtypes")
public class AbstractSequenceClassifierInstance {

	private static Semaphore semaphore;
	private static AbstractSequenceClassifier abstractSequenceClassifier;
	private static boolean processed;

	static {
		processed = false;
		semaphore = new Semaphore(0);
		abstractSequenceClassifier = CRFClassifier.getDefaultClassifier();
		semaphore.release(1);
		processed = true;
	}

	public static synchronized AbstractSequenceClassifier getInstance() {
		if (!processed) {
			try {
				semaphore.acquire();
				semaphore.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return abstractSequenceClassifier;
	}

}
