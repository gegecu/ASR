package model.utility;

import java.util.List;

import model.story_representation.story_element.noun.Noun;

public class SurfaceRealizer {
	
	public static String wordsConjunction(List<Noun> nouns) {
		String characters = "";
		for(int i = 0 ; i < nouns.size(); i++) {
			if(nouns.get(i).getIsCommon()) {
				characters += "the ";
			}
			if(i < nouns.size()-2) {
				characters += nouns.get(i).getId() + ", ";
			}
			else if (i < nouns.size() - 1) {
				characters += nouns.get(i).getId() + " and ";
			}
			else {
				characters += nouns.get(i).getId();
			}
		}
		
		return characters;
	}
	
	public static String determinerFixer(String word) {
		String temp = word.toLowerCase();
		if(temp.startsWith("a") || temp.startsWith("e") || temp.startsWith("i")
				|| temp.startsWith("o") || temp.startsWith("u")) {
			return "an " + temp;
		}
		else {
			return "a " + temp;
		}
	}
	
}
