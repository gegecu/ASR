package controller.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import model.text_generation.StorySegmentGenerator;
import model.text_generation.TextGeneration;
import model.text_generation.prompts.PromptChooser;
import utility.EvaluationLog;
import view.mode.dialog.AskMeDialog;
import view.mode.dialog.AskMeDialog.TypeOfHelp;
import view.mode.dialog.HelpDialog;
import view.mode.dialog.HelpDialog.HelpAnswer;
import view.mode.dialog.QuestionAnswerDialog;
import view.mode.dialog.SuggestionDialog;
import view.mode.dialog.WaitDialog;

public class AskMeController implements ActionListener {

	private static Logger log = Logger
			.getLogger(AskMeController.class.getName());

	private HelpDialog dialog = null;
	private HelpAnswer answer;
	private WaitDialog thinkingWaitDialog;
	private WaitDialog processingWaitDialog;
	private String helpText;
	private TypeOfHelp typeOfHelp;
	private Semaphore processed;

	private SubmitController submitController;

	private StorySegmentGenerator storySegmentGenerator;
	private PromptChooser promptChooser;
	private TextGeneration currentTextGenerator;

	private Semaphore submitSemaphore;

	public AskMeController(StorySegmentGenerator storySegmentGenerator,
			PromptChooser promptChooser, SubmitController submitController) {
		this.processingWaitDialog = new WaitDialog(
				"I am understanding your story ... Please Wait. =)");
		this.thinkingWaitDialog = new WaitDialog(
				"I am thinking ... Please Wait. =)");
		this.processed = new Semaphore(0);
		this.promptChooser = promptChooser;
		this.storySegmentGenerator = storySegmentGenerator;
		this.submitController = submitController;
		this.submitSemaphore = submitController.getSubmitSemaphore();
	}

	private void waitToFinishStoryProcessing() {

		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				submitSemaphore.acquire();
				return null;
			}

			@Override
			protected void done() {
				submitSemaphore.release();
				processingWaitDialog.setVisible(false);
			}

		}.execute();

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		waitToFinishStoryProcessing();
		processingWaitDialog.setVisible(true);
		typeOfHelp = new AskMeDialog().showDialog();

		if (AskMeDialog.TypeOfHelp.CANCEL != typeOfHelp) {

			helpText = "";

			switch (typeOfHelp) {
				case QUESTION_ANSWER :
					currentTextGenerator = promptChooser;
					dialog = new QuestionAnswerDialog();
					log.debug("*child asks for help. Chooses to user prompts*");
					EvaluationLog.log(
							"\n\t*child asks for help. Chooses to user prompts*");
					break;
				case SUGGESTIONS :
					currentTextGenerator = storySegmentGenerator;
					dialog = new SuggestionDialog();
					log.debug(
							"*child asks for help. Chooses to use story segments*");
					EvaluationLog.log(
							"\n\t*child asks for help. Chooses to use story segments*");
					break;
				default :
					break;
			}

			do {

				processTextGeneration();
				processed.release(1);
				thinkingWaitDialog.setVisible(true);

				answer = dialog.showDialog();

				if (typeOfHelp == TypeOfHelp.SUGGESTIONS) {

					log.debug("Story Segment Generated: " + helpText);
					EvaluationLog.log("\tStory Segment Generated: " + helpText);

					switch (answer) {
						case ACCEPT :
							log.debug("Child Used The Story Segment");
							storySegmentGenerator.addUsed(helpText);
							submitController.processStory(helpText);
							break;
						case REJECT :
							log.debug("Child Asked For Other Story Segment");
							break;
						case CANCEL :
							log.debug("Child Got It");
							break;
						case WRONG_ANSWER :
							break;
					}

				} else if (typeOfHelp == TypeOfHelp.QUESTION_ANSWER) {

					log.debug("Prompt Generated: " + helpText);
					EvaluationLog.log("\tPrompt Generated: " + helpText);

					QuestionAnswerDialog tempDialog = ((QuestionAnswerDialog) dialog);

					switch (answer) {
						case ACCEPT :
							String inputText = tempDialog.getInputText();
							log.debug("\tChild Answered : " + inputText);
							EvaluationLog.log("\tChild Answered : " + helpText);
							processPromptAnswer(inputText);
							processed.release(1);
							processingWaitDialog.setVisible(true);
							if (answer == HelpAnswer.WRONG_ANSWER) {
								log.debug("Child Answer was wrong");
							}
							break;
						case REJECT :
							log.debug("Child Asked For Other Prompt");
							promptChooser.ignored();
							break;
						case CANCEL :
							log.debug("Child Got It");
							promptChooser.ignored();
							promptChooser.stopLoop();
							break;
						default :
							break;
					}

					tempDialog.clearInputText();

				}

			} while (answer == HelpAnswer.REJECT
					|| promptChooser.getIsLoop());

		}

	}

	private void processPromptAnswer(String inputText) {

		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				try {
					processed.acquire();
					boolean isAnswerCorrect = submitController
							.verifyAnswer(inputText);
					if (!isAnswerCorrect) {
						answer = HelpAnswer.WRONG_ANSWER;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;

			}

			@Override
			protected void done() {
				processingWaitDialog.setVisible(false);
			}

		}.execute();

	}

	private void processTextGeneration() {

		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				try {
					processed.acquire();
					helpText = currentTextGenerator.generateText() + "";
					dialog.setHelpText(helpText);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;

			}

			@Override
			protected void done() {
				thinkingWaitDialog.setVisible(false);
			}

		}.execute();

	}

}
