package model.utility;

import java.util.List;

import model.story_representation.story_element.noun.Noun;

public class SurfaceRealizer {

	public static String wordsConjunction(List<Noun> nouns) {

		String characters = "";

		for (int i = 0; i < nouns.size(); i++) {
			if (nouns.get(i).getIsCommon()) {
				characters += "the ";
			}
			if (i < nouns.size() - 2) {
				characters += nouns.get(i).getId() + ", ";
			} else if (i < nouns.size() - 1) {
				characters += nouns.get(i).getId() + " and ";
			} else {
				characters += nouns.get(i).getId();
			}
		}

		return characters;

	}

	static String[] vowels = {"a", "e", "i", "o", "u"};

	public static String determinerFixer(String word) {

		String temp = word.toLowerCase();
		for (String vowel : vowels) {
			if (temp.startsWith(vowel)) {
				return "an " + temp;
			}
		}
		return "a " + temp;

	}

}
