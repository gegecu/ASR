package model.story_representation;

import java.util.Map.Entry;

import org.apache.log4j.Logger;

import model.story_representation.story_element.noun.Location;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.noun.Noun.TypeOfNoun;
import model.story_representation.story_element.story_sentence.StorySentence;

public class Checklist {

	private static Logger log = Logger.getLogger(Checklist.class.getName());

	private AbstractStoryRepresentation asr;
	private boolean isCharacterExist;
	private boolean isLocationExist;
	private boolean isConflictExist;
	private boolean isSeriesActionExist;
	private boolean isResolutionExist;

	public Checklist(AbstractStoryRepresentation asr) {
		this.asr = asr;
		this.isCharacterExist = false;
		this.isCharacterExist = false;
		this.isLocationExist = false;
		this.isConflictExist = false;
		this.isSeriesActionExist = false;
		this.isResolutionExist = false;
	}

	public boolean isCharacterExist() {
		this.characterExist();
		return isCharacterExist;
	}

	private void characterExist() {
		if (!this.isCharacterExist) {
			for (Entry<String, Noun> entry : this.asr.getNounMap().entrySet()) {
				if (entry.getValue() != null
						&& entry.getValue().getType() == TypeOfNoun.CHARACTER) {
					this.isCharacterExist = true;
					break;
				}
			}
		}
	}

	public boolean isLocationExist() {
		this.locationExist();
		return isLocationExist;
	}

	private void locationExist() {

		this.isLocationExist = false;

		for (Entry<String, Noun> entry : this.asr.getNounMap().entrySet()) {
			if (entry.getValue() != null
					&& entry.getValue().getType() == TypeOfNoun.LOCATION) {
				this.isLocationExist = true;
				break;
			}
		}

	}

	public boolean isConflictExist() {
		this.conflictExist();
		return isConflictExist;
	}

	private void conflictExist() {
		if (!this.isConflictExist) {
			this.isConflictExist = (this.asr.getConflict() != null);
		}
	}

	public boolean isBeginningComplete() {
		return this.isCharacterExist && this.isConflictExist
				&& this.isLocationExist;
	}

	private void seriesActionExist() {
		if (!this.isSeriesActionExist) {
			int nEvents = 0;
			for (StorySentence ss : this.asr.getStorySentencesBasedOnPart(
					AbstractStoryRepresentation.middle)) {
				if (ss.isValidEvent()) {
					nEvents += ss.getEventsCount();
				}
			}
			this.isSeriesActionExist = nEvents >= 2;
		}
	}

	public boolean isSeriesActionExist() {
		this.seriesActionExist();
		return this.isSeriesActionExist;
	}

	public boolean isMiddleComplete() {
		return this.isSeriesActionExist;
	}

	private void resolutionExist() {
		if (!this.isResolutionExist) {
			this.isResolutionExist = (this.asr.getResolution() != null);
		}
	}

	public boolean isResolutionExist() {
		this.resolutionExist();
		return this.isResolutionExist;
	}

	public boolean isEndingComplete() {
		return this.isResolutionExist;
	}

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
