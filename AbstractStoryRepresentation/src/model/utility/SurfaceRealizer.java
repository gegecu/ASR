package model.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import model.story_representation.story_element.noun.Noun;

public class SurfaceRealizer {

	public static String wordsConjunction(List<Noun> nouns) {
		
		Lexicon lexicon = Lexicon.getDefaultLexicon();
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		Realiser realiser = new Realiser(lexicon);

		String characters = "";
		
		List<NPPhraseSpec> subjects = new ArrayList();
		
		for(Noun noun: nouns) {
			
			String temp = "";
			
			Map<String, Noun> ownersMap = noun.getReference("IsOwnedBy");
			if(ownersMap != null) {
				List<Noun> owners = new ArrayList<>(ownersMap.values());
				if(owners != null) {
					temp = owners.get(owners.size()-1).getId() + "'s ";
				}
			}
			else {
				temp = (noun.getIsCommon() ? "the " : "");
			}
			
			temp += noun.getId();
			
			NPPhraseSpec subject = nlgFactory.createNounPhrase(temp);
			subjects.add(subject);
		}
		
		CoordinatedPhraseElement subj = nlgFactory.createCoordinatedPhrase(); 
		for(NPPhraseSpec subject: subjects) {
			subj.addCoordinate(subject);
		}
		
		subj.setFeature(Feature.CONJUNCTION, "and");
		
		characters = realiser.realise(subj).toString();
		
		
		

//		for (int i = 0; i < nouns.size(); i++) {
//			
//			Map<String, Noun> ownersMap = nouns.get(i).getReference("IsOwnedBy");
//			if(ownersMap != null) {
//				List<Noun> owners = new ArrayList<>(ownersMap.values());
//				if(owners != null) {
//					characters = owners.get(owners.size()-1).getId() + "'s ";
//				}
//			}
//			else {
//				characters = (nouns.get(i).getIsCommon() ? "the " : "");
//			}
//
//			if (i < nouns.size() - 2) {
//				characters += nouns.get(i).getId() + ", ";
//			} else if (i < nouns.size() - 1) {
//				characters += nouns.get(i).getId() + " and ";
//			} else {
//				characters += nouns.get(i).getId();
//			}
//		}

		return characters;

	}

	static String[] vowels = {"a", "e", "i", "o", "u"};

	public static String determinerFixer(String word) {

		String temp = word.toLowerCase();
		for (String vowel : vowels) {
			if (temp.startsWith(vowel)) {
				return "an " + word;
			}
		}
		return "a " + word;

	}

}
