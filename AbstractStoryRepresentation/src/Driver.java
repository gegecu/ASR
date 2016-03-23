import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Predicate;
import model.story_representation.noun.Character;
import model.story_representation.noun.Location;
import model.story_representation.noun.Noun;
import model.story_representation.noun.Object;
import model.story_representation.story_sentence.Event;
import model.story_representation.story_sentence.State;
import model.story_representation.story_sentence.StorySentence;
import model.text_understanding.TextUnderstanding;
public class Driver {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		AbstractStoryRepresentation asr = new AbstractStoryRepresentation();
		
		String sentence = "Geraldine went to the park. Geraldine is cute.";
		
		TextUnderstanding tu = new TextUnderstanding();
		
		tu.processInput(sentence, asr);
		
		
		for(StorySentence storySentence: asr.getManyStorySentences().get("start")) {
			if(storySentence.getLocation() != null)
				System.out.println("location: " + storySentence.getLocation().getId());
			
			if(storySentence instanceof Event) {
				System.out.println("doers: ");
				for(Map.Entry<String, Noun> entry: ((Event)storySentence).getManyDoers().entrySet()) {
					System.out.println(entry.getValue().getId());
					
					System.out.println("doers' attributes: ");
					for(Map.Entry<String, List<String>> entry2: entry.getValue().getAttributes().entrySet()) {
						System.out.print(entry2.getValue() + " ");
					}
					System.out.println();
				}
				
				System.out.println("predicates: ");
				for(Map.Entry<String, Predicate> entry: ((Event)storySentence).getManyPredicates().entrySet()) {
					System.out.println("action: " + entry.getValue().getAction());
					System.out.println("receivers: ");
					for(Map.Entry<String, Noun> entry2: entry.getValue().getReceivers().entrySet()) {
						System.out.println(entry2.getValue().getId());
						for(Map.Entry<String, List<String>> entry3: entry2.getValue().getAttributes().entrySet()) {
							System.out.print(entry3.getValue() + " ");
						}
					}
					System.out.println("dobj: ");
					for(Map.Entry<String, Noun> entry3: entry.getValue().getDirectObjects().entrySet()) {
						System.out.println(entry3.getValue().getId());
						for(Map.Entry<String, List<String>> entry4: entry3.getValue().getAttributes().entrySet()) {
							System.out.print(entry4.getValue() + " ");
						}
					}
				}
			}
			
			else if (storySentence instanceof State) {
				System.out.println();
				System.out.println();
				System.out.println("subjects: ");
				for(Map.Entry<String, Noun> entry: ((State)storySentence).getManySubjects().entrySet()) {
					System.out.println(entry.getValue().getId());
					
					System.out.println("subjects' attributes: ");
					for(Map.Entry<String, List<String>> entry2: entry.getValue().getAttributes().entrySet()) {
						System.out.print(entry2.getValue() + " ");
					}
					System.out.println();
				}
				
				System.out.println(((State)storySentence).getState());
			}
			System.out.println();
			System.out.println("polarity: " + storySentence.getPolarity());
			System.out.println();
		}
		
		StorySentence conflict = asr.getConflict();
		
		if(conflict != null) {
			System.out.println("Conflict: ");
			
			if(conflict.getLocation() != null)
				System.out.println("location: " + conflict.getLocation().getId());
			
			if(conflict instanceof Event) {
				System.out.println("doers: ");
				for(Map.Entry<String, Noun> entry: ((Event)conflict).getManyDoers().entrySet()) {
					System.out.println(entry.getValue().getId());
					
					System.out.println("doers' attributes: ");
					for(Map.Entry<String, List<String>> entry2: entry.getValue().getAttributes().entrySet()) {
						System.out.print(entry2.getValue() + " ");
					}
					System.out.println();
				}
				
				System.out.println("predicates: ");
				for(Map.Entry<String, Predicate> entry: ((Event)conflict).getManyPredicates().entrySet()) {
					System.out.println("action: " + entry.getValue().getAction());
					System.out.println("receivers: ");
					for(Map.Entry<String, Noun> entry2: entry.getValue().getReceivers().entrySet()) {
						System.out.println(entry2.getValue().getId());
						for(Map.Entry<String, List<String>> entry3: entry2.getValue().getAttributes().entrySet()) {
							System.out.print(entry3.getValue() + " ");
						}
					}
					System.out.println("dobj: ");
					for(Map.Entry<String, Noun> entry3: entry.getValue().getDirectObjects().entrySet()) {
						System.out.println(entry3.getValue().getId());
						for(Map.Entry<String, List<String>> entry4: entry3.getValue().getAttributes().entrySet()) {
							System.out.print(entry4.getValue() + " ");
						}
					}
				}
			}
			
			else if (conflict instanceof State) {
				System.out.println();
				System.out.println();
				System.out.println("subjects: ");
				for(Map.Entry<String, Noun> entry: ((State)conflict).getManySubjects().entrySet()) {
					System.out.println(entry.getValue().getId());
					
					System.out.println("subjects' attributes: ");
					for(Map.Entry<String, List<String>> entry2: entry.getValue().getAttributes().entrySet()) {
						System.out.print(entry2.getValue() + " ");
					}
					System.out.println();
				}
				
				System.out.println(((State)conflict).getState());
			}
			
			System.out.println();
			System.out.println("polarity: " + conflict.getPolarity());
			System.out.println();
			

		}
	}

}
