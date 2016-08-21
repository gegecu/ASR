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
import model.knowledge_base.topic.SpecificTopicDAO;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.text_generation.prompts.PromptGenerator;
import model.utility.Randomizer;

/**
 * Prompt generator for specific prompts
 */
public class SpecificPromptGenerator extends PromptGenerator {

	private static Logger log = Logger
			.getLogger(SpecificPromptGenerator.class.getName());

	/**
	 * List of supportext topics for generating specific topics
	 */
	public static final List<TypeOfNoun> availableTopics = Arrays
			.asList(new TypeOfNoun[]{TypeOfNoun.CHARACTER, TypeOfNoun.OBJECT});

	/**
	 * List of available topics for object nouns
	 */
	private String[] objectTopics = SpecificTopicDAO.getTopics("object");
	/**
	 * List of available topics for character nouns.
	 */
	private String[] personTopics = SpecificTopicDAO.getTopics("person");
	/**
	 * Map of correctly answered topics for each noun.
	 */
	private Map<Noun, List<String>> answered;
	/**
	 * The data that is used for generating prompts and answer checking
	 */
	private SpecificPromptData specificPromptData;

	/**
	 * @param specificPromptData
	 *            the specificPromptData to set
	 */
	public SpecificPromptGenerator(SpecificPromptData specificPromptData) {
		super(specificPromptData.getHistory());
		this.answered = specificPromptData.getAnswered();
		this.specificPromptData = specificPromptData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.text_generation.prompts.PromptGenerator#generateText(model.
	 * story_representation.story_element.noun.Noun)
	 */
	@Override
	public String generateText(Noun noun) {
		if (!specificPromptData.isWrong()) {
			return checkAvailableTopics(noun);
		} else {
			return generateWrongPrompts();
		}
	}

	/**
	 * @param isWrong
	 *            the isWrong to set
	 */
	public void setIsWrong(boolean isWrong) {
		specificPromptData.setIsWrong(isWrong);
	}

	/**
	 * @return the isWrong
	 */
	public boolean isWrong() {
		return specificPromptData.isWrong();
	}

	/**
	 * Returns a random topic from all the available topics for the noun, that
	 * are answered yet.
	 * 
	 * @param noun
	 *            the noun to use
	 * @return Returns a random topic from all the available topics for the
	 *         noun, that are answered yet.
	 */
	private String checkAvailableTopics(Noun noun) {

		specificPromptData.setCurrentNoun(noun);
		specificPromptData.setCurrentPrompt(null);
		String currentPrompt = null;
		List<String> availableTopics = getAvailableTopics(noun);

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

	/**
	 * Returns all the available topics for the noun, that are not answered yet.
	 * 
	 * @param noun
	 *            the noun to use
	 * @return Returns all the available topics for the noun, that are not
	 *         answered yet.
	 */
	private List<String> getAvailableTopics(Noun noun) {

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

		if (answered.get(noun) != null) {
			answeredTopics = new HashSet<>(answered.get(noun));
		}

		if (answeredTopics == null) {
			answeredTopics = new HashSet<>();
		}

		//check all anyway to be sure because TU sometime fails
		for (List<String> attributes : noun.getAttributes().values()) {
			for (String attribute : attributes) {
				for (String topic : topics) {
					if (ConceptNetDAO.conceptExists(attribute, "IsA", topic)) {
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
					if (ConceptNetDAO.conceptExists(entry2.getValue().getId(),
							"IsA", topic)) {
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

	/**
	 * @return Returns true if there are no more available topics for the
	 *         current noun set in the SpecificPromptData.
	 */
	public boolean checkifCompleted() {
		return getAvailableTopics(specificPromptData.getCurrentNoun())
				.isEmpty();
	}

	/**
	 * @return Generates an example of the expected answer for the current
	 *         prompt using the current noun in the SpecificPromptData <br>
	 *         <br>
	 *         Result : example + current prompt
	 */
	private String generateWrongPrompts() {

		String prompt = null;
		String currentTopic = specificPromptData.getCurrentTopic();

		List<Concept> concepts = ConceptNetDAO.getConceptsFrom(currentTopic,
				"IsA");

		while (concepts != null && !concepts.isEmpty()) {
			int randomConcept = Randomizer.random(1, concepts.size());
			prompt = "An example of " + currentTopic + " is "
					+ concepts.remove(randomConcept - 1).getStart() + ". "
					+ "What is the " + currentTopic + " of "
					+ specificPromptData.getCurrentNoun().getId() + "?";

			if (history.contains(prompt)) {
				prompt = null;
				continue;
			}

		}

		if (prompt == null) {
			//ignore
			specificPromptData.setIsWrong(false);
		}

		return prompt;

	}

}
