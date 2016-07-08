package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.story_database.StoryDAO;
import view.MainFrame;
import view.viewstory.ViewStoryPanel;

public class ViewStoryController implements ActionListener {

	private MainFrame mainFrame;

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
			case ViewStoryPanel.cancelButtonActionCommand :
				mainFrame.showMainMenu();
				break;
			default :
				int id = Integer.parseInt(e.getActionCommand());
				boolean delete = false; // delete dialog here
				if (delete) {
					StoryDAO.deleteStory(id);
					mainFrame.showMainMenu();
				}
				break;
		}

	}

}
