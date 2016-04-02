package model.text_understanding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import model.story_representation.AbstractStoryRepresentation;

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
	private StanfordCoreNLP pipeline;

	public Preprocessing(StanfordCoreNLP pipeline) {
		this.pipeline = pipeline;
	}

	public String preprocess(String text, AbstractStoryRepresentation asr) {
		String result = text;
		
		//result = normalize(result);
		result = coreference(result, asr);
		//result = normalize(result);
		return result;
	}

	private String normalize(String text) {
		JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
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
			e.printStackTrace();
		}
		
		return input;
	}

	private String coreference(String text, AbstractStoryRepresentation asr) {
		String output = "";
		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		List<Sentence> sens = new ArrayList<Sentence>();
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			Sentence s = new Sentence();
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				Word w = new Word();
				w.addInfo("text", word);
				w.addInfo("pos", pos);
				s.addWord(token.index(), w);
			}
			sens.add(s);
		}
		
		 for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {

			String mainRef = cc.getRepresentativeMention().mentionSpan;
			int num = cc.getRepresentativeMention().sentNum;
			MentionType type = cc.getRepresentativeMention().mentionType;
			
			for (CorefMention c1 : cc.getMentionsInTextualOrder()) {
				Sentence newSen = sens.get(c1.sentNum - 1);
				if (num != c1.sentNum && type.representativeness > c1.mentionType.representativeness) {
					if (newSen.getWord(c1.startIndex).getInfo("pos").equals("PRP$")) {
						newSen.getWord(c1.startIndex).addInfo("text", mainRef + "'s");
					} else {
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

}
