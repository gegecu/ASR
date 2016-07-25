package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import model.story_database.Story;
import model.story_database.StoryDAO;
import view.MainFrame;
import view.menu.mainmenu.MainMenuLibraryPanel;

public class MainMenuLibraryController implements ActionListener {

	private MainFrame mainFrame;

	private MainMenuLibraryPanel mainMenuLibraryPanel;

	@Override
	public void actionPerformed(ActionEvent e) {
		Story story = null;
		story = StoryDAO.getStory(Integer.parseInt(e.getActionCommand()));
		mainFrame.showViewStory(story);
	}

	public void refreshLibrary() {

		mainMenuLibraryPanel.clearLibraryPanel();

		List<Story> stories = StoryDAO.getSavedStories();
		for (Story story : stories) {
			mainMenuLibraryPanel.addStory(String.valueOf(story.getStoryId()),
					story.getStoryTitle());
		}

	}

	public void setMainMenuLibraryPanel(
			MainMenuLibraryPanel mainMenuLibraryPanel) {
		this.mainMenuLibraryPanel = mainMenuLibraryPanel;
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

}
