package controller.peer.checklist;

import controller.peer.SaveController;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import view.mode.beginner.ChecklistPanel;

public abstract class ChecklistController {

	public enum TypeOfChecklistController {
		BEGINNER, ADVANCED;
	}

	protected Checklist checklist;
	protected AbstractStoryRepresentation asr;
	protected SaveController saveController;

	protected TypeOfChecklistController type;

	public ChecklistController(AbstractStoryRepresentation asr,
			Checklist checklist, SaveController saveController) {
		this.asr = asr;
		this.checklist = checklist;
		this.saveController = saveController;
	}

	protected void doTheThing() {

		String partOfStory = asr.getCurrentPartOfStory();

		switch (partOfStory) {
			case ChecklistPanel.START :
				if (checklist.isBeginningComplete()) {
					asr.setPartOfStory(ChecklistPanel.MIDDLE);
				}
				break;
			case ChecklistPanel.MIDDLE :
				if (checklist.isMiddleComplete()) {
					asr.setPartOfStory(ChecklistPanel.END);
				}
				break;
			case ChecklistPanel.END :
				break;
		}

	}

	public abstract void updateChecklist();

	public TypeOfChecklistController getType() {
		return type;
	}

}
