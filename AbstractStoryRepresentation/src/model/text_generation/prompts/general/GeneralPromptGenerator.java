package model.text_generation.prompts.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.knowledge_base.template.TemplateDAO;
import model.story_representation.story_element.noun.Noun;
import model.text_generation.prompts.PromptGenerator;
import model.utility.Randomizer;
import model.utility.SurfaceRealizer;

public class GeneralPromptGenerator extends PromptGenerator {

	/**
	 * Prompt data to use
	 */
	private GeneralPromptData generalPromptData;

	/**
	 * @param generalPromptData
	 *            the generalPromptData to set
	 */
	public GeneralPromptGenerator(GeneralPromptData generalPromptData) {
		super(generalPromptData.getHistory());
		this.generalPromptData = generalPromptData;
	}

	/**
	 * Stores the template directives
	 */
	private String[] nounStartDirective = TemplateDAO
			.getTemplates("nounStartDirective");

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.text_generation.prompts.PromptGenerator#generateText(model.
	 * story_representation.story_element.noun.Noun)
	 */
	@Override
	public String generateText(Noun noun) {
		String directive = findDirective(noun,
				new ArrayList<>(Arrays.asList(nounStartDirective)));
		generalPromptData.setCurrentNoun(noun);
		generalPromptData.setCurrentPrompt(directive);
		return directive;
	}

	/**
	 * Generates a prompt based on the extract noun information passed as param,
	 * and uses the template directives param as template for generation
	 * 
	 * @param noun
	 *            the noun to use for generation
	 * @param directives
	 *            the template directives
	 * @return generated prompt
	 */
	private String findDirective(Noun noun, List<String> directives) {

		String directive = null;
		while (!directives.isEmpty()
				&& (directive == null || history.contains(directive))) {

			int randomNounDirective = Randomizer.random(1, directives.size());

			directive = directives.remove(randomNounDirective - 1);

			List<Noun> temp = new ArrayList<>();
			temp.add(noun);

			directive = directive.replace("<noun>",
					SurfaceRealizer.nounFixer(temp));

			if (history.contains(directive)) {
				directive = null;
			}

		}

		return directive;

	}

}
