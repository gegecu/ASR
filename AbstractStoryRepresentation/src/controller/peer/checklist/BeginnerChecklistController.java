package controller.peer.checklist;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import controller.peer.SaveController;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;
import view.mode.beginner.ChecklistPanel;
import view.mode.dialog.OkDialog;

public class BeginnerChecklistController extends ChecklistController
		implements
			ActionListener {

	private ChecklistPanel checklistPanel;
	private OkDialog finishedDialog;
	private OkDialog finishedSaveDialog;

	private boolean[] parts;

	private JButton nextButton;

	public BeginnerChecklistController(AbstractStoryRepresentation asr,
			Checklist checklist, ChecklistPanel checklistPanel,
			SaveController saveController, JButton nextButton) {
		super(asr, checklist, saveController);
		this.type = TypeOfChecklistController.BEGINNER;
		this.checklistPanel = checklistPanel;
		this.parts = new boolean[3];
		this.finishedDialog = new OkDialog("Congratulations!",
				"Congrats! You finished your story!");
		this.finishedSaveDialog = new OkDialog("Congratulations!",
				"You can save your story if you want.");
		this.nextButton = nextButton;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		String partOfStory = asr.getCurrentPartOfStory();

		switch (partOfStory) {
			case ChecklistPanel.START :
				if (checklist.isBeginningComplete()) {
					checklistPanel.middle();
					nextButton.setEnabled(false);
				}
				break;
			case ChecklistPanel.MIDDLE :
				if (checklist.isMiddleComplete()) {
					checklistPanel.end();
					nextButton.setEnabled(false);
				}
				break;
			case ChecklistPanel.END :
				if (checklist.isEndingComplete()) {
					finishedDialog.setVisible(true);
					finishedSaveDialog.setVisible(true);
				}
				break;
		}

		doTheThing();

	}

	public void showCongrats() {

		String partOfStory = asr.getCurrentPartOfStory();

		switch (partOfStory) {
			case ChecklistPanel.START :
				if (checklist.isBeginningComplete() && !parts[0]) {
					nextButton.setEnabled(true);
					parts[0] = true;
					new OkDialog("Congratulations!",
							"Congrats! You finished the start part of your story.")
									.setVisible(true);
					new OkDialog("Congratulations!",
							"You can now go to the next part of the story.")
									.setVisible(true);
				}
				break;
			case ChecklistPanel.MIDDLE :
				if (checklist.isMiddleComplete() && !parts[1]) {
					nextButton.setEnabled(true);
					parts[1] = true;
					new OkDialog("Congratulations!",
							"Congrats! You finished the middle part of your story.")
									.setVisible(true);
					new OkDialog("Congratulations!",
							"You can now go to the next part of the story.")
									.setVisible(true);
				}
				break;
			case ChecklistPanel.END :
				if (checklist.isEndingComplete() && !parts[2]) {
					nextButton.setEnabled(true);
					parts[2] = true;
					finishedDialog.setVisible(true);
					finishedSaveDialog.setVisible(true);
				}
				break;
		}

	}

	public void updateChecklist() {
		checklistPanel.updateChecklist(asr, checklist);
	}

	public Checklist getChecklist() {
		return checklist;
	}

}
