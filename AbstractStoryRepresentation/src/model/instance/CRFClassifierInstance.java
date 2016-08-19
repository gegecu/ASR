package model.instance;

import edu.stanford.nlp.ie.crf.CRFClassifier;

/**
 * Singleton implementation of Stanford CoreNLP CRFClassifier
 */
@SuppressWarnings("rawtypes")
public class CRFClassifierInstance {

	/**
	 * The singleton Stanford CoreNLP CRFClassifier instance
	 */
	private static CRFClassifier crfClassifier;

	static {
		crfClassifier = CRFClassifier.getDefaultClassifier();
	}

	/**
	 * Returns the singleton Stanford CoreNLP CRFClassifier instance
	 * 
	 * @return Stanford CoreNLP CRFClassifier instance
	 */
	public static synchronized CRFClassifier getInstance() {
		return crfClassifier;
	}

}
