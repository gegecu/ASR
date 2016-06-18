package model.text_understanding;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import model.instance.JLanguageToolInstance;
import model.instance.StanfordCoreNLPInstance;
import model.story_representation.AbstractStoryRepresentation;

public class Preprocessing {

	private StanfordCoreNLP pipeline;
	private AbstractStoryRepresentation asr;

	public Preprocessing(AbstractStoryRepresentation asr) {
		this.asr = asr;
		this.pipeline = StanfordCoreNLPInstance.getInstance();
	}

	public Map<String, String> preprocess(String text) {
		//result = normalize(result);
		return coreference(text, asr);
		//result = normalize(result);
	}

	private String normalize(String text) {

		JLanguageTool langTool = JLanguageToolInstance.getInstance();
		String input = text;
		List<RuleMatch> matches;

		try {
			matches = langTool.check(input);
			RuleMatch match = matches.get(0);
			while (!matches.isEmpty()
					&& !match.getSuggestedReplacements().isEmpty()) {
				input = input.substring(0, match.getColumn() - 1)
						+ match.getSuggestedReplacements().get(0)
						+ input.substring(match.getEndColumn() - 1);
				matches = langTool.check(input);
				System.out.println(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;

	}

	private Map<String, String> coreference(String text,
			AbstractStoryRepresentation asr) {

		Map<String, String> coreference = new HashMap<>();

		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		for (CorefChain cc : document
				.get(CorefCoreAnnotations.CorefChainAnnotation.class)
				.values()) {
			for (CorefMention c1 : cc.getMentionsInTextualOrder()) {
				if(c1.mentionType.representativeness < cc.getRepresentativeMention().mentionType.representativeness ||
						c1.mentionType.representativeness == cc.getRepresentativeMention().mentionType.representativeness) {
					coreference.put(c1.sentNum + " " + c1.headIndex,
							cc.getRepresentativeMention().sentNum + " "
									+ cc.getRepresentativeMention().headIndex);
				}
				else /*(c1.mentionType.representativeness > cc.getRepresentativeMention().mentionType.representativeness) */ {
					// if the other reference represents more than the main reference, flip
					// possible collision, just make it map with list later
					System.out.println("woo");
					coreference.put(cc.getRepresentativeMention().sentNum + " "
							+ cc.getRepresentativeMention().headIndex,
							c1.sentNum + " " + c1.headIndex);
				}
			}
		}

		for (Map.Entry<String, String> entry : coreference.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		return coreference;

		//		String output = "";
		//		Annotation document = new Annotation(text);
		//		pipeline.annotate(document);
		//
		//		List<Sentence> sens = new ArrayList<Sentence>();
		//		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		//
		//		for (CoreMap sentence : sentences) {
		//			Sentence s = new Sentence();
		//			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
		//				String word = token.get(TextAnnotation.class);
		//				String pos = token.get(PartOfSpeechAnnotation.class);
		//				Word w = new Word();
		//				w.addInfo("text", word);
		//				w.addInfo("pos", pos);
		//				s.addWord(token.index(), w);
		//			}
		//			sens.add(s);
		//		}
		//		
		//		 for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
		//
		//			String mainRef = cc.getRepresentativeMention().mentionSpan;
		//			int num = cc.getRepresentativeMention().sentNum;
		//			MentionType type = cc.getRepresentativeMention().mentionType;
		//			
		//			for (CorefMention c1 : cc.getMentionsInTextualOrder()) {
		//				Sentence newSen = sens.get(c1.sentNum - 1);
		//				if (num != c1.sentNum && type.representativeness > c1.mentionType.representativeness) {
		//					if (newSen.getWord(c1.startIndex).getInfo("pos").equals("PRP$")) {
		//						newSen.getWord(c1.startIndex).addInfo("text", mainRef + "'s");
		//					} else {
		//						newSen.getWord(c1.startIndex).addInfo("text", mainRef);
		//					}
		//					if (c1.endIndex > c1.startIndex + 1) {
		//						newSen.removeWord(c1.endIndex - 1);
		//					}
		//				}
		//
		//			}
		//		}
		//		 
		//		for (Sentence sen : sens) {
		//			output += sen.getWholeSentence();
		//		}
		//
		//		if (output.length() == 0) {
		//			return text;
		//		} else {
		//			return output;
		//		}

	}

}
