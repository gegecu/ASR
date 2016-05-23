import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import model.story_representation.story_element.Conflict;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.Event;
import model.story_representation.story_element.story_sentence.StorySentence;
import model.text_generation.DirectivesGenerator;
import model.text_generation.StorySegmentGenerator;
import model.text_generation.TextGeneration;
import model.text_understanding.TextUnderstanding;

public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String story = "";

		AbstractStoryRepresentation asr = new AbstractStoryRepresentation();

		TextUnderstanding tu = new TextUnderstanding(asr);

		System.out.println("Loading finish!");

		Scanner sc = new Scanner(System.in);

		List<TextGeneration> tg = new ArrayList<TextGeneration>();

		tg.add(new DirectivesGenerator(asr));

		tg.add(new StorySegmentGenerator(asr));

		Checklist checklist = new Checklist(asr);

		System.out.println("start? middle? end?");

		while (sc.hasNextLine()) {
			asr.setPartOfStory(sc.nextLine());
			System.out.println("input sentence story");
			String sentence = sc.nextLine();

			asr.reset();
			story += " " + sentence;

			tu.processInput(story);

			try {
				for (StorySentence e : asr
						.getStorySentencesBasedOnCurrentPart()) {
					System.out.println("event's address: " + e);

					System.out.println("is valid event? " + e.isValidEvent());

					for (Event p : e.getManyPredicates().values()) {
						System.out.println("doers: ");
						for (Map.Entry<String, Noun> entry : p.getManyDoers()
								.entrySet()) {
							System.out
									.println("id: " + entry.getValue().getId());
							System.out.println("common noun? "
									+ entry.getValue().getIsCommon());

							System.out.println("doers' attributes: ");
							for (Map.Entry<String, List<String>> entry2 : entry
									.getValue().getAttributes().entrySet()) {
								System.out.print(entry2.getKey() + " ");
								System.out.print(entry2.getValue());
								System.out.println();
							}

							System.out.println("doers' references: ");
							for (Map.Entry<String, List<Noun>> entry2 : entry
									.getValue().getReferences().entrySet()) {
								System.out.print(entry2.getKey() + " ");
								for (Noun n : entry2.getValue()) {
									System.out.print(n.getId() + " ");
								}
								System.out.println();
							}
						}

						System.out.println("action: " + p.getAction());

						System.out.println("receivers: ");
						for (Map.Entry<String, Noun> entry2 : p.getReceivers()
								.entrySet()) {
							System.out.println(
									"id: " + entry2.getValue().getId());
							System.out.println("common noun? "
									+ entry2.getValue().getIsCommon());

							System.out.println("receiver's attributes");
							for (Map.Entry<String, List<String>> entry3 : entry2
									.getValue().getAttributes().entrySet()) {
								System.out.print(entry3.getKey() + " ");
								System.out.print(entry3.getValue() + " ");
								System.out.println();
							}

							System.out.println("receiver's references");
							for (Map.Entry<String, List<Noun>> entry3 : entry2
									.getValue().getReferences().entrySet()) {
								System.out.print(entry3.getKey() + " ");
								for (Noun n : entry3.getValue()) {
									System.out.print(n.getId() + " ");
								}
								System.out.println();
							}
						}

						System.out.println("dobjs: ");
						for (Map.Entry<String, Noun> entry3 : p
								.getDirectObjects().entrySet()) {
							System.out.println(
									"id: " + entry3.getValue().getId());
							System.out.println("common noun? "
									+ entry3.getValue().getIsCommon());

							System.out.println("dobj's attributes ");
							for (Map.Entry<String, List<String>> entry4 : entry3
									.getValue().getAttributes().entrySet()) {
								System.out.print(entry4.getKey() + " ");
								System.out.print(entry4.getValue() + " ");
								System.out.println();
							}

							System.out.println("dobj's references ");
							for (Map.Entry<String, List<Noun>> entry4 : entry3
									.getValue().getReferences().entrySet()) {
								System.out.print(entry4.getKey() + " ");
								for (Noun n : entry4.getValue()) {
									System.out.print(n.getId() + " ");
								}
								System.out.println();
							}
						}
					}

					System.out.println();
					System.out.println("descriptions ");

					for (Entry<String, Description> entry : e
							.getManyDescriptions().entrySet()) {
						//System.out.println(entry.getKey());

						for (Map.Entry<String, Noun> doer : entry.getValue()
								.getManyDoers().entrySet()) {
							System.out.println(doer.getValue());
						}

						System.out.println("attributes ");
						for (Entry<String, List<String>> entry2 : entry
								.getValue().getAttributes().entrySet()) {
							System.out.print(entry2.getKey() + " ");
							System.out.println(entry2.getValue() + " ");
						}

						System.out.println("references ");
						for (Entry<String, List<Noun>> entry2 : entry.getValue()
								.getReferences().entrySet()) {
							System.out.print(entry2.getKey() + " ");
							for (Noun n : entry2.getValue()) {
								System.out.print(n.getId() + " ");
							}
							System.out.println();
						}
					}

					System.out.println();
					//System.out.println("polarity: " + e.getPolarity());
					System.out.println("concepts per predicate");
					for (Event predicate : e.getManyPredicates().values()) {
						System.out.print(
								"p_concepts: " + predicate.getConcepts());
						System.out.println(
								" polarity: " + predicate.getPolarity());
					}
					for (Description description : e.getManyDescriptions()
							.values()) {
						System.out.print(
								"n_concepts: " + description.getConcepts());
						System.out.println(
								" polarity: " + description.getPolarity());
					}
					System.out.println();
				}

			} catch (NullPointerException e) {
				//asr has no events but has so many descriptions for nouns
			}

			Conflict conflict = asr.getConflict();
			if (conflict != null) {
				System.out.println("conflict's address: " + conflict);
				System.out.println("expected conflict resolution: "
						+ conflict.getExpectedResolutionConcept());
			}

			//			System.out.println("expected resolution concept: " + asr.getConflict().getExpectedResolutionConcept());
			System.out.println("resolution's address: " + asr.getResolution());

			checklist.print();

			System.out.println();
			System.out.println("Generation? [1] yes [2] no");
			int yesNo = sc.nextInt();
			if (yesNo == 1) {
				System.out.println("[1] Directives [2] Story Segment");
				int typeToGen = sc.nextInt();
				System.out.println(tg.get(typeToGen - 1).generateText());
			}

			System.out.println("start? middle? end?");
			asr.setPartOfStory(sc.nextLine());

		}

	}

}
