package controller.peer.checklist;

import controller.peer.SaveController;
import model.story_representation.AbstractStoryRepresentation;
import model.story_representation.Checklist;

public class AdvancedChecklistController extends ChecklistController {

	public AdvancedChecklistController(AbstractStoryRepresentation asr,
			Checklist checklist, SaveController saveController) {
		super(asr, checklist, saveController);
		this.type = TypeOfChecklistController.ADVANCED;
	}

	@Override
	public void updateChecklist() {
		doTheThing();
	}

}
