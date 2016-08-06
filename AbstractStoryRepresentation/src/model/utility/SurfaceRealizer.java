package model.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import model.story_representation.story_element.noun.Noun;

public class SurfaceRealizer {
	
	public static String pluralNoun(Noun noun) {
		XMLLexicon xmlLexicon = new XMLLexicon();
		WordElement word = xmlLexicon.getWord(noun.getId(), LexicalCategory.NOUN);
		InflectedWordElement pluralWord = new InflectedWordElement(word);
		pluralWord.setPlural(true);
		Realiser realiser = new Realiser(xmlLexicon);
		return realiser.realise(pluralWord).toString();
		
	}

	public static String nounFixer(List<Noun> nouns) {
		
		List<String> nounsSurfaceText = new ArrayList<>();
		String text = "";
		
		for(Noun noun: nouns) {
			
			String temp = "";
			
			Map<String, Noun> ownersMap = noun.getReference("IsOwnedBy");
			if(ownersMap != null) {
				List<Noun> owners = new ArrayList<>(ownersMap.values());
				List<String> ownersSurfaceText = new ArrayList<>();
				for(Noun owner: owners) {
					ownersSurfaceText.add(owner.getIsCommon()? "the " + owner.getId() : owner.getId());
				}
				
				temp = wordConjunction(ownersSurfaceText) + "'s ";
				
			}
			else {
				temp = (noun.getIsCommon() ? "the " : "");
			}
			
			temp += noun.getId();
			
			nounsSurfaceText.add(temp);
		}
		
		text = wordConjunction(nounsSurfaceText);
		
		return text;

	}
	
	private static String wordConjunction(List<String> words) {
		Lexicon lexicon = Lexicon.getDefaultLexicon();
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		Realiser realiser = new Realiser(lexicon);

		String text = "";
		
		List<NPPhraseSpec> subjects = new ArrayList<>();
		
		for(String word: words) {
			
			NPPhraseSpec subject = nlgFactory.createNounPhrase(word);
			subjects.add(subject);
		}
		
		CoordinatedPhraseElement subj = nlgFactory.createCoordinatedPhrase(); 
		for(NPPhraseSpec subject: subjects) {
			subj.addCoordinate(subject);
		}
		
		subj.setFeature(Feature.CONJUNCTION, "and");
		
		text = realiser.realise(subj).toString();
		
		return text;
		
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
	
	public static String properNounFixer(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1);
	}
}
