package model.text_generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

/**
 * Abstract class for text generation
 */
public abstract class TextGeneration {

	/**
	 * To be used if Alice was not able to generate prompts
	 */
	public static final String defaultResponse = "I can't think of anything. Tell me more.";

	/**
	 * Information about words
	 */
	protected Lexicon lexicon;
	/**
	 * Object which creates simplenlg structures
	 */
	protected NLGFactory nlgFactory;
	/**
	 * Object which transforms simplenlg structures into text
	 */
	protected Realiser realiser;
	/**
	 * Stores extracted information from user's input.
	 */
	protected AbstractStoryRepresentation asr;
	/**
	 * Threshold for total number of descriptions or references of a noun
	 */
	protected final int defaultThreshold = 3;
	/**
	 * Used in defaultThreshold if all nouns are already above the
	 * defaultThreshold
	 */
	protected final int thresholdIncrement = 2;

	/**
	 * Initialize the variables
	 * 
	 * @param asr
	 *            the asr to set
	 */
	public TextGeneration(AbstractStoryRepresentation asr) {
		this.lexicon = Lexicon.getDefaultLexicon();
		this.nlgFactory = new NLGFactory(lexicon);
		this.realiser = new Realiser(lexicon);
		this.asr = asr;
	}

	/**
	 * Returns a string (the generated text) that is implemented by the
	 * subclasses
	 * 
	 * @return Returns a string (the generated text) that is implemented by the
	 *         subclasses
	 */
	public abstract String generateText();

	//recoded
	/**
	 * @return Returns a list of id of nouns that is below the default threshold
	 *         (there’s a rule for selecting the nouns)
	 */
	protected List<String> getNouns() {

		List<String> result = new ArrayList<>();
		int currThreshold = defaultThreshold;

		if (asr.getNounsMap().values().size() > 0) {

			while (result.isEmpty()) {

				List<String> nounId = asr.getCurrentStorySentence()
						.getAllNouns();

				while (!nounId.isEmpty()) {
					String id = nounId.remove(0);
					Noun noun = asr.getNoun(id);
					int count = Utilities
							.countLists(noun.getAttributes().values());
					for (Map.Entry<String, Map<String, Noun>> entry : noun
							.getReferences().entrySet()) {
						count += entry.getValue().size();
					}
					if (count < currThreshold) {
						result.add(id);
					}
				}

				if (result.isEmpty()) {
					Set<String> ids = asr.getNounsMap().keySet();
					for (String id : ids) {
						Noun noun = asr.getNoun(id);
						int count = Utilities
								.countLists(noun.getAttributes().values());
						for (Map.Entry<String, Map<String, Noun>> entry : noun
								.getReferences().entrySet()) {
							count += entry.getValue().size();
						}
						System.out.println(count);
						if (count < currThreshold) {
							result.add(id);
						}
					}
				}

				currThreshold += thresholdIncrement;

			}

		}

		return result;

	}

}
