package model.text_understanding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.dcoref.Dictionaries.MentionType;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Preprocessing {
	private Properties properties;
	private StanfordCoreNLP pipeline;

	public Preprocessing(Properties properties, StanfordCoreNLP pipeline) {
		this.properties = properties;
		this.pipeline = pipeline;
	}

	public String preprocess(String text) {
		String result = text;
		
		//result = normalize(result);
		result = coreference(result);
		//result = normalize(result);
		
		System.out.println(result);
		return result;
	}

	private String normalize(String text) {
		JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
		//langTool.activateDefaultPatternRules();  -- only needed for LT 2.8 or earlier
		String input = text;
		List<RuleMatch> matches;
		try {
			matches = langTool.check(input);
			while(!matches.isEmpty() && !matches.get(0).getSuggestedReplacements().isEmpty()) {
				input = input.substring(0, matches.get(0).getColumn()-1) + matches.get(0).getSuggestedReplacements().get(0) + input.substring(matches.get(0).getEndColumn()-1);
				matches = langTool.check(input);
				System.out.println(input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return input;
	}

	private String coreference(String text) {
		String output = "";
		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		List<Sentence> sens = new ArrayList();
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			Sentence s = new Sentence();
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				// String ne = token.get(NamedEntityTagAnnotation.class);

				Word w = new Word();
				w.addInfo("text", word);
				w.addInfo("pos", pos);
				s.addWord(token.index(), w);
			}
			sens.add(s);

		}
		
		//System.out.println(document.get(CorefCoreAnnotations.CorefChainAnnotation.class));
		 for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
		     
			// System.out.println(entry.getValue());
			String mainRef = cc.getRepresentativeMention().mentionSpan;
			int num = cc.getRepresentativeMention().sentNum;
			MentionType type = cc.getRepresentativeMention().mentionType;
			// System.out.println(mainRef + ", " + type.representativeness);
			for (CorefMention c1 : cc.getMentionsInTextualOrder()) {

				// System.out.println(c1.mentionSpan + ", " + c1.startIndex + ",
				// " + c1.endIndex);

				String refFound = c1.mentionSpan;
				// System.out.println(c1.mentionType.representativeness);
				Sentence newSen = sens.get(c1.sentNum - 1);
				if (type.representativeness > c1.mentionType.representativeness) {
					if (newSen.getWord(c1.startIndex).getInfo("pos").equals("PRP$")) {
						newSen.getWord(c1.startIndex).addInfo("text", mainRef + "'s");
					} else {
						// System.out.println(newSen.getWord(c1.startIndex).getInfo("text"));
						newSen.getWord(c1.startIndex).addInfo("text", mainRef);
					}
					if (c1.endIndex > c1.startIndex + 1) {
						newSen.removeWord(c1.endIndex - 1);
					}
				}

			}
		}
		 
		for (Sentence sen : sens) {
			output += sen.getWholeSentence();
		}

		if (output.length() == 0) {
			return text;
		} else {
			return output;
		}

	}

	private List<String> segmentStorySegment(String text) {
		// pero meron rin segmenter ng sentence un jLanguageTool
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<String> stringSentences = new ArrayList();

		for (CoreMap sentence : sentences) {
			stringSentences.add(sentence.toString());
		}

		return stringSentences;
	}

}
