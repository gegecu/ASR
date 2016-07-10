package controller.peer.checklist;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.peer.SaveController;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import view.mode.beginner.ChecklistPanel;

public class BeginnerChecklistController extends ChecklistController
		implements
			ActionListener {

	private ChecklistPanel checklistPanel;

	public BeginnerChecklistController(AbstractStoryRepresentation asr,
			Checklist checklist, ChecklistPanel checklistPanel,
			SaveController saveController) {
		super(asr, checklist, saveController);
		this.checklistPanel = checklistPanel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		String partOfStory = asr.getCurrentPartOfStory();

		switch (partOfStory) {
			case ChecklistPanel.START :
				if (checklist.isBeginningComplete()) {
					checklistPanel.middle();
				}
				break;
			case ChecklistPanel.MIDDLE :
				if (checklist.isMiddleComplete()) {
					checklistPanel.end();
				}
				break;
			case ChecklistPanel.END :
				break;
		}

		doTheThing();

	}

	public void updateChecklist() {
		checklistPanel.updateChecklist(asr, checklist);
	}

}
