package model.knowledge_base.senticnet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.log4j.Logger;

/**
 * Offline SenticNet knowledge base parser
 */
public class SenticNetParser {

	/**
	 * Stores the model of the offline SenticNet knowledge base
	 */
	private Model m;
	/**
	 * The location of the offline SenticNet knowledge base file
	 * "senticnet3.rdf.xml"
	 */
	private final String file = "senticnet3.rdf.xml";

	private static Logger log = Logger
			.getLogger(SenticNetParser.class.getName());

	/**
	 * loads the offline SenticNet knowledge base into the model variable
	 */
	public SenticNetParser() {
		m = FileManager.get().loadModel(file);
	}

	/**
	 * Searches the offline SenticNet knowledge base for the polarity of the
	 * word, if the word does not exists in knowledge base this returns zero
	 * 
	 * @param concept
	 *            the word to be searched
	 * @return the polarity of the word, if not exists in knowledge base returns
	 *         zero
	 */
	public float getPolarity(String concept) {

		Resource title = m
				.getResource("http://sentic.net/api/en/concept/" + concept);
		Property contains = m.getProperty("http://sentic.net/apipolarity");

		try {
			log.debug("senticPolar" + title.getProperty(contains).getFloat());
			return title.getProperty(contains).getFloat();
		} catch (NullPointerException e) {
			return 0;
		}

	}

}
