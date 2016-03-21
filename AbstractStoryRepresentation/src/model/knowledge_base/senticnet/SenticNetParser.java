package model.knowledge_base.senticnet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

public class SenticNetParser {

	private Model m;
	private final String file = "senticnet3.rdf.xml";
	
	public SenticNetParser() {
		m = FileManager.get().loadModel(file);
	}
	
	public float getPolarity(String concept) {
		Resource title = m.getResource("http://sentic.net/api/en/concept/" + concept);
		Property contains = m.getProperty("http://sentic.net/apipolarity");
		try {
			return title.getProperty(contains).getFloat();
		}catch(NullPointerException e) {
			return 0;
		}
	}
		
}
