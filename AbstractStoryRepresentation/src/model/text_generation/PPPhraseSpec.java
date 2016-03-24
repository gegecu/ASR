package model.text_generation;

import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.InternalFeature;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;

public class PPPhraseSpec extends PhraseElement {

	public PPPhraseSpec(NLGFactory phraseFactory) {
		super(PhraseCategory.PREPOSITIONAL_PHRASE);
		this.setFactory(phraseFactory);
	}
	
	/** sets the preposition (head) of a prepositional phrase
	 * @param preposition
	 */
	public void setPreposition(Object preposition) {
		if (preposition instanceof NLGElement)
			setHead(preposition);
		else {
			// create noun as word
			NLGElement prepositionalElement = getFactory().createWord(preposition, LexicalCategory.PREPOSITION);

			// set head of NP to nounElement
			setHead(prepositionalElement);
		}
	}

	/**
	 * @return preposition (head) of prepositional phrase
	 */
	public NLGElement getPreposition() {
		return getHead();
	}
	
	/** Sets the  object of a PP
	 *
	 * @param object
	 */
	public void setObject(Object object) {
		PhraseElement objectPhrase = getFactory().createNounPhrase(object);
		objectPhrase.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);
		addComplement(objectPhrase);
	}
	
	
	/**
	 * @return object of PP (assume only one)
	 */
	public NLGElement getObject() {
		List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		for (NLGElement complement: complements)
			if (complement.getFeature(InternalFeature.DISCOURSE_FUNCTION) == DiscourseFunction.OBJECT)
				return complement;
		return null;
	}

}