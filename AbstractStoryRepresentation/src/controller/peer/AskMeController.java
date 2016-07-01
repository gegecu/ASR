package controller.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;

import model.text_generation.DirectivesGenerator;
import model.text_generation.StorySegmentGenerator;
import model.text_generation.TextGeneration;
import model.text_generation.prompts.PromptChooser;
import view.mode.dialog.AskMeDialog;
import view.mode.dialog.AskMeDialog.TypeOfHelp;
import view.mode.dialog.HelpDialog;
import view.mode.dialog.HelpDialog.HelpAnswer;
import view.mode.dialog.IdeaDialog;
import view.mode.dialog.QuestionAnswerDialog;
import view.mode.dialog.SuggestionDialog;
import view.mode.dialog.WaitDialog;

public class AskMeController implements ActionListener {

	private HelpDialog dialog = null;
	private HelpAnswer answer;
	private WaitDialog waitDialog;
	private String helpText;
	private TypeOfHelp typeOfHelp;
	private Semaphore processed;

	private SubmitController submitController;

	private DirectivesGenerator directivesGenerator;
	private StorySegmentGenerator storySegmentGenerator;
	private PromptChooser promptChooser;
	private TextGeneration currentTextGenerator;

	public AskMeController(DirectivesGenerator directivesGenerator,
			StorySegmentGenerator storySegmentGenerator,
			PromptChooser promptChooser, SubmitController submitController) {
		this.waitDialog = new WaitDialog(
				"Alice is thinking ... Please Wait. =)");
		this.processed = new Semaphore(0);
		this.promptChooser = promptChooser;
		this.directivesGenerator = directivesGenerator;
		this.storySegmentGenerator = storySegmentGenerator;
		this.submitController = submitController;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		typeOfHelp = new AskMeDialog().showDialog();

		if (AskMeDialog.TypeOfHelp.CANCEL != typeOfHelp) {

			helpText = "Test";

			switch (typeOfHelp) {
				case IDEAS :
					currentTextGenerator = directivesGenerator;
					dialog = new IdeaDialog();
					break;
				case QUESTION_ANSWER :
					currentTextGenerator = promptChooser;
					dialog = new QuestionAnswerDialog();
					break;
				case SUGGESTIONS :
					currentTextGenerator = storySegmentGenerator;
					dialog = new SuggestionDialog();
					break;
				default :
					break;
			}

			do {

				processTextGeneration();
				processed.release(1);
				waitDialog.setVisible(true);

				answer = dialog.showDialog();

				if (typeOfHelp == TypeOfHelp.SUGGESTIONS) {
					if (answer == HelpAnswer.ACCEPT) {
						submitController.processStory(helpText);
					}
				} else if (typeOfHelp == TypeOfHelp.QUESTION_ANSWER) {
					QuestionAnswerDialog tempDialog = ((QuestionAnswerDialog) dialog);
					if (answer == HelpAnswer.ACCEPT) {
						String inputText = tempDialog.getInputText();
						boolean isAnswerCorrect = submitController
								.verifyAnswer(inputText);
						if (!isAnswerCorrect) {
							answer = HelpAnswer.WRONG_ANSWER;
						}
					} else if (answer == HelpAnswer.CANCEL
							|| answer == HelpAnswer.REJECT) {
						promptChooser.ignored();
					}
					tempDialog.clearInputText();
				}

			} while (answer == HelpAnswer.REJECT
					|| answer == HelpAnswer.WRONG_ANSWER);

		}

	}

	private void processTextGeneration() {

		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				helpText = currentTextGenerator.generateText() + "";
				dialog.setHelpText(helpText);
				return null;
			}

			@Override
			protected void done() {
				try {
					processed.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				waitDialog.setVisible(false);
			}

		}.execute();

	}

}
