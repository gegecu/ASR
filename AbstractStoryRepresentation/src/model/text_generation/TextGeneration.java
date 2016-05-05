package model.text_generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

public abstract class TextGeneration {

	protected Lexicon lexicon;
	protected NLGFactory nlgFactory;
	protected Realiser realiser;
	protected AbstractStoryRepresentation asr;
	protected final int defaultThreshold = 7;
	protected final int thresholdIncrement = 2;

	public TextGeneration(AbstractStoryRepresentation asr) {
		this.lexicon = Lexicon.getDefaultLexicon();
		this.nlgFactory = new NLGFactory(lexicon);
		this.realiser = new Realiser(lexicon);
		this.asr = asr;
	}

	public abstract String generateText();

	protected List<String> getNouns() {

		List<String> result = new ArrayList<>();
		int currThreshold = defaultThreshold;

		if (asr.getManyNouns().values().size() > 0) {

			while (result.isEmpty()) {

				List<String> nounId = asr.getCurrentStorySentence()
						.getAllNounsInStorySentence();

				while (!nounId.isEmpty()) {
					Noun noun = asr.getNoun(nounId.remove(0));
					int count = Utilities
							.countLists(noun.getAttributes().values())
							+ Utilities
									.countLists(noun.getReferences().values());
					if (count < currThreshold)
						result.add(noun.getId());
				}

				if (result.isEmpty()) {
					Collection<Noun> nouns = asr.getManyNouns().values();
					for (Noun noun : nouns) {
						int count = Utilities
								.countLists(noun.getAttributes().values())
								+ Utilities.countLists(
										noun.getReferences().values());
						if (count < currThreshold)
							result.add(noun.getId());
					}
				}

				currThreshold += thresholdIncrement;

			}

		}

		return result;

	}

}
