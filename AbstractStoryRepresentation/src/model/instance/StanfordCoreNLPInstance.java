package model.instance;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Singleton implementation of Stanford CoreNLP Pipeline
 */
public class StanfordCoreNLPInstance {

	/**
	 * The Singleton Stanford CoreNLP Pipeline
	 */
	private static StanfordCoreNLP pipeline;

	static {
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		pipeline = new StanfordCoreNLP(props);
	}

	/**
	 * Returns the Singleton Stanford CoreNLP Pipeline
	 * 
	 * @return Stanford CoreNLP Pipeline
	 */
	public static synchronized StanfordCoreNLP getInstance() {
		return pipeline;
	}

}
