package model.text_generation.prompts.specific;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import model.knowledge_base.conceptnet.Concept;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.text_generation.prompts.PromptGenerator;
import model.utility.Randomizer;

public class SpecificPromptGenerator extends PromptGenerator {

	private static Logger log = Logger
			.getLogger(SpecificPromptGenerator.class.getName());

	public static final List<TypeOfNoun> availableTopics = Arrays
			.asList(new TypeOfNoun[]{TypeOfNoun.CHARACTER, TypeOfNoun.OBJECT});

	private String[] objectTopics = {"color", "shape", "size", "texture"};
	private String[] personTopics = {"attitude", "nationality", "talent"};
	private Map<Noun, List<String>> answered;
	private SpecificPromptData specificPromptData;

	public SpecificPromptGenerator(SpecificPromptData specificPromptData) {
		super(specificPromptData.getHistory());
		this.answered = specificPromptData.getAnswered();
		this.specificPromptData = specificPromptData;
	}

	@Override
	public String generateText(Noun noun) {
		if (!specificPromptData.isWrong()) {
			return checkAvailableTopics(noun);
		} else {
			return generateWrongPrompts();
		}
	}

	public void setIsWrongIgnored(boolean isWrong) {
		specificPromptData.setWrong(isWrong);
	}

	public boolean getIsWrong() {
		return specificPromptData.isWrong();
	}

	private String checkAvailableTopics(Noun noun) {

		specificPromptData.setCurrentNoun(noun);
		specificPromptData.setCurrentPrompt(null);
		String currentPrompt = null;
		List<String> availableTopics = availableTopics(noun);

		while (!availableTopics.isEmpty() && currentPrompt == null) {
			int random = Randomizer.random(1, availableTopics.size());

			String toBeReplaced = "";

			Map<String, Noun> ownersMap = noun.getReference("IsOwnedBy");

			if (ownersMap != null) {
				List<Noun> owners = new ArrayList<>(ownersMap.values());
				if (owners != null) {
					toBeReplaced = owners.get(owners.size() - 1).getId()
							+ "'s ";
				}
			} else {
				toBeReplaced = (noun.getIsCommon() ? "the " : "");
			}

			String currentTopic = availableTopics.remove(random - 1);
			currentPrompt = "What is the " + currentTopic + " of "
					+ toBeReplaced + noun.getId() + "?";

			if (history.contains(currentPrompt)) {
				currentPrompt = null;
				continue;
			}

			log.debug(currentPrompt);

			specificPromptData.setCurrentTopic(currentTopic);
			specificPromptData.setCurrentPrompt(currentPrompt);

			return currentPrompt;

		}

		return null;

	}

	private List<String> availableTopics(Noun noun) {

		String[] topics = null;

		switch (noun.getType()) {
			case CHARACTER :
				topics = personTopics;
				break;
			case OBJECT :
				topics = objectTopics;
				break;
			default :
				break;
		}
		
		Set<String> answeredTopics = null;
		
		if(answered.get(noun) != null) {
			answeredTopics = new HashSet<>(answered.get(noun));
		}

		if (answeredTopics == null) {
			answeredTopics = new HashSet<>();
		}

		//check all anyway to be sure because TU sometime fails
		for (List<String> attributes : noun.getAttributes().values()) {
			for (String attribute : attributes) {
				for (String topic : topics) {
					if (ConceptNetDAO.checkSRL(attribute, "IsA", topic)) {
						answeredTopics.add(topic);
					}
				}
			}
		}

		//need to check references because possible error in TU ball isA round
		for (Map.Entry<String, Map<String, Noun>> entry : noun.getReferences()
				.entrySet()) {
			for (Map.Entry<String, Noun> entry2 : entry.getValue().entrySet()) {
				for (String topic : topics) {
					if (ConceptNetDAO.checkSRL(entry2.getValue().getId(), "IsA",
							topic)) {
						answeredTopics.add(topic);
					}
				}
			}
		}

		List<String> availableTopics = null;

		switch (noun.getType()) {
			case CHARACTER :
				availableTopics = new ArrayList<>(Arrays.asList(personTopics));
				break;
			case OBJECT :
				availableTopics = new ArrayList<>(Arrays.asList(objectTopics));
				break;
			default :
				break;
		}

		availableTopics.removeAll(answeredTopics);
		return availableTopics;

	}

	public boolean checkifCompleted() {
		return availableTopics(specificPromptData.getCurrentNoun()).isEmpty();
	}

	private String generateWrongPrompts() {

		String prompt = "";
		String currentTopic = specificPromptData.getCurrentTopic();

		List<Concept> concepts = ConceptNetDAO.getConceptFrom(currentTopic,
				"IsA");

		if (concepts != null && !concepts.isEmpty()) {
			int randomConcept = Randomizer.random(1, concepts.size());
			prompt = "An example of " + currentTopic + " is "
					+ concepts.get(randomConcept).getStart() + ". ";
		} else {
			//ignore
			specificPromptData.setWrong(false);
		}

		prompt += "What is the " + currentTopic + " of "
				+ specificPromptData.getCurrentNoun().getId() + "?";

		return prompt;

	}

}
