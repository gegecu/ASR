package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;

import model.story_representation.AbstractStoryRepresentation;
import model.text_understanding.TextUnderstanding;
import view.mode.StoryInputPanel;
import view.mode.StoryViewPanel;

public class SubmitController implements ActionListener {

	private StoryInputPanel storyInputPanel;
	private StoryViewPanel storyViewPanel;
	private AbstractStoryRepresentation asr;
	private TextUnderstanding tu;
	private ChecklistController checklistController;
	private Semaphore semaphore;

	/**
	 * @param asr
	 *            Abstract Story Representation
	 * @param tu
	 *            Text Understanding
	 * @param checklistController
	 *            CheclistController
	 */
	public SubmitController(AbstractStoryRepresentation asr,
			TextUnderstanding tu, ChecklistController checklistController) {
		this.asr = asr;
		this.tu = tu;
		this.checklistController = checklistController;
		this.semaphore = new Semaphore(1);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String text = storyInputPanel.getInputStorySegment();
		if (!text.trim().isEmpty()) {
			processStory(text);
		}
	}

	public void processStory(String text) {

		storyViewPanel.appendStory(text);

		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				semaphore.acquire();
				tu.processInput(storyViewPanel.getStory());
				semaphore.release();
				checklistController.updateChecklist();
				return null;
			}

			@Override
			protected void done() {
			}

		}.execute();

	}

	public void setStoryInputPanel(StoryInputPanel storyInputPanel) {
		this.storyInputPanel = storyInputPanel;
	}

	public void setStoryViewPanel(StoryViewPanel storyViewPanel) {
		this.storyViewPanel = storyViewPanel;
	}

}
