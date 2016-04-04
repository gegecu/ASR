package model.story_representation;

import java.util.ArrayList;
import java.util.List;
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
		
		this.isLocationExist = false;
			
			List<Character> temp = new ArrayList();
			
			for(Entry<String, Noun> entry: this.asr.getManyNouns().entrySet()) {
				if(entry.getValue() instanceof Character) {
					temp.add((Character) entry.getValue());
				}
			}
			
			List<Character> temp3 = new ArrayList();
			
			for(int i = 0; i < temp.size(); i++) {
				Character c = (Character) temp.get(i);
	
				List<Noun> temp2 = c.getReference("IsA");
				
				System.out.println(c.getId());
				
				if(c.getReference("AtLocation") != null && !c.getReference("AtLocation").isEmpty()) {
					System.out.println(c.getId());
					temp3.add(c);
					if(temp2 != null) {
						for(Noun n: temp2) {
							if(n instanceof Character) {
								System.out.println(n.getId());
								temp3.add((Character)n);
							}
						}
					}
				}
			}
			
			temp.removeAll(temp3);

			System.out.println(temp.size());
			if(temp.isEmpty()) {
				this.isLocationExist = true;
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
	
	public void print() {
		if(asr.getPartOfStory().equals("start")) {
			System.out.println("Character exist? " + this.isCharacterExist());
			System.out.println("Location exist? " + this.isLocationExist());
			System.out.println("Conflict exist? " + this.isConflictExist());
			System.out.println("Start complete? " + this.isBeginningComplete());
		}
		else if (asr.getPartOfStory().equals("middle")) {
			System.out.println("Series event exist? " + this.isSeriesActionExist());
			System.out.println("Middle complete? " + this.isMiddleComplete());
		}
		else if (asr.getPartOfStory().equals("end")) {
			System.out.println("Resolution exist? " + this.isResolutionExist());
			System.out.println("End complete? " + this.isEndingComplete());
		}
	}

}
