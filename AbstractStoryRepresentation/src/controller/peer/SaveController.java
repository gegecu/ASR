package controller.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import model.story_database.Story;
import model.story_database.StoryDAO;
import utility.EvaluationLog;
import view.MainFrame;
import view.mode.dialog.OkDialog;
import view.mode.dialog.OkCancelDialog;

public class SaveController implements ActionListener {

	private static Logger log = Logger
			.getLogger(SaveController.class.getName());

	private MainFrame mainFrame;
	private JTextField titleField;
	private JTextArea storyViewArea;

	private OkDialog noTitleDialog;
	private OkDialog noStoryDialog;

	private OkCancelDialog saveConfirmDialog;
	private OkDialog databaseErrorDialog;

	public SaveController(JTextField titleField, JTextArea textArea) {
		this.titleField = titleField;
		this.storyViewArea = textArea;
		this.noTitleDialog = new OkDialog("No Story Title",
				"You should have a title for your story. =)");
		this.noStoryDialog = new OkDialog("No Story",
				"You should write a story. =)");
		this.saveConfirmDialog = new OkCancelDialog("Save Story",
				"You cannot edit your story after saving it.");
		this.databaseErrorDialog = new OkDialog("Database Error",
				"Something went wrong try again.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (titleField.getText().isEmpty()) {
			noTitleDialog.setVisible(true);
			return;
		}

		if (storyViewArea.getText().isEmpty()) {
			noStoryDialog.setVisible(true);
			return;
		}

		if (saveConfirmDialog.showDialog()) {
			Story story = new Story(titleField.getText(),
					storyViewArea.getText());
			if (StoryDAO.saveStory(story)) {

				log.debug("Child Saved The Story : " + story);
				log.debug("WHOLE STORY OF CHILD: \n\tTitle: "
						+ story.getStoryTitle() + "\nStory: "
						+ story.getStoryText());
				EvaluationLog.log("WHOLE STORY OF CHILD: \n\tTitle: "
						+ story.getStoryTitle() + "\n\tStory: "
						+ story.getStoryText() + "\n");

				mainFrame.showMainMenu();

			} else {
				databaseErrorDialog.setVisible(true);
			}
		}

	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

}
