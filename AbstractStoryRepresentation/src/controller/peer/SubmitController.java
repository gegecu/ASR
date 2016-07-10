package controller.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import controller.peer.checklist.ChecklistController;
import model.story_representation.AbstractStoryRepresentation;
import model.text_generation.prompts.PromptChooser;
import model.text_understanding.TextUnderstanding;
import utility.EvaluationLog;
import view.mode.StoryInputPanel;
import view.mode.StoryViewPanel;

public class SubmitController implements ActionListener {

	private static Logger log = Logger
			.getLogger(SubmitController.class.getName());

	private StoryInputPanel storyInputPanel;
	private StoryViewPanel storyViewPanel;
	private AbstractStoryRepresentation asr;
	private TextUnderstanding tu;
	private PromptChooser promptChooser;
	private ChecklistController checklistController;
	private ExecutorService executorService;

	private Semaphore submitSemaphore;
	private AtomicInteger threadCount;

	/**
	 * @param asr
	 *            Abstract Story Representation
	 * @param tu
	 *            Text Understanding
	 * @param checklistController
	 *            CheclistController
	 */
	public SubmitController(AbstractStoryRepresentation asr,
			TextUnderstanding tu, PromptChooser promptChooser,
			ChecklistController checklistController) {
		this.asr = asr;
		this.tu = tu;
		this.promptChooser = promptChooser;
		this.checklistController = checklistController;
		this.executorService = Executors.newSingleThreadExecutor();

		this.submitSemaphore = new Semaphore(1);
		this.threadCount = new AtomicInteger(0);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String text = storyInputPanel.getInputStorySegment();
		if (!text.trim().isEmpty()) {
			log.debug("Input of User : " + text);
			EvaluationLog.log("\nChild wrote : " + text);
			processStory(text);
		}
	}

	public void processStory(String text) {

		if (threadCount.incrementAndGet() == 1) {
			try {
				submitSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		executorService.submit(new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				try {
					storyViewPanel.appendStory(text);
					tu.processInput(storyViewPanel.getStory());
					checklistController.updateChecklist();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;

			}

			@Override
			protected void done() {
				if (threadCount.decrementAndGet() == 0) {
					submitSemaphore.release();
				}
			}

		});

	}

	public void setStoryInputPanel(StoryInputPanel storyInputPanel) {
		this.storyInputPanel = storyInputPanel;
	}

	public void setStoryViewPanel(StoryViewPanel storyViewPanel) {
		this.storyViewPanel = storyViewPanel;
	}

	public boolean verifyAnswer(String inputText) {
		boolean isAnswerCorrect = promptChooser.checkAnswer(inputText);
		if (isAnswerCorrect) {
			processStory(promptChooser.incompleteAnswer(inputText));
		}
		return isAnswerCorrect;
	}

	public Semaphore getSubmitSemaphore() {
		return submitSemaphore;
	}

}
