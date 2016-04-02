package model.story_representation;

import java.util.Map.Entry;

import model.story_representation.story_element.noun.Character;
import model.story_representation.story_element.noun.Noun;
import model.story_representation.story_element.story_sentence.StorySentence;

public class Checklist {
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
		if(!this.isCharacterExist) {
			for(Entry<String, Noun> entry: this.asr.getManyNouns().entrySet()) {
				if(entry.getValue() instanceof Character) {
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
		if(!this.isLocationExist) {
			for(Entry<String, Noun> entry: this.asr.getManyNouns().entrySet()) {
				if(entry.getValue() instanceof Character) {
					if(entry.getValue().getReference("AtLocation") == null 
							|| entry.getValue().getReference("AtLocation").isEmpty())
					break;
				}
				this.isLocationExist = true;
			}
 		}
	}

	public boolean isConflictExist() {
		this.conflictExist();
		return isConflictExist;
	}
	
	private void conflictExist() {
		if(!this.isConflictExist) {
			this.isConflictExist = this.asr.getConflict() != null;
		}
	}

	public boolean isBeginningComplete() {
		return this.isCharacterExist && this.isConflictExist && this.isLocationExist;
	}
	
	private void seriesActionExist() {
		if(!this.isSeriesActionExist) {
			int nEvents = 0;
			for(StorySentence ss: this.asr.getManyStorySentencesBasedOnPart("middle")) {
				if(ss.isValidEvent()) {
					nEvents++;
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
		if(!this.isResolutionExist) {
			this.isResolutionExist = this.asr.getResolution() != null;
		}
	}
	
	public boolean isResolutionExist() {
		this.resolutionExist();
		return this.isResolutionExist;
	}
	
	public boolean isEndingComplete() {
		return this.isResolutionExist;
	}

}
