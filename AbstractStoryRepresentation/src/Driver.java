import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Object;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_generation.DirectivesGenerator;
import model.text_generation.RelationQuestionGenerator;
import model.text_generation.StorySegmentGenerator;
import model.text_generation.TextGeneration;
import model.text_understanding.TextUnderstanding;
import model.utility.Randomizer;
public class Driver {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String story = "";
		
		AbstractStoryRepresentation asr = new AbstractStoryRepresentation();
		
		TextUnderstanding tu = new TextUnderstanding(asr);
		
		List<TextGeneration> tg = new ArrayList();
		
		tg.add(new StorySegmentGenerator(asr));
//				
//		tg.add(new DirectivesGenerator(asr));
//		
//		tg.add(new RelationQuestionGenerator(asr));
		
		System.out.println("Loading finish!");
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("start? middle? end?");

		while (sc.hasNextLine()) {
			asr.setPartOfStory(sc.nextLine());
			System.out.println("input sentence story");
			String sentence = sc.nextLine();
			
			story += " " + sentence;
			
			tu.processInput(sentence);
		
		
			try{
				for(StorySentence e: asr.getManyStorySentencesBasedOnCurrentPart()) {
					System.out.println("event's address: "+ e);
					
					System.out.println("is valid event? " + e.isValidEvent());
					
					for(Event p: e.getManyPredicates().values()) {
						System.out.println("doers: ");
						for(Map.Entry<String, Noun> entry: p.getManyDoers().entrySet()) {
							System.out.println("id: " + entry.getValue().getId());
							System.out.println("common noun? " + entry.getValue().getIsCommon());
						
							System.out.println("doers' attributes: ");
							for(Map.Entry<String, List<String>> entry2: entry.getValue().getAttributes().entrySet()) {
								System.out.print(entry2.getKey() + " ");
								System.out.print(entry2.getValue());
								System.out.println();
							}
							
							System.out.println("doers' references: ");
							for(Map.Entry<String, List<Noun>> entry2: entry.getValue().getReferences().entrySet()) {
								System.out.print(entry2.getKey() + " ");
								for(Noun n: entry2.getValue()) {
									System.out.print(n.getId() + " ");
								}
								System.out.println();
							}
						}	
						
						System.out.println("action: " + p.getAction());
						
						System.out.println("receivers: ");
						for(Map.Entry<String, Noun> entry2: p.getReceivers().entrySet()) {
							System.out.println("id: " + entry2.getValue().getId());
							System.out.println("common noun? " + entry2.getValue().getIsCommon());
							
							System.out.println("receiver's attributes");
							for(Map.Entry<String, List<String>> entry3: entry2.getValue().getAttributes().entrySet()) {
								System.out.print(entry3.getKey() + " ");
								System.out.print(entry3.getValue() + " ");
								System.out.println();
							}
							
							System.out.println("receiver's references");
							for(Map.Entry<String, List<Noun>> entry3: entry2.getValue().getReferences().entrySet()) {
								System.out.print(entry3.getKey() + " ");
								for(Noun n: entry3.getValue()) {
									System.out.print(n.getId() + " ");
								}
								System.out.println();
							}
						}
						
						System.out.println("dobjs: ");
						for(Map.Entry<String, Noun> entry3: p.getDirectObjects().entrySet()) {
							System.out.println("id: " + entry3.getValue().getId());
							System.out.println("common noun? " + entry3.getValue().getIsCommon());
				
							System.out.println("dobj's attributes ");
							for(Map.Entry<String, List<String>> entry4: entry3.getValue().getAttributes().entrySet()) {
								System.out.print(entry4.getKey() + " ");
								System.out.print(entry4.getValue() + " ");
								System.out.println();
							}
							
							System.out.println("dobj's references ");
							for(Map.Entry<String, List<Noun>> entry4: entry3.getValue().getReferences().entrySet()) {
								System.out.print(entry4.getKey() + " ");
								for(Noun n: entry4.getValue()) {
									System.out.print(n.getId() + " ");
								}
								System.out.println();
							}
						}
					}	
					
					System.out.println("descriptions ");
					
					
					for(Entry<String, Description> entry: e.getManyDescriptions().entrySet()) {
						System.out.println(entry.getKey());
						System.out.println("attributes ");
						
						for(Entry<String, List<String>> entry2: entry.getValue().getAttributes().entrySet()) {
							System.out.print(entry2.getKey() + " ");
							System.out.println(entry2.getValue() + " ");
						}
						
						System.out.println("references ");
						for(Entry<String, List<Noun>> entry2: entry.getValue().getReferences().entrySet()) {
							System.out.print(entry2.getKey() + " ");
							for(Noun n: entry2.getValue()) {
								System.out.print(n.getId() + " ");
							}
							System.out.println();
						}
					}

					System.out.println();
					//System.out.println("polarity: " + e.getPolarity());
					System.out.println("concepts per predicate");
					for(Event predicate: e.getManyPredicates().values()){
						System.out.print("p_concepts: " + predicate.getConcepts());
						System.out.println(" polarity: " + predicate.getPolarity());
					}
					for(Description description: e.getManyDescriptions().values()) {
						System.out.print("n_concepts: " + description.getConcepts());
						System.out.println(" polarity: " + description.getPolarity());
					}
					System.out.println();
				}

			}catch(NullPointerException e) {
				//asr has no events but has so many descriptions for nouns
			}
			
			System.out.println("conflict's address: " + asr.getConflict());
			System.out.println("expected resolution concept: " + asr.getExpectedResolution());
			System.out.println("resolution's address: " + asr.getResolution());
			
			//gen
			System.out.println();
			System.out.println("Generation? [1] yes [2] no");
			int yesNo = sc.nextInt();
			if(yesNo == 1) {
				System.out.println(tg.get(0).generateText());
			}
//				System.out.println("[1] Directives   [2] Prompts   [3] Story Segment");
//				int typeToGen = sc.nextInt();
//				int random = 0;
//				
//				String generated = "";
//				
//				for (int i = 0; i < tg.size(); i++) {
//					tg.remove(i);
//				}
//				
//				switch(typeToGen) {
//					case 1:
//						tg.add(new DirectivesGenerator(asr));
//						break;
//					case 2:
//						tg.add(new RelationQuestionGenerator(asr));
//						break;
//					case 3:
//						tg.add(new StorySegmentGenerator(asr));
//						break;
//				}
//				
//				random = Randomizer.random(1, tg.size());
//				generated = tg.get(random-1).generateText();
//				
//				if(generated == null) {
//					generated = "Tell me more.";
//				}
//				
//				System.out.println(generated);
//			}
//			
//			if(yesNo == 1) {
//				int random = Randomizer.random(1, tg.size());
//				String generated = tg.get(random-1).generateText();
//				if(generated == null) {
//					generated = "Tell me more.";
//				}
//				
//				System.out.println(generated);
//			}
			
			System.out.println("start? middle? end?");
			asr.setPartOfStory(sc.nextLine());
		}
	}

}
