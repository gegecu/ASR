package model.instance;

import java.util.Properties;
import java.util.concurrent.Semaphore;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordCoreNLPInstance {

	private static Semaphore semaphore;
	private static StanfordCoreNLP pipeline;

	static {
		semaphore = new Semaphore(0);
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		pipeline = new StanfordCoreNLP(props);
		semaphore.release(1);
	}

	public static synchronized StanfordCoreNLP getInstance() {
		try {
			semaphore.acquire();
			semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return pipeline;
	}

}
