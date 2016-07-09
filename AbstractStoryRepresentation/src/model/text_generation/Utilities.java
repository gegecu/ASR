package model.text_generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.Description;
import model.story_representation.story_element.story_sentence.StorySentence;

public class Utilities {

	/**
	 * @param storySentence
	 * @return
	 */
	public static List<Noun> getDoers(StorySentence storySentence) {
		List<Noun> doers = new ArrayList<Noun>();
		for (Description description : storySentence.getManyDescriptions()
				.values()) {
			for (Noun noun : description.getManyDoers().values()) {
				if (noun instanceof Character) {
					doers.add((Character) noun);
				}
			}
		}
		return doers;
	}

	/**
	 * @param storySentence
	 * @return
	 */
	public static Location getLocation(StorySentence storySentence,
			AbstractStoryRepresentation asr) {
		Location location = null;
		for (String i : storySentence.getAllNounsInStorySentence()) {
			Noun noun = asr.getNoun(i);
			if (noun instanceof Location) {
				location = (Location) noun;
				return location;
			}
		}
		return location;
	}

	public static <T> int countLists(Collection<List<T>> collection) {
		int count = 0;
		for (List<T> list : collection) {
			count += list.size();
		}
		return count;
	}

}
