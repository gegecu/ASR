package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;

import model.text_generation.DirectivesGenerator;
import model.text_generation.StorySegmentGenerator;
import model.text_generation.TextGeneration;
import view.mode.dialog.AskMeDialog;
import view.mode.dialog.AskMeDialog.TypeOfHelp;
import view.mode.dialog.HelpDialog;
import view.mode.dialog.HelpDialog.HelpAnswer;
import view.mode.dialog.IdeaDialog;
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
	private TextGeneration currentTextGenerator;

	public AskMeController(DirectivesGenerator directivesGenerator,
			StorySegmentGenerator storySegmentGenerator,
			SubmitController submitController) {
		this.waitDialog = new WaitDialog();
		this.processed = new Semaphore(0);
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
				case SUGGESTIONS :
					currentTextGenerator = storySegmentGenerator;
					dialog = new SuggestionDialog();
					break;
				default :
					break;
			}

			System.out.println(typeOfHelp);

			do {

				processTextGeneration();
				processed.release(1);
				waitDialog.setVisible(true);

				answer = dialog.showDialog();

				if (typeOfHelp == TypeOfHelp.SUGGESTIONS) {
					if (answer == HelpAnswer.ACCEPT) {
						submitController.processStory(helpText);
					}
				}

			} while (answer == HelpAnswer.REJECT);

		}

	}

	private void processTextGeneration() {

		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				helpText = currentTextGenerator.generateText() + "";
				//helpText = "The quick brown fox jumps over the lazy dog.";
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
