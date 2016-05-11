package model.text_generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

	
	//recoded
	protected List<String> getNouns() {

		List<String> result = new ArrayList<>();
		int currThreshold = defaultThreshold;

		if (asr.getManyNouns().values().size() > 0) {

			while (result.isEmpty()) {

				List<String> nounId = asr.getCurrentStorySentence()
						.getAllNounsInStorySentence();

				while (!nounId.isEmpty()) {
					String id = nounId.remove(0);
					Noun noun = asr.getNoun(id);
					int count = Utilities
							.countLists(noun.getAttributes().values())
							+ Utilities
									.countLists(noun.getReferences().values());
					if (count < currThreshold)
						//result.add(noun.getId());
						result.add(id);
				}

				if (result.isEmpty()) {
					Set<String> ids = asr.getManyNouns().keySet();
					for (String id : ids) {
						int count = Utilities
								.countLists(asr.getNoun(id).getAttributes().values())
								+ Utilities.countLists(
										asr.getNoun(id).getReferences().values());
						if (count < currThreshold)
							//result.add(noun.getId());
							result.add(id);
					}
				}

				currThreshold += thresholdIncrement;

			}

		}

		return result;

	}

}
