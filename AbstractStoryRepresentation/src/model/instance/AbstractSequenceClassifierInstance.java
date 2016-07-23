package model.instance;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

@SuppressWarnings("rawtypes")
public class AbstractSequenceClassifierInstance {

	private static AbstractSequenceClassifier abstractSequenceClassifier;

	static {
		abstractSequenceClassifier = CRFClassifier.getDefaultClassifier();
	}

	public static synchronized AbstractSequenceClassifier getInstance() {
		return abstractSequenceClassifier;
	}

}
