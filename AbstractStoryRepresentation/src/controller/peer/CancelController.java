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
import view.mode.dialog.OkCancelDialog;

public class CancelController implements ActionListener {

	private static Logger log = Logger
			.getLogger(CancelController.class.getName());

	private MainFrame mainFrame;

	private JTextField titleField;
	private JTextArea storyViewArea;

	private OkCancelDialog cancelDialog;

	/**
	 * @param storyTitleField
	 *            the JTextField for the title
	 * @param storyArea
	 *            the JTextArea for the whole story
	 */
	public CancelController(JTextField titleField, JTextArea storyViewArea) {
		this.titleField = titleField;
		this.storyViewArea = storyViewArea;
		this.cancelDialog = new OkCancelDialog("Cancel Story",
				"This will not save your story.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		boolean cancel = cancelDialog.showDialog();

		if (cancel) {
			if (!storyViewArea.getText().isEmpty()) {
				Story story = new Story(titleField.getText(),
						storyViewArea.getText());

				log.debug("Child Cancelled The Story : " + story);
				log.debug("\nWHOLE STORY OF CHILD: \n\tTitle: "
						+ story.getStoryTitle() + "\nStory: "
						+ story.getStoryBody());
				EvaluationLog.log("\nWHOLE STORY OF CHILD: \n\tTitle: "
						+ story.getStoryTitle() + "\n\tStory: "
						+ story.getStoryBody() + "\n");

				StoryDAO.saveUnfinishedStory(story);
			}
			mainFrame.showMainMenu();
		}

	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

}
