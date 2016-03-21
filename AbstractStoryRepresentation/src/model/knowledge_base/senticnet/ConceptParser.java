package model.knowledge_base.senticnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import semantic_parser.concept_parser;

public class ConceptParser {
	private concept_parser cp;
	
	public ConceptParser() {
		cp = new concept_parser();
	}
	
	public List<String> getConcepts(String text) {
		try {
			return cp.get_concepts(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
