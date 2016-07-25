package model.instance;

import edu.stanford.nlp.ie.crf.CRFClassifier;

@SuppressWarnings("rawtypes")
public class CRFClassifierInstance {

	private static CRFClassifier crfClassifier;

	static {
		crfClassifier = CRFClassifier.getDefaultClassifier();
	}

	public static synchronized CRFClassifier getInstance() {
		return crfClassifier;
	}

}
