/**
 * 
 */
package view;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.story_database.Story;
import view.menu.choosefriend.ChooseFriendPanel;
import view.menu.choosemode.ChooseModePanel;
import view.menu.mainmenu.MainMenuPanel;
import view.mode.advanced.AdvancedModePanel;
import view.mode.beginner.BeginnerModePanel;
import view.viewstory.ViewStoryPanel;

/**
 * @author Alice
 * @since December 28, 2015
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private MainMenuPanel mainMenuPanel;
	private ViewStoryPanel viewStoryPanel;
	private ChooseFriendPanel chooseFriendPanel;
	private ChooseModePanel chooseModePanel;
	private BeginnerModePanel beginnerModePanel;
	private AdvancedModePanel advancedModePanel;
	private CardLayout cardLayout;
	private JPanel panel;

	public MainFrame() {
		super("Alice");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//setMinimumSize(new Dimension(1104, 621));
		setMinimumSize(new Dimension(880, 660));
		initializeUI();
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
	}

	private void initializeUI() {

		panel = new JPanel();
		mainMenuPanel = new MainMenuPanel();
		viewStoryPanel = new ViewStoryPanel();
		chooseFriendPanel = new ChooseFriendPanel();
		chooseModePanel = new ChooseModePanel();

		cardLayout = new CardLayout();
		panel.setLayout(cardLayout);
		this.add(panel);

		panel.add(mainMenuPanel, "main menu");
		panel.add(viewStoryPanel, "view story");
		panel.add(chooseModePanel, "choose mode");
		//panel.add(beginnerModePanel = new BeginnerModePanel(), "beginner");
		//panel.add(advancedModePanel = new AdvancedModePanel(), "advanced");

		showMainMenu();

	}

	public MainMenuPanel getMainMenuPanel() {
		return mainMenuPanel;
	}

	public ChooseFriendPanel getChooseFriendPanel() {
		return chooseFriendPanel;
	}

	public ChooseModePanel getChooseModePanel() {
		return chooseModePanel;
	}

	public void showBeginnerMode() {
		if (beginnerModePanel != null) {
			panel.remove(beginnerModePanel);
		}
		panel.add(beginnerModePanel = new BeginnerModePanel(), "beginner");
		beginnerModePanel.setMainFrame(this);
		cardLayout.show(panel, "beginner");
	}

	public void showAdvancedMode() {
		if (advancedModePanel != null) {
			panel.remove(advancedModePanel);
		}
		panel.add(advancedModePanel = new AdvancedModePanel(), "advanced");
		advancedModePanel.setMainFrame(this);
		cardLayout.show(panel, "advanced");
	}

	public void showMainMenu() {
		mainMenuPanel.refresh();
		cardLayout.show(panel, "main menu");
	}

	public void showChooseModePanel() {
		cardLayout.show(panel, "choose mode");
	}

	public AdvancedModePanel getAdvancedModePanel() {
		return advancedModePanel;
	}

	public BeginnerModePanel getBeginnerModePanel() {
		return beginnerModePanel;
	}

	public void showViewStory(Story story) {
		viewStoryPanel.setStory(story);
		cardLayout.show(panel, "view story");
	}

	public ViewStoryPanel getViewStoryPanel() {
		return viewStoryPanel;
	}

}
