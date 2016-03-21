package model.text_understanding.preprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import semantic_parser.concept_parser;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.senticnet.ConceptParser;
import model.knowledge_base.senticnet.SenticNetParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Event;
import model.story_representation.Predicate;
import model.story_representation.noun.Character;
import model.story_representation.noun.Location;
import model.story_representation.noun.Noun;
import model.story_representation.noun.Object;
import model.story_representation.noun.Unknown;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class Extractor {
	private Properties properties;
	private StanfordCoreNLP pipeline;
	private AbstractSequenceClassifier classifier;
	private ConceptParser cp;
	private SenticNetParser snp;

	private String prevDep;
	private String prevDepTag;
	private String prevGov;
	private String prevGovTag;
	private String prevReln;
	
	public Extractor(Properties properties, StanfordCoreNLP pipeline) {
		this.properties = properties;
		this.pipeline = pipeline;
		this.classifier = CRFClassifier.getDefaultClassifier();
		this.cp = new ConceptParser();
		this.snp = new SenticNetParser();
	}

	public void extract(String text, AbstractStoryRepresentation asr) {
		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {

			// this is the parse tree of the current sentence
			//Tree tree = sentence.get(TreeAnnotation.class);
			//System.out.println("\nTREE: " + tree + "\n");

			// this is the Stanford dependency graph of the current sentence
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);

			System.out.println(sentence.toString());
			
			Event event = new Event();
			for (TypedDependency td : dependencies.typedDependencies()) {
				extractDependency(td, asr, event);
			}
			event.setPolarity(this.getPolarityOfEvent(sentence.toString()));
			
			asr.addEvent(event);
			
			//System.out.println(sentence.toString() + ", " + this.getPolarityOfEvent(sentence.toString()));
		}
	}
	
	private void extractDependency(TypedDependency td, AbstractStoryRepresentation asr, Event event) {
		try {
				String tdDepTag = td.dep().tag();
				String tdGovTag = td.gov().tag();
				String tdReln = td.reln().toString();

				System.out.println(td.dep().originalText() +":"
									+ tdDepTag + ", " 
									+ td.gov().originalText() + ":"
									+ tdGovTag + ",  " 
									+ tdReln );
				
				if(tdReln.equals("compound")) {
					boolean properNouns = tdDepTag.equals("NNP") && tdGovTag.equals("NNP");
					boolean commonNouns = tdDepTag.equals("NN") && tdGovTag.contains("NN");
					String name = td.dep().originalText() + " " + td.gov().originalText();
					
					if(asr.getNoun(td.gov().originalText()) == null) {
						if(properNouns) {
							asr.addNoun(td.gov().originalText(), this.extractCategory(this.getNER(name), name));
						}
						else if(commonNouns) {
							asr.addNoun(td.gov().originalText(), this.extractCategory(this.getSRL(name), name));
						}
					}
					
					System.out.println("compound: " + asr.getNoun(td.gov().originalText()).getId());
				}
				
				else if(tdReln.equals("nsubj")) {
					
					Noun noun = asr.getNoun(td.dep().originalText());
					
					if(noun == null) {
						if (tdDepTag.equals("NNP")) {
							noun = this.extractCategory(this.getNER(td.dep().originalText()), td.dep().originalText());
						}
						else if (tdDepTag.contains("NN")) {
							noun = this.extractCategory(this.getSRL(td.dep().originalText()), td.dep().originalText());
						}
						
						asr.addNoun(td.dep().originalText(), noun);
					}
					
					if(tdGovTag.equals("JJ")) {
						
						noun.addAttribute("hasProperty", td.gov().originalText());
						//event.addDoer(td.dep().originalText(), noun);
						System.out.println(noun.getId() + " hasProperty " + td.gov().originalText());
					}
					
					else if(tdGovTag.contains("VB")) {
						
						noun.addAttribute("capableOf", td.gov().lemma());
						event.addDoer(td.dep().originalText(), noun);
						
						Predicate predicate = event.getPredicate(td.gov().lemma());
						
						if(predicate == null) {
							predicate = new Predicate(td.gov().lemma());
						}
						
						event.addPredicate(predicate);
						System.out.println(noun.getId() + " capable of " + td.gov().lemma());
						//unsure if verb for event or capableOf
					}
				}
				
				else if(tdReln.equals("iobj")) {
					if(tdDepTag.contains("NN") && tdGovTag.contains("VB")) {
						
						Noun noun = asr.getNoun(td.dep().originalText());
						
						if(noun == null) {
							if (tdDepTag.equals("NNP")) {
								noun = this.extractCategory(this.getNER(td.dep().originalText()), td.dep().originalText());
							}
							else if (tdDepTag.contains("NN")) {
								noun = this.extractCategory(this.getSRL(td.dep().originalText()), td.dep().originalText());
							}
							
							asr.addNoun(td.dep().originalText(), noun);
						}
						
						Predicate predicate = event.getPredicate(td.gov().lemma());
						
						if(predicate == null) {
							predicate = new Predicate(td.gov().lemma());
						}
						predicate.addReceiver(td.dep().originalText(), noun);
						event.addPredicate(predicate);
						
						//event.getPredicate(td.gov().lemma()).addReceiver(td.dep().originalText(), noun);

						System.out.println("iobj: " + asr.getNoun(td.dep().originalText()).getId());
					}
				}
				
				else if (tdReln.equals("dobj") || tdReln.equals("nmod:for")) {
					// object?
					// Mary and Samantha took the bus.
					// dobj ( took-4 , bus-6 ) 
					// new object
					
					Noun noun = asr.getNoun(td.dep().originalText());
					
					if(noun == null) {
						if (tdDepTag.equals("NNP")) {
							noun = this.extractCategory(this.getNER(td.dep().originalText()), td.dep().originalText());
						}
						else if (tdDepTag.contains("NN")) {
							noun = this.extractCategory(this.getSRL(td.dep().originalText()), td.dep().originalText());
						}
						
						asr.addNoun(td.dep().originalText(), noun);
					}
					
					Predicate predicate = event.getPredicate(td.gov().lemma());
					
					if(predicate == null) {
						predicate = new Predicate(td.gov().lemma());
					}
					predicate.addDirectObject(td.dep().originalText(), noun);
					event.addPredicate(predicate);
					
					System.out.println("dobj: " + event.getPredicate(td.gov().lemma()).getDirectObject(td.dep().originalText()).getId());
				}
				
//				else if (tdReln.equals("ccomp")) {
//
//					if(tdDepTag.equals("VBN")
//						&& tdGovTag.equals("VBD")) {
//						System.out.println("CREATE 2 EVENTS");
//					}
//
//				}

//				else if (tdReln.equals("advcl")) {
//
//					if(tdDepTag.equals("VBD")
//						&& tdGovTag.equals("VBD")) {
//						System.out.println("CREATE 2 EVENTS");
//					}
//					
//				}				

//				else if (tdReln.equals("advmod")) {
//					// adj
//					// the train was late.
//					// advmod ( was-3 , late-4 ) 
//					
//					
//				}
//				else if (tdReln.equals("nmod:until")) {
//					//time
//				}
				else if (tdReln.equals("nmod:at")
					|| tdReln.equals("nmod:near")
					|| tdReln.equals("nmod:to")
					|| tdReln.equals("nmod:in")
					|| tdReln.equals("nmod:on")) {
					
					Noun noun = asr.getNoun(td.dep().originalText());
					
					if(noun == null) {
						if (tdDepTag.equals("NNP")) {
							noun = this.extractCategory(this.getNER(td.dep().originalText()), td.dep().originalText());
						}
						else if (tdDepTag.contains("NN")) {
							noun = this.extractCategory(this.getSRL(td.dep().originalText()), td.dep().originalText());
						}
						
						asr.addNoun(td.dep().originalText(), noun);
					}
					
					if(noun instanceof Location) {
						event.setLocation((Location) noun);
					}

					System.out.println("Location: " + td.dep().originalText());
				}
//				else if (!(tdReln.equals("nmod:near") || tdReln.equals("nmod:at") || tdReln.equals("nmod:in")
//								|| tdReln.equals("nmod:on") || tdReln.equals("nmod:to")
//								|| tdReln.equals("dobj")
//								|| tdReln.equals("nsubj"))) {
//
//					if ((tdDepTag.equals("NNP") 
//							|| tdDepTag.equals("NN")) 
//						&& tdGovTag.equals("VBD")) {
//
//						// adds new doer with what he/she is capable of
//						//event.getDoer(td.dep().originalText()).addAttribute("capable of", td.gov().lemma());
//						
//					}
//				}
//			
//				prevDep = td.dep().originalText(); 
//				prevDepTag = tdDepTag;
//				prevGov = td.gov().originalText();
//				prevGovTag = tdGovTag;
//				prevReln = tdReln;
				
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private Noun extractCategory(String category, String word) {
		switch (category) {
			case "PERSON" :
				return new Character(word);
		case "ORGANIZATION" :
				return new Location(word);
			case "LOCATION" :
				return new Location(word);
			case "OBJECT" :
				return new Object(word);
			case "UNKNOWN" :
				return new Unknown(word);
			default :
				return null;
		}
	}
	
	private String getSRL(String text) {
		String[] ENTITY_LIST = { "person", "location", "object"};
		
		for (String entityValue : ENTITY_LIST) {
			
			if(ConceptNetDAO.checkSRL(text, "isA", entityValue)) {
				return entityValue.toUpperCase();
			}	
			
		}
		return "UNKNOWN";
	}
	

	private String getNER(String text) {
		// http://blog.thedigitalgroup.com/sagarg/2015/06/26/named-entity-recognition/
		String output = classifier.classifyToString(text);

		String[] ENTITY_LIST = { "person", "location", "date", "organization", "time", "money", "percentage" };

		try {
			for (String entityValue : ENTITY_LIST) {

				String entity = entityValue.toUpperCase();
				Pattern pattern = Pattern
						.compile("([a-zA-Z0-9.%]+(/" + entity
							+ ")[ ]*)*[a-zA-Z0-9.%]+(/" + entity + ")");
				Matcher matcher = pattern.matcher(output);
				while (matcher.find()) {
//					int start = matcher.start();
//					int end = matcher.end();
//					String inputText = output.substring(start, end);
//					inputText = inputText.replaceAll("/" + entity, "");
					System.out.println(entity);
					return entity;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "UNKNOWN";
	}
	
	private float getPolarityOfEvent(String sentence) {
		List<String> concepts = cp.getConcepts(sentence);
		//System.out.println(concepts);
		float worstPolarity = snp.getPolarity(concepts.remove(0).replace(" ", "_"));
		float polarity = 0;
		while(!concepts.isEmpty()) {
			polarity = snp.getPolarity(concepts.remove(0).replace(" ", "_"));
			if(polarity < worstPolarity) {
				worstPolarity = polarity;
			}
		}
		return worstPolarity;
	}
}
