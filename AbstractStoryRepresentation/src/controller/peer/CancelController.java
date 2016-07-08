package controller.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.story_database.Story;
import model.story_database.StoryDAO;
import view.MainFrame;

public class CancelController implements ActionListener {

	private MainFrame mainFrame;
	private JTextField storyTitleField;
	private JTextArea storyArea;

	@Override
	public void actionPerformed(ActionEvent e) {

		boolean cancel = false;// alert sure delete all progress

		if (cancel) {
			Story story = new Story(0, storyTitleField.getText(),
					storyArea.getText());
			StoryDAO.saveUnfinishedStory(story);
		}

		mainFrame.showMainMenu();

	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	/**
	 * @param storyTitleField
	 *            the JTextField for the title
	 * @param storyArea
	 *            the JTextArea for the whole story
	 */
	public void setFields(JTextField storyTitleField, JTextArea storyArea) {
		this.storyArea = storyArea;
		this.storyTitleField = storyTitleField;
	}

}
