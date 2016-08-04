package model.text_generation.prompts.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import model.story_representation.story_element.noun.Noun;
import model.text_generation.prompts.PromptGenerator;
import model.utility.Randomizer;
import model.utility.SurfaceRealizer;

public class GeneralPromptGenerator extends PromptGenerator {

	private GeneralPromptData generalPromptData;

	public GeneralPromptGenerator(GeneralPromptData generalPromptData) {
		super(generalPromptData.getHistory());
		this.generalPromptData = generalPromptData;
	}

	private String[] nounStartDirective = {"Describe <noun>.",
			"Tell me more about <noun>.", "Write more about <noun>.",
			"I want to hear more about <noun>.",
			"Tell something more about <noun>."};

	@Override
	public String generateText(Noun noun) {
		String directive = findDirective(noun,
				new ArrayList<>(Arrays.asList(nounStartDirective)));
		generalPromptData.setCurrentNoun(noun);
		generalPromptData.setCurrentPrompt(directive);
		return directive;
	}

	private String findDirective(Noun noun, List<String> directives) {

		String directive = null;
		while (!directives.isEmpty()
				&& (directive == null || history.contains(directive))) {

			int randomNounDirective = Randomizer.random(1, directives.size());

			directive = directives.remove(randomNounDirective - 1);

			List<Noun> temp = new ArrayList<>();
			temp.add(noun);

			directive = directive.replace("<noun>", SurfaceRealizer.nounFixer(temp));
			
			if(history.contains(directive)) {
				directive = null;
			}

		}

		return directive;

	}

}
