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

	/**
	 * Pluralizes the surface text of the noun
	 * 
	 * @param noun
	 *            the noun used
	 * @return pluralized surface text of the noun
	 */
	public static String pluralNoun(Noun noun) {
		XMLLexicon xmlLexicon = new XMLLexicon();
		WordElement word = xmlLexicon.getWord(noun.getId(),
				LexicalCategory.NOUN);
		InflectedWordElement pluralWord = new InflectedWordElement(word);
		pluralWord.setPlural(true);
		Realiser realiser = new Realiser(xmlLexicon);
		return realiser.realise(pluralWord).toString();
	}

	/**
	 * Puts an article on the text (“the” at the start for common noun or
	 * apostrophe s “‘s” at the end of the text for proper nouns)
	 * 
	 * @param nouns
	 *            the nouns to use
	 * @return string with fixed article
	 */
	public static String nounFixer(List<Noun> nouns) {

		List<String> nounsSurfaceText = new ArrayList<>();
		String text = "";

		for (Noun noun : nouns) {

			String temp = "";

			Map<String, Noun> ownersMap = noun.getReference("IsOwnedBy");
			if (ownersMap != null) {
				List<Noun> owners = new ArrayList<>(ownersMap.values());
				List<String> ownersSurfaceText = new ArrayList<>();
				for (Noun owner : owners) {
					ownersSurfaceText.add(owner.getIsCommon()
							? "the " + owner.getId()
							: owner.getId());
				}

				if (ownersSurfaceText.get(ownersSurfaceText.size() - 1)
						.endsWith("s")) {
					temp = wordConjunction(ownersSurfaceText) + "' ";
				} else {
					temp = wordConjunction(ownersSurfaceText) + "'s ";
				}

			} else {
				temp = (noun.getIsCommon() ? "the " : "");
			}

			temp += noun.getId();

			nounsSurfaceText.add(temp);
		}

		text = wordConjunction(nounsSurfaceText);

		return text;

	}

	/**
	 * Concatenates all the strings with an "and" from the list of strings
	 * parameter
	 * 
	 * @param words
	 *            - List of strings to be concatenated.
	 * @return combined strings
	 */
	private static String wordConjunction(List<String> words) {
		Lexicon lexicon = Lexicon.getDefaultLexicon();
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		Realiser realiser = new Realiser(lexicon);

		String text = "";

		List<NPPhraseSpec> subjects = new ArrayList<>();

		for (String word : words) {

			NPPhraseSpec subject = nlgFactory.createNounPhrase(word);
			subjects.add(subject);
		}

		CoordinatedPhraseElement subj = nlgFactory.createCoordinatedPhrase();
		for (NPPhraseSpec subject : subjects) {
			subj.addCoordinate(subject);
		}

		subj.setFeature(Feature.CONJUNCTION, "and");

		text = realiser.realise(subj).toString();

		return text;

	}

	/**
	 * list of vowel characters
	 */
	static String[] vowels = {"a", "e", "i", "o", "u"};

	/**
	 * Adds “a” or “an” to the beginning of the word depending on the starting
	 * letter of the input word
	 * 
	 * @param word
	 *            the word to be fixed
	 * @return The word fixed
	 */
	public static String determinerFixer(String word) {

		String temp = word.toLowerCase();
		for (String vowel : vowels) {
			if (temp.startsWith(vowel)) {
				return "an " + word;
			}
		}
		return "a " + word;

	}

	/**
	 * @param word
	 *            the word to use
	 * @return word word with first letter capitalized
	 */
	public static String capitalizeFirstLetter(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1);
	}
}
