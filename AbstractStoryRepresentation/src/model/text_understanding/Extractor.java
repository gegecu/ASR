package model.text_understanding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import semantic_parser.concept_parser;
import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.senticnet.ConceptParser;
import model.knowledge_base.senticnet.ManualConceptParser;
import model.knowledge_base.senticnet.SenticNetParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Object;
import model.story_representation.story_element.noun.Unknown;
import model.story_representation.story_element.story_sentence.Predicate;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.States;
import model.utility.TypedDependencyComparator;
import edu.stanford.nlp.dcoref.Dictionaries;
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
	private Dictionaries dictionary;

	public Extractor(Properties properties, StanfordCoreNLP pipeline) {
		this.properties = properties;
		this.pipeline = pipeline;
		this.classifier = CRFClassifier.getDefaultClassifier();
		this.cp = new ConceptParser();
		this.snp = new SenticNetParser();
		this.dictionary = new Dictionaries();
	}

	public void extract(String text, AbstractStoryRepresentation asr) {
		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {

			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);

			System.out.println(sentence.toString());

			StorySentence storySentence = new StorySentence();
			List<TypedDependency> listDependencies = new ArrayList(dependencies.typedDependencies());
			Collections.sort(listDependencies, new TypedDependencyComparator());
			
			ManualConceptParser conceptParser = new ManualConceptParser();//builds a new conceptParser for each sentence
			for (TypedDependency td : listDependencies) { 
				extractDependency(td, asr, storySentence, conceptParser);//extract based on dependency
			}
			//storySentence.setConcept(this.cp.getConcepts(sentence.toString())); //change to manual concept parsing
			//storySentence.setVerbConcepts(conceptParser.getVerbConcepts()); //extract concepts from conceptParser
			//storySentence.setAdjectiveConcepts(conceptParser.getAdjectiveConcepts());
			storySentence.setPolarity(this.getPolarityOfEvent(storySentence));

			asr.addEvent(storySentence);
		}
	}

	//private void 
	private void extractDependency(TypedDependency td, AbstractStoryRepresentation asr, StorySentence storySentence, ManualConceptParser conceptParser) {
		try {
			String tdDepTag = td.dep().tag();
			String tdGovTag = td.gov().tag();
			String tdReln = td.reln().toString();

			System.out.println("extracting:..." + td.dep().originalText() + ":" + tdDepTag + ", " + td.gov().originalText() + ":"
					+ tdGovTag + ",  " + tdReln);

			if (tdReln.equals("compound")) {
				boolean properNouns = tdDepTag.equals("NNP") && tdGovTag.equals("NNP");
				boolean commonNouns = tdDepTag.equals("NN") && tdGovTag.contains("NN");
				String name = td.dep().lemma() + " " + td.gov().lemma();

				Noun noun = asr.getNoun(td.gov().lemma());

				if (noun == null) {
					if (properNouns) {
						noun = this.extractCategory(this.getNER(name), name);
						noun.setProper();
					} else if (commonNouns) {
						noun = this.extractCategory(this.getSRL(name), name);
					}
				} else {
					noun.setId(name);
				}

				if(noun != null)
					asr.addNoun(td.gov().lemma(), noun);

				System.out.println("compound: " + asr.getNoun(td.gov().lemma()).getId());
			}
			
			else if (tdReln.contains("nsubj")) {

				Noun noun = asr.getNoun(td.dep().lemma());

				if (noun == null) { 
					if (tdDepTag.equals("NNP")) {
						noun = this.extractCategory(this.getNER(td.dep().lemma()), td.dep().lemma());
						noun.setProper();
					} else if (tdDepTag.contains("NN")) {
						noun = this.extractCategory(this.getSRL(td.dep().lemma()), td.dep().lemma());
					}

					if(noun != null)
					asr.addNoun(td.dep().lemma(), noun);
				}
				
				if(noun != null) { /**if noun exists**/
					//storySentence.addDoer(td.dep().lemma(), noun);
	
					if (tdGovTag.equals("JJ")) { /**if 'noun is adjective' format**/
	
						// if(States.STATES.contains(td.gov().lemma())) {
						// if(noun instanceof Character) {
						// ((Character) noun).setState(td.gov().lemma());
						// }
						// }
						noun.addAttribute("HasProperty", td.gov().lemma());
						storySentence.addAttribute(noun.getId(), "HasProperty", td.gov().lemma());
						
						//create concept
						storySentence.addNounSpecificConcept(noun.getId(), conceptParser.createConceptAsAdjective(td.gov().lemma()));
						storySentence.addNounSpecificConcept(noun.getId(),conceptParser.createConceptAsPredicativeAdjective(td.gov().lemma()));
						//conceptParser.createConceptsFromAdjective(td.gov().lemma());
						System.out.println(noun.getId() + " hasProperty " + td.gov().lemma());
					}
	
					else if (tdGovTag.contains("VB")) { /**if verb**/
	
						boolean hasARelation = td.gov().lemma().equals("has") || td.gov().lemma().equals("have");
	
						if (!hasARelation || !dictionary.copulas.contains(td.gov().lemma())) {
							noun.addAttribute("CapableOf", td.gov().lemma());
							System.out.println(noun.getId() + " capable of " + td.gov().lemma());
						}
	
						Predicate predicate = storySentence.getPredicate(td.gov().index());

						if (predicate == null) {
							predicate = new Predicate(td.gov().lemma());
						}
						predicate.addDoer(td.dep().lemma(), noun);
						storySentence.addPredicate(td.gov().index(), predicate);
						
						//create concept
						predicate.addVerbConcept(conceptParser.createConceptAsVerb(td.gov().lemma()));
						
					}						
					// unsure if verb for event or capableOf
				}
	
				else if (tdGovTag.contains("NN")) { /**for isA type relation**/
					Noun noun2 = asr.getNoun(td.gov().lemma());
					if (noun2 == null) {
						if (tdGovTag.equals("NNP")) {
							noun2 = this.extractCategory(this.getNER(td.gov().lemma()), td.gov().lemma());
							noun2.setProper();
						} else if (tdGovTag.contains("NN")) {
							noun2 = this.extractCategory(this.getSRL(td.gov().lemma()), td.gov().lemma());
						}
	
						asr.addNoun(td.gov().lemma(), noun2);
					}
	
					noun.addReference("IsA", noun2);
					storySentence.addReferences(noun.getId(), "IsA", noun2);
				}
			}
		

			else if (tdReln.equals("iobj")) { /**indirect object**/
				if (tdDepTag.contains("NN") && tdGovTag.contains("VB")) {

					Noun noun = asr.getNoun(td.dep().lemma());

					if (noun == null) { 
						if (tdDepTag.equals("NNP")) {
							noun = this.extractCategory(this.getNER(td.dep().lemma()), td.dep().lemma());
							noun.setProper();
						} else if (tdDepTag.contains("NN")) {
							noun = this.extractCategory(this.getSRL(td.dep().lemma()), td.dep().lemma());
						}

						asr.addNoun(td.dep().lemma(), noun);
					}

					Predicate predicate = storySentence.getPredicate(td.gov().index());

					if (predicate == null) {
						predicate = new Predicate(td.gov().lemma());
					}
					predicate.addReceiver(td.dep().lemma(), noun);
					storySentence.addPredicate(td.gov().index(), predicate);

					// event.getPredicate(td.gov().lemma()).addReceiver(td.dep().originalText(),
					// noun);

					System.out.println("iobj: " + asr.getNoun(td.dep().lemma()).getId());
				}
			}
			
			else if (tdReln.equals("nmod:of")) { /** hasA relation **/
				
				Noun noun = asr.getNoun(td.dep().lemma());

				if (noun == null) {
					if (tdDepTag.equals("NNP")) {
						noun = this.extractCategory(this.getNER(td.dep().lemma()), td.dep().lemma());
						noun.setProper();
					} else if (tdDepTag.contains("NN")) {
						noun = this.extractCategory(this.getSRL(td.dep().lemma()), td.dep().lemma());
					}

					asr.addNoun(td.dep().lemma(), noun);
				}
				
				Noun noun2 = asr.getNoun(td.gov().lemma());

				if (noun2 == null) {
					if (tdGovTag.equals("NNP")) {
						noun = this.extractCategory(this.getNER(td.dep().lemma()), td.dep().lemma());
						noun.setProper();
					} else if (tdGovTag.contains("NN")) {
						noun = this.extractCategory(this.getSRL(td.dep().lemma()), td.dep().lemma());
					}

					asr.addNoun(td.dep().lemma(), noun);
				}
				
				if(noun != null && noun2 != null) {
					noun.addReference("HasA", noun2);
					storySentence.addReferences(noun.getId(), "HasA", noun2);
				}
			}

			else if (tdReln.equals("dobj") || tdReln.equals("nmod:for") || tdReln.equals("nmod:agent")) {
				// object?
				// Mary and Samantha took the bus.
				// dobj ( took-4 , bus-6 )
				// new object

				Noun noun = asr.getNoun(td.dep().lemma());

				if (noun == null) {
					if (tdDepTag.equals("NNP")) {
						noun = this.extractCategory(this.getNER(td.dep().lemma()), td.dep().lemma());
						noun.setProper();
					} else if (tdDepTag.contains("NN")) {
						noun = this.extractCategory(this.getSRL(td.dep().lemma()), td.dep().lemma());
					}

					asr.addNoun(td.dep().lemma(), noun);
				}

				Predicate predicate = storySentence.getPredicate(td.gov().index());

				if (predicate == null) {
					predicate = new Predicate(td.gov().lemma());
				}
				
				if(noun != null)
					predicate.addDirectObject(td.dep().lemma(), noun);
					
				
				storySentence.addPredicate(td.gov().index(), predicate);

				if (td.gov().lemma().equals("has") || td.gov().lemma().equals("have")) {
					// System.out.println("remove: " +
					// storySentence.getManyPredicates().remove(predicate.getAction()));
					
					for(Noun n: storySentence.getPredicate(td.gov().index()).getManyDoers().values()) {
						if (noun != null) {
							n.addReference("HasA", noun);
							storySentence.addReferences(n.getId(), "HasA", noun);
						}
					}
					storySentence.getManyPredicates().remove(td.gov().index());
					
				} else {
					System.out.println("dobj: "
							+ storySentence.getPredicate(td.gov().index()).getDirectObject(td.dep().lemma()).getId());
				}
				
				//create concept
				String object = td.dep().lemma();				
				if(noun instanceof Character) //if direct object is a person, change to someone
					predicate.addVerbConcept(conceptParser.createConceptWithDirectObject(td.gov().lemma(), "someone"));
				else {
					predicate.addVerbConcept(conceptParser.createConceptWithDirectObject(td.gov().lemma(), object));
				}
			}

			else if (tdReln.equals("xcomp") && dictionary.copulas.contains(td.gov().lemma())) {

				for(Noun n: storySentence.getPredicate(td.gov().index()).getManyDoers().values()) {
					if (tdDepTag.equals("JJ")) {
						n.addAttribute("HasProperty", td.dep().lemma());
						storySentence.addAttribute(n.getId(), "HasProperty", td.dep().lemma());
						
						storySentence.addNounSpecificConcept(n.getId(), conceptParser.createConceptAsAdjective(td.gov().lemma()));
						storySentence.addNounSpecificConcept(n.getId(),conceptParser.createConceptAsPredicativeAdjective(td.gov().lemma()));
						
					} else if (tdDepTag.contains("NN")) {
						// n.addAttribute("IsA", td.dep().lemma());
						Noun noun2 = asr.getNoun(td.dep().lemma());
						if (noun2 == null) {
							if (tdDepTag.equals("NNP")) {
								noun2 = this.extractCategory(this.getNER(td.dep().lemma()), td.dep().lemma());
								noun2.setProper();
							} else if (tdDepTag.contains("NN")) {
								noun2 = this.extractCategory(this.getSRL(td.dep().lemma()), td.dep().lemma());
							}

							asr.addNoun(td.dep().lemma(), noun2);
						}

						if(noun2 != null) {
							n.addReference("IsA", noun2);
							storySentence.addReferences(n.getId(), "IsA", noun2);
						}
					}
				}
				storySentence.getManyPredicates().remove(td.gov().index());
			}

			else if (tdReln.equals("amod")) { /** 'adjective noun' format **/
				Noun noun = asr.getNoun(td.gov().lemma());

				if (noun == null) {
					if (tdGovTag.equals("NNP")) {
						noun = this.extractCategory(this.getNER(td.gov().lemma()), td.gov().lemma());
						noun.setProper();
					} else if (tdGovTag.contains("NN")) {
						noun = this.extractCategory(this.getSRL(td.gov().lemma()), td.gov().lemma());
					}

					asr.addNoun(td.gov().lemma(), noun);
				}

				noun.addAttribute("HasProperty", td.dep().lemma());
				System.out.println("amod " + noun.getId());
				storySentence.addAttribute(noun.getId(), "HasProperty", td.dep().lemma());
				
				//create concept
				storySentence.addNounSpecificConcept(noun.getId(),conceptParser.createConceptAsAdjective(td.dep().lemma()));
				storySentence.addNounSpecificConcept(noun.getId(),conceptParser.createConceptAsPredicativeAdjective(td.dep().lemma()));
				
			}

			else if (tdReln.equals("advmod")) {
				if (tdDepTag.equals("RB")) {
					if(dictionary.copulas.contains(td.gov().lemma())) {
						for(Noun n: storySentence.getPredicate(td.gov().index()).getManyDoers().values()) {
							n.addAttribute("HasProperty", td.dep().lemma());
							storySentence.addAttribute(n.getId(), "HasProperty", td.dep().lemma());
						}	
					}
					else {
						Predicate predicate = storySentence.getPredicate(td.gov().index());

						if (predicate == null) {
							predicate = new Predicate(td.gov().lemma());
						}
						
						predicate.addAdverb(td.dep().lemma());
						
					}
					
				}
				
			}

			// else if (tdReln.equals("advcl")) {
			//
			// if(tdDepTag.equals("VBD")
			// && tdGovTag.equals("VBD")) {
			// System.out.println("CREATE 2 EVENTS");
			// }
			//
			// }

			// else if (tdReln.equals("advmod")) {
			// // adj
			// // the train was late.
			// // advmod ( was-3 , late-4 )
			//
			//
			// }
			// else if (tdReln.equals("nmod:until")) {
			// //time
			// }
			else if (tdReln.equals("nmod:at") || tdReln.equals("nmod:near") || tdReln.equals("nmod:to")
					|| tdReln.equals("nmod:in") || tdReln.equals("nmod:on")) {
				System.out.println("");
				Noun noun = asr.getNoun(td.dep().lemma());

				if (noun == null) {
					if (tdDepTag.equals("NNP")) {
						noun = this.extractCategory(this.getNER(td.dep().lemma()), td.dep().lemma());
						noun.setProper();
					} else if (tdDepTag.contains("NN")) {
						noun = this.extractCategory(this.getSRL(td.dep().lemma()), td.dep().lemma());
					}

					asr.addNoun(td.dep().lemma(), noun);
				}

				if (noun != null && noun instanceof Location) {
					for (Noun n : storySentence.getPredicate(td.gov().index()).getManyDoers().values()) {
						n.addReference("AtLocation", noun);
						storySentence.addReferences(n.getId(), "AtLocation", noun);
					}
					
					
					Predicate predicate = storySentence.getPredicate(td.gov().index());

					if (predicate == null) {
						predicate = new Predicate(td.gov().lemma());
					}
					
					predicate.addDirectObject(noun.getId(), noun);
				}
				
				System.out.println("Location: " + td.dep().lemma());
				if(tdReln.equals("nmod:to") && tdGovTag.contains("VB")){
					//create concept
					Predicate predicate = storySentence.getPredicate(td.gov().index());
					if (predicate == null) {
						predicate = new Predicate(td.gov().lemma());
						storySentence.addPredicate(td.gov().index(), predicate);
					}
					predicate.addVerbConcept(conceptParser.createConceptAsInfinitive(td.gov().lemma(),td.dep().lemma()));
					
				}
				
				
			}

//			else if (tdReln.equals("nmod:poss")) {
//				//Moira's ball is blue
//				//nmod:poss ( ball-3 , Moira-1 ) Moira is dep, ball is gov
//				
//				if (tdDepTag.contains("NN") && tdGovTag.contains("NN")) {
//					Noun noun = asr.getNoun(td.dep().lemma());
//
//					if (noun == null) {
//						if (tdDepTag.equals("NNP")) {
//							noun = this.extractCategory(this.getNER(td.dep().lemma()), td.dep().lemma());
//							noun.setProper();
//						} else if (tdDepTag.contains("NN")) {
//							noun = this.extractCategory(this.getSRL(td.dep().lemma()), td.dep().lemma());
//						}
//
//						asr.addNoun(td.dep().lemma(), noun);
//					}
//
//					Noun noun2 = asr.getNoun(td.gov().lemma());
//					if (noun2 == null) {
//						if (tdGovTag.equals("NNP")) {
//							noun2 = this.extractCategory(this.getNER(td.gov().lemma()), td.gov().lemma());
//							noun2.setProper();
//						} else if (tdGovTag.contains("NN")) {
//							noun2 = this.extractCategory(this.getSRL(td.gov().lemma()), td.gov().lemma());
//						}
//
//						asr.addNoun(td.gov().lemma(), noun2);
//					}
//					
//					Noun temp = null;
//					if(noun.getReference("HasA") != null) {
//						for(Noun n: noun.getReference("HasA")) {
//							if(n.getId().equals(td.gov().lemma())) {
//								temp = n;
//							}
//						}
//					}
//					
//					if (temp == null) {
//						if (tdGovTag.equals("NNP")) {
//							temp = this.extractCategory(this.getNER(td.gov().lemma()), td.gov().lemma());
//							temp.setProper();
//						} else if (tdGovTag.contains("NN")) {
//							temp = this.extractCategory(this.getSRL(td.gov().lemma()), td.gov().lemma());
//						}
//					}
//					
//					if(temp != null && noun != null && noun2 != null) {
//						System.out.println("possesive");
//						temp.addReference("IsA", noun2);
//						noun.addReference("HasA", temp);
//						storySentence.addReferences(noun.getId(), "HasA", temp);
//					}
//
//				}
//
//			}
			// else if (!(tdReln.equals("nmod:near") || tdReln.equals("nmod:at")
			// || tdReln.equals("nmod:in")
			// || tdReln.equals("nmod:on") || tdReln.equals("nmod:to")
			// || tdReln.equals("dobj")
			// || tdReln.equals("nsubj"))) {
			//
			// if ((tdDepTag.equals("NNP")
			// || tdDepTag.equals("NN"))
			// && tdGovTag.equals("VBD")) {
			//
			// // adds new doer with what he/she is capable of
			// //event.getDoer(td.dep().originalText()).addAttribute("capable
			// of", td.gov().lemma());
			//
			// }
			// }
			//
			// prevDep = td.dep().originalText();
			// prevDepTag = tdDepTag;
			// prevGov = td.gov().originalText();
			// prevGovTag = tdGovTag;
			// prevReln = tdReln;

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private Noun extractCategory(String category, String word) {
		switch (category) {
		case "PERSON":
			return new Character(word);
		case "ORGANIZATION":
			return new Location(word);
		case "LOCATION":
			return new Location(word);
		case "PLACE":
			return new Location(word);
		case "OBJECT":
			return new Object(word);
		case "UNKNOWN":
			return new Unknown(word);
		default:
			return null;
		}
	}

	private String getSRL(String text) {
		String[] ENTITY_LIST = { "person", "place", "object" };

		for (String entityValue : ENTITY_LIST) {

			if (ConceptNetDAO.checkSRL(text, "isA", entityValue)) {
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
						.compile("([a-zA-Z0-9.%]+(/" + entity + ")[ ]*)*[a-zA-Z0-9.%]+(/" + entity + ")");
				Matcher matcher = pattern.matcher(output);
				while (matcher.find()) {
					// int start = matcher.start();
					// int end = matcher.end();
					// String inputText = output.substring(start, end);
					// inputText = inputText.replaceAll("/" + entity, "");
					System.out.println(entity);
					return entity;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "UNKNOWN";
	}

	private float getPolarityOfEvent(StorySentence event) {
		List<String> concepts = event.getConcepts();

		if (concepts == null) {
			return (float) 0;
		}

		float worstPolarity = snp.getPolarity(concepts.get(0).replace(" ", "_"));
		float polarity = 0;
		for (int i = 1; i < concepts.size(); i++) {
			polarity = snp.getPolarity(concepts.get(i).replace(" ", "_"));
			if (polarity < worstPolarity) {
				worstPolarity = polarity;
			}
			System.out.println(worstPolarity);
		}

		return worstPolarity;
	}
}
