package model.text_generation.prompts;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.instance.StanfordCoreNLPInstance;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.text_generation.TextGeneration;
import model.text_generation.prompts.general.GeneralPromptAnswerChecker;
import model.text_generation.prompts.general.GeneralPromptData;
import model.text_generation.prompts.general.GeneralPromptGenerator;
import model.text_generation.prompts.special.SpecialPromptAnswerChecker;
import model.text_generation.prompts.special.SpecialPromptData;
import model.text_generation.prompts.special.SpecialPromptGenerator;
import model.text_generation.prompts.specific.SpecificPromptAnswerChecker;
import model.text_generation.prompts.specific.SpecificPromptData;
import model.text_generation.prompts.specific.SpecificPromptGenerator;
import model.text_understanding.Preprocessing;
import model.utility.Randomizer;

public class PromptChooser extends TextGeneration {

	public enum TypeOfPrompt {
		GENERAL, SPECIFIC, SPECIAL;
	}

	private static Logger log = Logger.getLogger(PromptChooser.class.getName());

	private Set<String> restrictedInGeneral;
	private Set<String> restrictedInSpecific;
	private String currentId;
	private Queue<String> history;
	private boolean answeredCorrect;
	private StanfordCoreNLP pipeline;
	private Preprocessing preprocess;
	private boolean isLoop;

	private TypeOfPrompt currentPromptType;
	private PromptGenerator currentPromptGenerator;
	private PromptAnswerChecker currentPromptAnswerChecker;

	private GeneralPromptData generalPromptData;
	private GeneralPromptAnswerChecker generalPromptAnswerChecker;
	private GeneralPromptGenerator generalPromptGenerator;

	private SpecificPromptData specificPromptData;
	private SpecificPromptGenerator specificPromptGenerator;
	private SpecificPromptAnswerChecker specificPromptAnswerChecker;

	private SpecialPromptData specialPromptData;
	private SpecialPromptAnswerChecker specialPromptAnswerChecker;
	private SpecialPromptGenerator specialPromptGenerator;

	private int historySizeThreshold = 2;

	public PromptChooser(AbstractStoryRepresentation asr) {

		super(asr);

		history = new LinkedList<>();
		restrictedInGeneral = new LinkedHashSet<>();
		restrictedInSpecific = new LinkedHashSet<>();
		pipeline = StanfordCoreNLPInstance.getInstance();
		preprocess = new Preprocessing();

		/* General Prompts */
		generalPromptData = new GeneralPromptData(history, asr);
		generalPromptAnswerChecker = new GeneralPromptAnswerChecker(
				generalPromptData);
		generalPromptGenerator = new GeneralPromptGenerator(generalPromptData);

		/* Specific Prompts */
		specificPromptData = new SpecificPromptData(history, asr);
		specificPromptGenerator = new SpecificPromptGenerator(
				specificPromptData);
		specificPromptAnswerChecker = new SpecificPromptAnswerChecker(
				specificPromptData);

		/* Special Prompts */
		specialPromptData = new SpecialPromptData(history, asr);
		specialPromptAnswerChecker = new SpecialPromptAnswerChecker(
				specialPromptData);
		specialPromptGenerator = new SpecialPromptGenerator(specialPromptData,
				nlgFactory, realiser);

	}

	@Override
	public String generateText() {

		String output = null;

		if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.start)) {

			List<String> nounId = this.getNouns();

			while (output == null && !nounId.isEmpty()) {

				String nounid = "";

				if (isLoop) {
					nounid = currentId;
				} else {
					nounid = nounId
							.remove(Randomizer.random(1, nounId.size()) - 1);
				}

				Noun noun = asr.getNoun(nounid);
				currentId = nounid;

				if (restrictedInGeneral.contains(nounid)) {
					if (SpecificPromptGenerator.availableTopics
							.contains(noun.getType())) {
						currentPromptType = TypeOfPrompt.SPECIFIC;
						currentPromptGenerator = specificPromptGenerator;
						currentPromptAnswerChecker = specificPromptAnswerChecker;
					}
				} else {
					currentPromptType = TypeOfPrompt.GENERAL;
					currentPromptGenerator = generalPromptGenerator;
					currentPromptAnswerChecker = generalPromptAnswerChecker;
				}

				output = currentPromptGenerator.generateText(noun);

				if (currentPromptType == TypeOfPrompt.SPECIFIC) {
					if (specificPromptGenerator.checkifCompleted()) {
						restrictedInGeneral.remove(currentId);
						restrictedInSpecific.add(currentId);
					}
				}

			}

		} else {
			
			List<Clause> clauses = new ArrayList();
			clauses.addAll(asr.getCurrentStorySentence().getManyDescriptions().values());
			clauses.addAll(asr.getCurrentStorySentence().getManyEvents().values());
			
			while(output == null && !clauses.isEmpty()) {
				int randomClause = Randomizer.random(1, clauses.size());
				
				output = specialPromptGenerator.generateText(clauses.remove(randomClause - 1));
			}
//			
//			if(predicates.isEmpty()) {
//				output = null;
//			}
//			
			currentPromptType = TypeOfPrompt.SPECIAL;
			
			currentPromptAnswerChecker = specialPromptAnswerChecker;
		}

		history.add(output);
		if (history.size() >= historySizeThreshold) {
			history.remove();
		}

		return output;

	}

	public boolean checkAnswer(String input) {

//		String temp = incompleteAnswer(input);
		Noun noun = asr.getNoun(currentId);

		answeredCorrect = false;
		isLoop = false;

		boolean isAnswerCorrect;

		if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.start)) {

			isAnswerCorrect = currentPromptAnswerChecker.checkAnswer(input);

			if (currentPromptType == TypeOfPrompt.GENERAL) {

				if (isAnswerCorrect == false) {

					//forever in general prompts, never add in restrictedGeneral because all specific answered
					if (!restrictedInSpecific.contains(currentId)) {
						// answered wrong, not yet completed q/a and not object or person
						if (!(noun.getType() == TypeOfNoun.LOCATION
								|| noun.getType() == TypeOfNoun.UNKNOWN)) {
							log.debug(currentId);
							restrictedInGeneral.add(currentId);
						}
						//isLoop = true;
					}
					
					isLoop = true;

				} else {
					answeredCorrect = true;
					isLoop = false;
				}

			} else if (currentPromptType == TypeOfPrompt.SPECIFIC) {

				if (isAnswerCorrect) {

					answeredCorrect = true;
					isLoop = false;
					specificPromptGenerator.setIsWrongIgnored(false);

					if (specificPromptGenerator.checkifCompleted()) {
						restrictedInGeneral.remove(currentId);
						restrictedInSpecific.add(currentId);
					}

				} else {
					specificPromptGenerator.setIsWrongIgnored(true);
					isLoop = true;
				}

			}

		} else {
			answeredCorrect = currentPromptAnswerChecker.checkAnswer(input);
			
			if(!answeredCorrect) {
				isLoop = true;
			}
			else {
				isLoop = false;
			}
		}

		return answeredCorrect;

	}

	public void ignored() {
		if (currentPromptType == TypeOfPrompt.SPECIFIC) {
			specificPromptGenerator.setIsWrongIgnored(false);
		}
	}

	public void stopLoop() {
		this.isLoop = false;
	}

	public boolean getIsLoop() {
		return this.isLoop;
	}

	public String incompleteAnswer(String input) {
		
		Annotation document;
		List<CoreMap> sentences;

		//annotate to check if one word answer
		
		if(currentPromptType == TypeOfPrompt.SPECIFIC) {
			document = new Annotation(input);
			pipeline.annotate(document);
			sentences = document.get(SentencesAnnotation.class);
			for (CoreMap sentence : sentences) {
				SemanticGraph dependencies = sentence
						.get(CollapsedCCProcessedDependenciesAnnotation.class);
	
				List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
						dependencies.typedDependencies());
	
				if (listDependencies.size() == 1 && listDependencies.get(0).equals("root")) {
					
					Noun noun = asr.getNoun(currentId);
					
					input = "";
				
					if(noun.getIsCommon()) {
						input += "The ";
					}
					
					input += noun.getId() + " is "
							+ listDependencies.get(0).dep().lemma().toLowerCase()
							+ ".";
				}
	
			}
		}
		
		preprocess.preprocess(history.peek() + " " + input);
		String temp = preprocess.getUpdatedString();
		
		String output = "";
		
		//annotate again to get updated string after correcting the one word answer
		document = new Annotation(temp);
		pipeline.annotate(document);
		sentences = document.get(SentencesAnnotation.class);
		//skip first sentence because it is the prompt
		
		int i = 1; 
		
		if(currentPromptType == TypeOfPrompt.SPECIFIC) {
			if(specificPromptGenerator.getIsWrong()) {
				// 2 sentences because there is additional sentence for example
				i = 2;
			}
		}
		
		
		
		for(int counter = i ; counter < sentences.size(); counter++) {
			String sentence = sentences.get(counter).toString();
			
			if(sentence.substring(sentence.length()-1).equals(".")) {
				output += sentence.substring(0, 1).toUpperCase() + sentence.substring(1, sentence.length()-2) + sentence.substring(sentence.length()-1) + " "; 
			}
			else {
				output += sentence.substring(0, 1).toUpperCase() + sentence.substring(1, sentence.length()) + " "; 
			}
		}

		System.out.println(output);
		
		return output;

	}

}
