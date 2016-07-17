package model.text_understanding;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import model.instance.JLanguageToolInstance;
import model.instance.StanfordCoreNLPInstance;
import model.story_representation.AbstractStoryRepresentation;

public class Preprocessing {

	private static Logger log = Logger.getLogger(Preprocessing.class.getName());

	private StanfordCoreNLP pipeline;
	
	private Map<String, String> coref;
	
	private String updatedText;
	
	public static void main(String[] args) {
		Preprocessing p = new Preprocessing();
		p.preprocess("Moira and Gege cried. Moira and Gege got injured.");
	}
	

	public Preprocessing() {
		this.pipeline = StanfordCoreNLPInstance.getInstance();
		this.coref = new HashMap();
		this.updatedText = "";
	}

	public void preprocess(String text) {
		//result = normalize(result);
		updatedText = doTest(text);
		coref = coreference(updatedText);
		//result = normalize(result);
	}
	
	public Map<String, String> getCoref() {
		return this.coref;
	}
	
	public String getUpdatedString() {
		return this.updatedText;
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
				log.debug(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;

	}

	private Map<String, String> coreference(String text) {

		Map<String, String> coreference = new HashMap<>();

		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		for (CorefChain cc : document
				.get(CorefCoreAnnotations.CorefChainAnnotation.class)
				.values()) {
			for (CorefMention c1 : cc.getMentionsInTextualOrder()) {
				// if (c1.mentionType.representativeness < cc
				//		.getRepresentativeMention().mentionType.representativeness
				//		|| c1.mentionType.representativeness == cc
				//				.getRepresentativeMention().mentionType.representativeness) {
					coreference.put(c1.sentNum + " " + c1.headIndex,
							cc.getRepresentativeMention().sentNum + " "
									+ cc.getRepresentativeMention().headIndex);
				//} else 
				//	/*
				//		 * (c1.mentionType.representativeness >
				//		 * cc.getRepresentativeMention().mentionType.
				//		 * representativeness)
				//		 */ {
				//	// if the other reference represents more than the main reference, flip
				//	// possible collision, just make it map with list later
				//	
				//	System.out.println("flip");
				//	
				//	coreference.put(
				//			cc.getRepresentativeMention().sentNum + " "
				//					+ cc.getRepresentativeMention().headIndex,
				//			c1.sentNum + " " + c1.headIndex);
				//}
			}
		}

		for (Map.Entry<String, String> entry : coreference.entrySet()) {
			log.debug(entry.getKey() + " : " + entry.getValue());
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
	
	private String doTest(String text){
	    Annotation doc = new Annotation(text);
	    pipeline.annotate(doc);
	
	
	    //get coref mapping
	    Map<Integer, CorefChain> corefs = doc.get(CorefChainAnnotation.class);
	    
	    //sentence segment
	    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
	
	    //whole sentence or updated sentence later
	    List<String> resolved = new ArrayList<String>();
	
	    for (CoreMap sentence : sentences) {
	
	    	//tokens for a sentence
	        List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
	
	        for (CoreLabel token : tokens) {
	
	        	//token belongs to which chain of coref
	            Integer corefClustId= token.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
	            log.debug(token.word() +  " --> corefClusterID = " + corefClustId);
	
	
	            CorefChain chain = corefs.get(corefClustId);
	            log.debug("matched chain = " + chain);
	
	
	            if(chain==null || chain.getMentionsInTextualOrder().size() == 1){
	                resolved.add(token.word());
	            }else{
	
	                int sentINdx = chain.getRepresentativeMention().sentNum -1;
	                CoreMap corefSentence = sentences.get(sentINdx);
	                List<CoreLabel> corefSentenceTokens = corefSentence.get(TokensAnnotation.class);
	
	                String newwords = "";
	                CorefMention reprMent = chain.getRepresentativeMention();
	                log.debug(reprMent);
	                
	                boolean found = false;
	                for(int i = reprMent.startIndex; i<reprMent.endIndex; i++){
	                	
	                	log.debug(reprMent.sentNum + ", " + token.sentIndex());
	                	
	                	if(reprMent.sentNum != token.sentIndex() + 1) {
	                	
		                    CoreLabel matchedLabel = corefSentenceTokens.get(i-1); //resolved.add(tokens.get(i).word());
		                    
		                    if(token.tag().contains("NN")) {
		                    	
		                    	if (!found) {

		                			resolved.add(token.word());
		                			found = true;
		                		}
		                    	continue;
		                    }
		                    
		                    if(i != reprMent.endIndex -1) {
		                    	resolved.add(matchedLabel.word());
		        				
		 	                    newwords+=matchedLabel.word();
		                    }
		                    
		                    else {
		                    	if(matchedLabel.tag().equals("POS")) {
		                    		if(token.tag().equals("PRP$")) {
				                    	resolved.add(matchedLabel.word());
				        				
				 	                    newwords+=matchedLabel.word();
		                    		}
		                    		else {
		                    			
		                    		}
			                    }
		                    	else if(token.tag().equals("PRP$")) {
		                    		log.debug("WHY");
		                    		
		                    		resolved.add(reprMent.mentionSpan + "'s");
			        				
			 	                    newwords+=reprMent.mentionSpan + "'s ";
		                    	}
		                    	
		                    	else {
		                    		resolved.add(matchedLabel.word());
			        				
			 	                    newwords+=matchedLabel.word();
		                    	}
		                    }
		                }

	                	else {
	                		
	                		CoreLabel matchedLabel = corefSentenceTokens.get(i-1); 
                			
               			 	if(token.tag().equals("PRP$")) {
		                    		
               			 		if(i == reprMent.endIndex - 1) {
               			 			resolved.add(reprMent.mentionSpan + "'s");
		        				
               			 			newwords+=reprMent.mentionSpan + "'s ";
               			 		}
		                    	
		                    }
	                		
               			 	else if (!found) {

	                			resolved.add(token.word());
	                			found = true;
	                		}
	                	}
	                }

	                log.debug("converting " + token.word() + " to " + newwords);
	            }
	
	
	            log.debug("");
	            log.debug("");
	            log.debug("-----------------------------------------------------------------");
	
	        }
	
	    }
	
	
	    String resolvedStr ="";
	    log.debug("");
	    for (String str : resolved) {
	        resolvedStr+=str+" ";
	    }
	    log.debug(resolvedStr);
	    
	    return resolvedStr;
	
	}

}
