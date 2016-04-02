package model.text_generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Predicate;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.Randomizer;
import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.phrasespec.SPhraseSpec;

public class MissingElementQuestionGenerator extends TextGeneration {

	private SPhraseSpec phraseElement;
	private final InterrogativeType[] indirectQuestions = { InterrogativeType.WHO_INDIRECT_OBJECT };
	private final InterrogativeType[] directQuestions = { InterrogativeType.WHAT_OBJECT, InterrogativeType.WHO_OBJECT };

	public MissingElementQuestionGenerator(AbstractStoryRepresentation asr) {
		super(asr);
	}

	@Override
	public String generateText() {
		this.setSentenceElements();

		int randomNum = Randomizer.random(1, 2);
		String output = null;

		switch (randomNum) {
		case 1:
			output = this.askAboutIndirectObject();
			if (output == null)
				output = this.askAboutDirectObject();

		case 2:
			output = this.askAboutDirectObject();
			if (output == null)
				output = this.askAboutIndirectObject();
		}

		return output;
	}

	private String askAboutIndirectObject() {

		int randomNum = Randomizer.random(1, indirectQuestions.length);
		if (this.phraseElement.getIndirectObject() == null) {
			this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, indirectQuestions[randomNum - 1]);
			return this.realiser.realiseSentence(phraseElement);
		}
		return null;
	}

	private String askAboutDirectObject() {

		int randomNum = Randomizer.random(1, directQuestions.length); // why,
																		// who,
																		// how,
																		// where
		if (this.phraseElement.getObject() == null) {
			this.phraseElement.setFeature(Feature.INTERROGATIVE_TYPE, directQuestions[randomNum - 1]);
			return this.realiser.realiseSentence(phraseElement);
		}
		return null;
	}

	private void setSentenceElements() {

		StorySentence storySentence = asr.getCurrentStorySentence();
		this.phraseElement = nlgFactory.createClause();

		// subjects or doers
		Collection<Noun> subjects = Utilities.getDoers(storySentence);
		CoordinatedPhraseElement subj = nlgFactory.createCoordinatedPhrase();
		for (Noun subject : subjects) {
			subj.addCoordinate(subject.getId());
		}
		this.phraseElement.setSubject(subj);

		// randomly choose a predicate
		List<Predicate> predicates = new ArrayList(storySentence.getManyPredicates().values());
		int randomNum = Randomizer.random(1, predicates.size());

		// verb
		this.phraseElement.setVerb(predicates.get(randomNum - 1).getAction());

		// receivers
		Collection<Noun> receivers = predicates.get(randomNum - 1).getReceivers().values();
		CoordinatedPhraseElement iobj = nlgFactory.createCoordinatedPhrase();
		for (Noun receiver : receivers) {
			iobj.addCoordinate(receiver.getId());
		}
		if (!receivers.isEmpty())
			this.phraseElement.setIndirectObject(iobj);

		// directObject
		Collection<Noun> directObjects = predicates.get(randomNum - 1).getReceivers().values();
		CoordinatedPhraseElement dobj = nlgFactory.createCoordinatedPhrase();
		for (Noun directObject : directObjects) {
			dobj.addCoordinate(directObject.getId());
		}
		if (!directObjects.isEmpty())
			this.phraseElement.setObject(dobj);

	}

}
