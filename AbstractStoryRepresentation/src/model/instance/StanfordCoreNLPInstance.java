package model.instance;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordCoreNLPInstance {

	private static StanfordCoreNLP pipeline;

	static {
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		pipeline = new StanfordCoreNLP(props);
	}

	public static synchronized StanfordCoreNLP getInstance() {
		return pipeline;
	}

}
