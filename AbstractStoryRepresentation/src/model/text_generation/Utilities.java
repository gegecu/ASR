package model.text_generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.story_representation.story_element.story_sentence.StorySentence;

public class Utilities {

	//	public static List<Noun> getDoers(StorySentence storySentence) {
	//		List<Noun> doers = new ArrayList<Noun>();
	//		for (Description description : storySentence.getManyDescriptions()
	//				.values()) {
	//			for (Noun noun : description.getManyDoers().values()) {
	//				if (noun.getType() == TypeOfNoun.CHARACTER) {
	//					doers.add((Character) noun);
	//				}
	//			}
	//		}
	//		return doers;
	//	}

	/**
	 * Returns list of locations detected in the StorySentence param using the
	 * AbstractStoryRepresentation as reference
	 * 
	 * @param storySentence
	 *            the storySentence to get location from
	 * @param asr
	 *            the AbstractStoryRepresentation used as reference
	 * @return list of noun locations found in the story sentence
	 */
	public static List<Location> getLocation(StorySentence storySentence,
			AbstractStoryRepresentation asr) {
		List<Location> locations = new ArrayList<>();
		for (String i : storySentence.getAllNouns()) {
			Noun noun = asr.getNoun(i);
			if (noun.getType() == TypeOfNoun.LOCATION) {
				locations.add((Location) noun);
			}
		}
		return locations;
	}

	/**
	 * @param collection
	 *            the collection to count
	 * @return the total added size of all list within the collection
	 */
	public static <T> int countLists(Collection<List<T>> collection) {
		int count = 0;
		for (List<T> list : collection) {
			count += list.size();
		}
		return count;
	}

}
