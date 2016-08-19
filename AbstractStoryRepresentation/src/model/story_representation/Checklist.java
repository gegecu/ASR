package model.story_representation;

import java.util.Map.Entry;

import org.apache.log4j.Logger;

import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.story_representation.story_element.story_sentence.StorySentence;

/**
 * Used to check if criteria has been met
 */
public class Checklist {

	private static Logger log = Logger.getLogger(Checklist.class.getName());

	/**
	 * The AbstractStoryRepresentation used to check if the criteria has been
	 * met
	 */
	private AbstractStoryRepresentation asr;
	/**
	 * Stores if character criteria has been met
	 */
	private boolean isCharacterExist;
	/**
	 * Stores if location criteria has been met
	 */
	private boolean isLocationExist;
	/**
	 * Stores if conflict criteria has been met
	 */
	private boolean isConflictExist;
	/**
	 * Stores if series of action criteria has been met
	 */
	private boolean isSeriesActionExist;
	/**
	 * Stores if resolution criteria has been met
	 */
	private boolean isResolutionExist;
	/**
	 * Number of required actions for middle part of story
	 */
	private static final int requiredActionsMiddle = 2;

	/**
	 * Initializes the variables
	 * 
	 * @param asr
	 *            the AbstractStoryRepresentation to set
	 */
	public Checklist(AbstractStoryRepresentation asr) {
		this.asr = asr;
		this.isCharacterExist = false;
		this.isCharacterExist = false;
		this.isLocationExist = false;
		this.isConflictExist = false;
		this.isSeriesActionExist = false;
		this.isResolutionExist = false;
	}

	/**
	 * Checks if character criteria is met
	 * 
	 * @return returns true if character criteria is met.
	 */
	public boolean isCharacterExist() {
		if (!this.isCharacterExist) {
			for (Entry<String, Noun> entry : this.asr.getNounsMap()
					.entrySet()) {
				if (entry.getValue() != null
						&& entry.getValue().getType() == TypeOfNoun.CHARACTER) {
					this.isCharacterExist = true;
					break;
				}
			}
		}
		return isCharacterExist;
	}

	/**
	 * Checks if location criteria is met
	 * 
	 * @return returns true if location criteria is met.
	 */
	public boolean isLocationExist() {
		if (!isLocationExist) {
			for (Entry<String, Noun> entry : this.asr.getNounsMap()
					.entrySet()) {
				if (entry.getValue() != null
						&& entry.getValue().getType() == TypeOfNoun.LOCATION) {
					this.isLocationExist = true;
					break;
				}
			}
		}
		return isLocationExist;
	}

	/**
	 * Checks if conflict criteria is met
	 * 
	 * @return returns true if conflcit criteria is met.
	 */
	public boolean isConflictExist() {
		if (!this.isConflictExist) {
			this.isConflictExist = (this.asr.getConflict() != null);
		}
		return isConflictExist;
	}

	/**
	 * Checks if beginning part of story criterias is met
	 * 
	 * @return returns true if beginning part of story criterias is met.
	 */
	public boolean isBeginningComplete() {
		return this.isCharacterExist && this.isConflictExist
				&& this.isLocationExist;
	}

	/**
	 * @return the requiredActionsMiddle
	 */
	public int getRequiredActionsMiddle() {
		return requiredActionsMiddle;
	}

	/**
	 * @return the number of actions detected in the middle part of story
	 */
	public int getSeriesActionCount() {
		int nEvents = 0;
		if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.middle)) {
			for (StorySentence ss : this.asr.getStorySentencesBasedOnPart(
					AbstractStoryRepresentation.middle)) {
				if (ss.hasValidEvent()) {
					nEvents += ss.getEventsCount();
				}
			}
		}
		return nEvents;
	}

	/**
	 * Checks if series of actions criteria is met
	 * 
	 * @return returns true if series of actions criteria is met.
	 */
	public boolean isSeriesActionExist() {
		if (!this.isSeriesActionExist && asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.middle)) {
			int nEvents = 0;
			for (StorySentence ss : this.asr.getStorySentencesBasedOnPart(
					AbstractStoryRepresentation.middle)) {
				if (ss.hasValidEvent()) {
					nEvents += ss.getEventsCount();
				}
			}
			this.isSeriesActionExist = nEvents >= requiredActionsMiddle;
		}
		return this.isSeriesActionExist;
	}

	/**
	 * Checks if middle part of story criterias is met
	 * 
	 * @return returns true if middle part of story criterias is met.
	 */
	public boolean isMiddleComplete() {
		return this.isSeriesActionExist;
	}

	/**
	 * Checks if resolution criteria is met
	 * 
	 * @return returns true if resolution criteria is met.
	 */
	public boolean isResolutionExist() {
		if (!this.isResolutionExist && asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.end)) {
			this.isResolutionExist = (this.asr.getResolution() != null);
		}
		return this.isResolutionExist;
	}

	/**
	 * Checks if end part of story criterias is met
	 * 
	 * @return returns true if end part of story criterias is met.
	 */
	public boolean isEndingComplete() {
		return this.isResolutionExist;
	}

	/**
	 * used for debugging purposes
	 */
	public void print() {
		if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.start)) {
			log.debug("Character exist? " + this.isCharacterExist());
			log.debug("Location exist? " + this.isLocationExist());
			log.debug("Conflict exist? " + this.isConflictExist());
			log.debug("Start complete? " + this.isBeginningComplete());
		} else if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.middle)) {
			log.debug("Series event exist? " + this.isSeriesActionExist());
			log.debug("Middle complete? " + this.isMiddleComplete());
		} else if (asr.getCurrentPartOfStory()
				.equals(AbstractStoryRepresentation.end)) {
			log.debug("Resolution exist? " + this.isResolutionExist());
			log.debug("End complete? " + this.isEndingComplete());
		}
	}

}
