package model.text_generation.prompts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import model.instance.DictionariesInstance;
import model.instance.StanfordCoreNLPInstance;
import model.text_understanding.Preprocessing;
import model.utility.TypedDependencyComparator;

public abstract class PromptAnswerChecker {

	protected StanfordCoreNLP pipeline;
	protected Preprocessing preprocess;

	public PromptAnswerChecker() {
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.preprocess = new Preprocessing();
	}

	public boolean checkAnswer(String answer) {
		return preChecking(answer);
	}

	public boolean preChecking(String answer) {
		
		Map<String, Boolean> xcompChecker = new HashMap();
		int xcompCounter = 0;
		
		boolean postChecking = false;
		
		Annotation document = new Annotation(answer);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		for(CoreMap sentence: sentences) {
			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);

			List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(
					dependencies.typedDependencies());
			Collections.sort(listDependencies, new TypedDependencyComparator());
			
			for (TypedDependency td : listDependencies) {
				
				//What is the color of the ball? It is red. cannot be He is red.
				//What is the nationality of John Roberts. He is Chinese or John Roberts is Chinese.
				
				if (td.reln().toString().contains("nsubj")) {

					if (DictionariesInstance.getInstance().copulas
							.contains(td.gov().lemma())) {
						xcompChecker.put(Integer.toString(td.gov().index()), true);
					}
					
				}
				
				else if (td.reln().toString().equals("xcomp")) {
					if(DictionariesInstance.getInstance().copulas
							.contains(td.gov().lemma()) && xcompChecker.get(Integer.toString(td.gov().index()))) {
						xcompCounter++;
					}
				}
			}
			postChecking = postChecking(sentence.toString());
			break;
		}
		
		return postChecking && xcompCounter == xcompChecker.size();
		
	}
	
	public abstract boolean postChecking(String answer);
}
