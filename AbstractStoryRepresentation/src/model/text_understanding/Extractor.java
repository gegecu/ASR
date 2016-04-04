package model.text_understanding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.knowledge_base.conceptnet.ConceptNetDAO;
import model.knowledge_base.senticnet.ConceptParser;
import model.knowledge_base.senticnet.SenticNetParser;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Object;
import model.story_representation.story_element.noun.Unknown;
import model.story_representation.story_element.story_sentence.Clause;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.utility.TypedDependencyComparator;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class Extractor {
	private StanfordCoreNLP pipeline;
	private AbstractSequenceClassifier classifier;
	private ConceptParser cp;
	private SenticNetParser snp;
	private Dictionaries dictionary;

	public Extractor(StanfordCoreNLP pipeline) {
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
			List<TypedDependency> listDependencies = new ArrayList<TypedDependency>(dependencies.typedDependencies());
			Collections.sort(listDependencies, new TypedDependencyComparator());
			
			for (TypedDependency td : listDependencies) { 
				extractDependency(td, asr, storySentence, cp);//extract based on dependency
			}
		
			for(Event event: storySentence.getManyPredicates().values()){
				event.setPolarity(getPolarityOfEvent(event));
			}
			for(Description description: storySentence.getManyDescriptions().values()){
				description.setPolarity(getPolarityOfEvent(description));
			}
			
			asr.addEvent(storySentence);
		}
	}

	//private void 
	private void extractDependency(TypedDependency td, AbstractStoryRepresentation asr, StorySentence storySentence, ConceptParser conceptParser) {
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
					
					System.out.println(noun.getClass().toString());

					if(noun != null)
					asr.addNoun(td.dep().lemma(), noun);
				}
				
				if(noun != null) { /**if noun exists**/
	
					if (tdGovTag.equals("JJ")) { /**if 'noun is adjective' format**/

						noun.addAttribute("HasProperty", td.gov().lemma());
						
						Description description = storySentence.getDescription(td.gov().lemma());
						if(description == null) {
							description = new Description();
						}
						
						description.addDoer(noun.getId(), noun);
						description.addAttribute("HasProperty", td.gov().lemma());
						
						description.addConcept(conceptParser.createConceptAsAdjective(td.gov().lemma()));
						description.addConcept(conceptParser.createConceptAsPredicativeAdjective(td.gov().lemma()));
						
						storySentence.addDescription(td.gov().lemma(), description);
						
						System.out.println(noun.getId() + " hasProperty " + td.gov().lemma());
					}
	
					else if (tdGovTag.contains("VB")) { /**if verb**/
	
						boolean hasARelation = td.gov().lemma().equals("has") || td.gov().lemma().equals("have");
	
						if (!hasARelation || !dictionary.copulas.contains(td.gov().lemma())) {
							noun.addAttribute("CapableOf", td.gov().lemma());
							System.out.println(noun.getId() + " capable of " + td.gov().lemma());
						}
	
						Event event = storySentence.getPredicate(td.gov().index());

						if (event == null) {
							event = new Event(td.gov().lemma());
						}
						event.addDoer(td.dep().lemma(), noun);
						event.addConcept(conceptParser.createConceptAsVerb(td.gov().lemma()));
						storySentence.addPredicate(td.gov().index(), event);
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
						
						Description description = storySentence.getDescription(td.gov().lemma());
						if(description == null) {
							description = new Description();
						}
						
						description.addDoer(noun.getId(), noun);
						description.addReference("IsA", noun2);
						
						description.addConcept(conceptParser.createConceptAsAdjective(td.gov().lemma()));
						description.addConcept(conceptParser.createConceptAsPredicativeAdjective(td.gov().lemma()));
						
						storySentence.addDescription(td.gov().lemma(), description);
					}
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

					Event event = storySentence.getPredicate(td.gov().index());

					if (event == null) {
						event = new Event(td.gov().lemma());
					}
					event.addReceiver(td.dep().lemma(), noun);
					storySentence.addPredicate(td.gov().index(), event);

					System.out.println("iobj: " + asr.getNoun(td.dep().lemma()).getId());
				}
			}

			else if (tdReln.equals("dobj") || tdReln.equals("nmod:for") || tdReln.equals("nmod:agent")) {

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

				Event event = storySentence.getPredicate(td.gov().index());

				if (event == null) {
					event = new Event(td.gov().lemma());
				}
				
				if(noun != null) {
	
					if (td.gov().lemma().equals("has") || td.gov().lemma().equals("have")) {
						
						Description description = storySentence.getDescription(td.dep().lemma());
						if(description == null) {
							description = new Description();
						}

						for(Noun n: storySentence.getPredicate(td.gov().index()).getManyDoers().values()) {
							if (noun != null) {
								n.addReference("HasA", noun);
								description.addDoer(n.getId(), n);
								description.addReference("HasA", noun);
								description.addConcept(conceptParser.createConceptWithDirectObject(td.gov().lemma(), td.dep().lemma()));
								description.addConcept(td.dep().lemma());
							}
						}
						storySentence.addDescription(td.dep().lemma(), description);
						storySentence.getManyPredicates().remove(td.gov().index());
						
					} else {
						
						//create concept
						event.addDirectObject(td.dep().lemma(), noun);
						storySentence.addPredicate(td.gov().index(), event);
									
						if(noun instanceof Character) //if direct object is a person, change to someone
							event.addConcept(conceptParser.createConceptWithDirectObject(td.gov().lemma(), "someone"));
						else {
							event.addConcept(conceptParser.createConceptWithDirectObject(td.gov().lemma(), td.dep().lemma()));
							event.addConcept(td.dep().lemma());
						}
						
						System.out.println("dobj: "
								+ storySentence.getPredicate(td.gov().index()).getDirectObject(td.dep().lemma()).getId());
					}
				}
			}

			else if (tdReln.equals("xcomp") && dictionary.copulas.contains(td.gov().lemma())) {
				
				Description description = storySentence.getDescription(td.dep().lemma());
				
				if(description == null) {
					description = new Description();
				}
				
				for(Noun n: storySentence.getPredicate(td.gov().index()).getManyDoers().values()) {
					if (tdDepTag.equals("JJ")) {
						n.addAttribute("HasProperty", td.dep().lemma());
						description.addDoer(n.getId(), n);
						description.addAttribute("HasProperty", td.dep().lemma());
						description.addConcept(conceptParser.createConceptAsAdjective(td.dep().lemma()));
						description.addConcept(conceptParser.createConceptAsPredicativeAdjective(td.dep().lemma()));
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
							description.addReference("IsA", noun2);
							description.addConcept(conceptParser.createConceptAsRole(td.dep().lemma()));
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
				
				Description description = storySentence.getDescription(td.dep().lemma());
				if(description == null) {
					description = new Description();
				}
				
				description.addAttribute("HasProperty", td.dep().lemma());
				description.addConcept(conceptParser.createConceptAsAdjective(td.dep().lemma()));
				description.addConcept(conceptParser.createConceptAsPredicativeAdjective(td.dep().lemma()));
				
			}

			else if (tdReln.equals("advmod")) {
				if (tdDepTag.equals("RB")) {
					if(dictionary.copulas.contains(td.gov().lemma())) {
						Description description = storySentence.getDescription(td.dep().lemma());
						if(description == null) {
							description = new Description();
						}
						for(Noun n: storySentence.getPredicate(td.gov().index()).getManyDoers().values()) {
							n.addAttribute("HasProperty", td.dep().lemma());
							description.addDoer(n.getId(), n);
						}	
						description.addAttribute("HasProperty", td.dep().lemma());
						description.addConcept(conceptParser.createConceptAsAdjective(td.dep().lemma()));
					storySentence.getManyPredicates().remove(td.gov().lemma());
					}
				}
			}

			else if (tdReln.equals("nmod:at") || tdReln.equals("nmod:near") || tdReln.equals("nmod:to")
					|| tdReln.equals("nmod:in") || tdReln.equals("nmod:on")) {
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
					Description description = storySentence.getDescription(td.dep().lemma());
					if(description == null) {
						description = new Description();
					}
					
					for (Noun n : storySentence.getPredicate(td.gov().index()).getManyDoers().values()) {
						n.addReference("AtLocation", noun);
						description.addReference("AtLocation", noun);
						description.addDoer(n.getId(), n);
					}

					Event predicate = storySentence.getPredicate(td.gov().index());

					if (predicate == null) {
						predicate = new Event(td.gov().lemma());
					}
					
					predicate.addDirectObject(noun.getId(), noun);
					System.out.println("Location: " + td.dep().lemma());
					
				}
				
				if(tdReln.equals("nmod:to") && tdGovTag.contains("VB")){
					//create concept
					Event predicate = storySentence.getPredicate(td.gov().index());
					if (predicate == null) {
						predicate = new Event(td.gov().lemma());
						storySentence.addPredicate(td.gov().index(), predicate);
					}
					predicate.addConcept(conceptParser.createConceptAsInfinitive(td.gov().lemma(),td.dep().lemma()));					
				}

			}
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

	private float getPolarityOfEvent(Clause clause) {
		List<String> concepts = clause.getConcepts();

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