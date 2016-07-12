package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.story_database.StoryDAO;
import view.MainFrame;
import view.mode.dialog.OkCancelDialog;
import view.viewstory.ViewStoryPanel;

public class ViewStoryController implements ActionListener {

	private MainFrame mainFrame;
	private OkCancelDialog deleteDialog;

	public ViewStoryController() {
		this.deleteDialog = new OkCancelDialog("Delete Story", "Delete the story?");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
			case ViewStoryPanel.cancelButtonActionCommand :
				mainFrame.showMainMenu();
				break;
			default :
				int id = Integer.parseInt(e.getActionCommand());
				boolean delete = deleteDialog.showDialog(); // delete dialog here
				if (delete) {
					StoryDAO.deleteStory(id);
					mainFrame.showMainMenu();
				}
				break;
		}

	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

}
