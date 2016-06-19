package model.story_representation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Noun;
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
			for (Entry<String, Noun> entry : this.asr.getNounMap()
					.entrySet()) {
				if (entry.getValue() instanceof Character) {
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

		if(this.isCharacterExist != false) {
			List<Character> temp = new ArrayList<>();
	
			for (Entry<String, Noun> entry : this.asr.getNounMap().entrySet()) {
				if (entry.getValue() instanceof Character) {
					temp.add((Character) entry.getValue());
				}
			}
	
			List<Character> temp3 = new ArrayList<>();
	
			for (int i = 0; i < temp.size(); i++) {
				Character c = (Character) temp.get(i);
	
				Map<String, Noun> temp2 = c.getReference("IsA");
	
				log.debug(c.getId());
	
				if (c.getReference("AtLocation") != null
						&& !c.getReference("AtLocation").isEmpty()) {
					log.debug(c.getId());
					temp3.add(c);
					if (temp2 != null) {
						Collection<Noun> nouns = temp2.values();
						for (Noun n : nouns) {
							if (n instanceof Character) {
								log.debug(n.getId());
								temp3.add((Character) n);
							}
						}
					}
				}
			}
	
			temp.removeAll(temp3);
	
			log.debug(temp.size());
			if (temp.isEmpty()) {
				this.isLocationExist = true;
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
			for (StorySentence ss : this.asr
					.getStorySentencesBasedOnPart("middle")) {
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
		if (asr.getCurrentPartOfStory().equals("start")) {
			log.debug("Character exist? " + this.isCharacterExist());
			log.debug("Location exist? " + this.isLocationExist());
			log.debug("Conflict exist? " + this.isConflictExist());
			log.debug("Start complete? " + this.isBeginningComplete());
		} else if (asr.getCurrentPartOfStory().equals("middle")) {
			log.debug(
					"Series event exist? " + this.isSeriesActionExist());
			log.debug("Middle complete? " + this.isMiddleComplete());
		} else if (asr.getCurrentPartOfStory().equals("end")) {
			log.debug("Resolution exist? " + this.isResolutionExist());
			log.debug("End complete? " + this.isEndingComplete());
		}
	}

}
