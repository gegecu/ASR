package model.text_generation.prompts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Object;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_generation.TextGeneration;
import model.text_generation.Utilities;
import model.utility.Randomizer;

public class PromptChooser extends TextGeneration{
	public Set<String> restrictedInGeneral;
	public Set<String> restrictedInSpecific;
	private GeneralPrompt generalPrompt;
	private SpecificPrompt specificPrompt;
	private Prompt currentPrompt;
	private int descriptionThreshold;
	private String currentId;
	
	public PromptChooser(AbstractStoryRepresentation asr) {
		super(asr);
		generalPrompt = new GeneralPrompt();
		specificPrompt = new SpecificPrompt();
		descriptionThreshold = 7;
		restrictedInGeneral = new LinkedHashSet();
		restrictedInSpecific = new LinkedHashSet();
	}

	@Override
	public String generateText() {
		// TODO Auto-generated method stub
		
		String nounid = findNounId();
		Noun noun = asr.getNoun(nounid);
		currentId = nounid;
		
		if(restrictedInGeneral.contains(nounid)) {
			if(noun instanceof Object || noun instanceof Character) {
				currentPrompt = specificPrompt;
			}
		}
		else {
			currentPrompt = generalPrompt;
		}
		
		return currentPrompt.generateText(noun);
	}
	
	public void checkAnswer(String input) {
		if(currentPrompt instanceof GeneralPrompt) {
			if(!currentPrompt.checkAnswer(input)) {
				//did not answer
				if(!restrictedInSpecific.contains(currentId)) {
					restrictedInGeneral.add(currentId);
				}
			}
		}
		else if(currentPrompt instanceof SpecificPrompt) {
			currentPrompt.checkAnswer(input);
			
			if(((SpecificPrompt)currentPrompt).checkifCompleted()) {
				restrictedInGeneral.remove(currentId);
			}
			
		}
	}
	
	private String findNounId() {
		StorySentence storySentence = asr.getCurrentStorySentence();
		List<String> nounId;
		int iterations = 0;
		while (iterations++ < 10) {

			nounId = storySentence.getAllNounsInStorySentence();
			int randomNoun;
			String id;
			Noun noun;
			int threshold;
			
			while(!nounId.isEmpty()) {
				threshold = 0;
				randomNoun = Randomizer.random(1, nounId.size());
				id = nounId.remove(randomNoun - 1);
				noun = asr.getNoun(id);

				threshold += Utilities.countLists(noun.getAttributes().values());
				//threshold += Utilities.countLists(noun.getReferences().values());
				threshold += noun.getReferences().values().size();
				
				if (threshold < descriptionThreshold) {
					return id;
				}
			}
			
			
			nounId = new ArrayList<>(asr.getNounMap().keySet());
			nounId.removeAll(storySentence.getAllNounsInStorySentence());
				
			while(!nounId.isEmpty()) {
				threshold = 0;
				randomNoun = Randomizer.random(1, nounId.size());
				id = nounId.remove(randomNoun - 1);
				noun = asr.getNoun(id);
	
				threshold += Utilities.countLists(noun.getAttributes().values());
				//threshold += Utilities.countLists(noun.getReferences().values());
				threshold += noun.getReferences().values().size();
					
				if (threshold < descriptionThreshold) {
					return id;
				}
					
			}
			descriptionThreshold+=2;
		}
		return null;
	}
	
}
