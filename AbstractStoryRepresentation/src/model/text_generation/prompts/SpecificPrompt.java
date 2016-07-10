package model.text_generation.prompts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.knowledge_base.conceptnet.Concept;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Object;
import model.utility.Randomizer;
import model.utility.TypedDependencyComparator;

public class SpecificPrompt extends Prompt {

	private static Logger log = Logger
			.getLogger(SpecificPrompt.class.getName());

	private String[] objectTopics = {"color", "shape", "size", "texture"};
	private String[] personTopics = {"attitude", "nationality", "talent"};
	private Map<Noun, List<String>> answered;
	private String currentTopic;
	private boolean isWrong;

	public SpecificPrompt(Queue<String> history) {
		super(history);
		answered = new HashMap<>();
		isWrong = false;
	}

	@Override
	public String generateText(Noun noun) {
		if (!isWrong) {
			return checkAvailableTopics(noun);
		} else {
			return generateWrongPrompts();
		}
	}

	public void setIsWrongIgnored(boolean isWrong) {
		this.isWrong = isWrong;
	}

	public boolean getIsWrong() {
		return this.isWrong;
	}

	private String checkAvailableTopics(Noun noun) {

		currentNoun = noun;
		List<String> availableTopics = availableTopics(noun);
		currentPrompt = null;

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

			currentTopic = availableTopics.remove(random - 1);
			currentPrompt = "What is the " + currentTopic
					+ " of " + toBeReplaced + noun.getId() + "?";

			if (history.contains(currentPrompt)) {
				currentPrompt = null;
				continue;
			}

			log.debug(currentPrompt);

			return currentPrompt;

		}

		return null;

	}

	// need to fix or think of another way because possible compound compound.
	public boolean checkAnswer(String input) {

		Map<String, String> coref;

		String noun = "";
		String topicAnswer = "";

		Annotation document = new Annotation(input);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {

			preprocess.preprocess(currentPrompt + " " + sentence.toString());

			coref = preprocess.getCoref();

			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);

			List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
					dependencies.typedDependencies());
			Collections.sort(listDependencies, new TypedDependencyComparator());

			for (TypedDependency td : listDependencies) {

				//What is the color of the ball? It is red. cannot be He is red.
				//What is the nationality of John Roberts. He is Chinese or John Roberts is Chinese.
				int countSame = 0;
				for (Map.Entry<String, String> entry : coref.entrySet()) {
					if (entry.getKey().equals(entry.getValue())) {
						countSame++;
					}
				}

				if (coref.size() - countSame >= 1) {

					noun = currentNoun.getId();

					if (td.reln().toString().equals("nsubj")) {
						//noun = td.dep().lemma();
						topicAnswer = td.gov().lemma();
					}

					if (td.reln().toString().equals("compound")) {
						if (td.gov().lemma().equals(topicAnswer)) {
							topicAnswer = td.dep().lemma() + " " + topicAnswer;
						}
					}

				}

			}

			//System.out.println("noun  " + noun + " " + currentNoun.getId() + " " + "answer " + topicAnswer + " " + currentTopic);

			if (ConceptNetDAO.checkSRL(topicAnswer, "IsA", currentTopic)
					&& noun.equals(currentNoun.getId())) {

				List<String> topics = answered.get(currentNoun);
				if (topics == null) {
					topics = new ArrayList<>();
				}
				topics.add(currentTopic);
				answered.put(currentNoun, topics);
				return true;

			}

			// get first sentence of answer only.
			break;

		}

		return false;

	}

	private List<String> availableTopics(Noun noun) {
		String[] topics = null;

		if (noun instanceof Object) {
			topics = objectTopics;
		} else if (noun instanceof Character) {
			topics = personTopics;
		}

		List<String> answeredTopics = answered.get(noun);
		if (answeredTopics == null) {
			answeredTopics = new ArrayList<>();
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
		if (noun instanceof Object) {
			availableTopics = new ArrayList<>(Arrays.asList(objectTopics));
		} else if (noun instanceof Character) {
			availableTopics = new ArrayList<>(Arrays.asList(personTopics));
		}

		availableTopics.removeAll(answeredTopics);
		return availableTopics;
	}

	public boolean checkifCompleted() {
		return availableTopics(currentNoun).isEmpty();
	}

	private String generateWrongPrompts() {

		String prompt = "";

		List<Concept> concepts = ConceptNetDAO.getConceptFrom(currentTopic,
				"IsA");

		if (concepts != null && !concepts.isEmpty()) {
			int randomConcept = Randomizer.random(1, concepts.size());
			prompt = "An example of " + currentTopic + " is "
					+ concepts.get(randomConcept).getStart() + ". ";
		} else {
			//ignore
			this.isWrong = false;
		}

		prompt += "What is the " + currentTopic + " of " + currentNoun.getId()
				+ "?";

		return prompt;
	}

}
