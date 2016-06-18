package controller.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import view.mode.beginner.ChecklistPanel;

public class ChecklistController implements ActionListener {

	private Checklist checklist;
	private AbstractStoryRepresentation asr;
	private ChecklistPanel checklistPanel;

	public ChecklistController(AbstractStoryRepresentation asr,
			Checklist checklist, ChecklistPanel checklistPanel) {
		this.asr = asr;
		this.checklist = checklist;
		this.checklistPanel = checklistPanel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		String partOfStory = asr.getCurrentPartOfStory();

		switch (partOfStory) {
			case ChecklistPanel.START :
				if (checklist.isBeginningComplete()) {
					asr.setPartOfStory(ChecklistPanel.MIDDLE);
					checklistPanel.middle();
				}
				break;
			case ChecklistPanel.MIDDLE :
				if (checklist.isMiddleComplete()) { 
					asr.setPartOfStory(ChecklistPanel.END);
					checklistPanel.end();
				}
				break;
			case ChecklistPanel.END :
				break;
		}

	}

	public void updateChecklist() {
		if (checklistPanel != null) {
			checklistPanel.updateChecklist(asr, checklist);
		}
	}

}
