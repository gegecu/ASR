import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Event;
import model.story_representation.Predicate;
import model.story_representation.noun.Character;
import model.story_representation.noun.Location;
import model.story_representation.noun.Noun;
import model.story_representation.noun.Object;
import model.text_understanding.TextUnderstanding;
public class Driver {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		AbstractStoryRepresentation asr = new AbstractStoryRepresentation();
		
		String sentence = "The late train was late.";
		
		TextUnderstanding tu = new TextUnderstanding();
		
		tu.processInput(sentence, asr);
		
		
		for(Event e: asr.getManyEvents().get("start")) {
			if(e.getLocation() != null)
				System.out.println("location: " + e.getLocation().getId());
			
			System.out.println("doers: ");
			for(Map.Entry<String, Noun> entry: e.getManyDoers().entrySet()) {
				System.out.println(entry.getValue().getId());
				
				System.out.println("doers' attributes: ");
				for(Map.Entry<String, List<String>> entry2: entry.getValue().getAttributes().entrySet()) {
					System.out.print(entry2.getValue() + " ");
				}
				System.out.println();
			}
			
			System.out.println("predicates: ");
			for(Map.Entry<String, Predicate> entry: e.getManyPredicates().entrySet()) {
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
			System.out.println();
			System.out.println("polarity: " + e.getPolarity());
			System.out.println();
		}
		
		Event conflict = asr.getConflict();
		
		if(conflict != null) {
			System.out.println("Conflict: ");
			
			if(conflict.getLocation() != null)
				System.out.println("location: " + conflict.getLocation().getId());
			
			System.out.println("doers: ");
			for(Map.Entry<String, Noun> entry: conflict.getManyDoers().entrySet()) {
				System.out.println(entry.getValue().getId());
				
				System.out.println("doers' attributes: ");
				for(Map.Entry<String, List<String>> entry2: entry.getValue().getAttributes().entrySet()) {
					System.out.print(entry2.getValue() + " ");
				}
				System.out.println();
			}
			
			System.out.println("predicates: ");
			for(Map.Entry<String, Predicate> entry: conflict.getManyPredicates().entrySet()) {
				System.out.println("action: " + entry.getValue().getAction());
				System.out.println("receivers: ");
				for(Map.Entry<String, Noun> entry2: entry.getValue().getReceivers().entrySet()) {
					System.out.println(entry2.getValue().getId());
				}
				System.out.println("dobj: ");
				for(Map.Entry<String, Noun> entry3: entry.getValue().getDirectObjects().entrySet()) {
					System.out.println(entry3.getValue().getId());
				}
			}
			
			System.out.println("polarity: " + conflict.getPolarity());
			System.out.println();
		}
	}

}
