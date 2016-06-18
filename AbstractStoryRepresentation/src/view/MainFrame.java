/**
 * 
 */
package view;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import view.menu.choosefriend.ChooseFriendPanel;
import view.menu.choosemode.ChooseModePanel;
import view.menu.mainmenu.MainMenuPanel;
import view.mode.advanced.AdvancedModePanel;
import view.mode.beginner.BeginnerModePanel;

/**
 * @author Alice
 * @since December 28, 2015
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private MainMenuPanel mainMenuPanel;
	private ChooseFriendPanel chooseFriendPanel;
	private ChooseModePanel chooseModePanel;
	private BeginnerModePanel beginnerModePanel;
	private AdvancedModePanel advancedModePanel;
	private CardLayout cardLayout;
	private JPanel panel;

	public MainFrame() {
		super("Alice");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(880, 660));
		initializeUI();
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
	}

	private void initializeUI() {

		panel = new JPanel();
		mainMenuPanel = new MainMenuPanel();
		chooseFriendPanel = new ChooseFriendPanel();
		chooseModePanel = new ChooseModePanel();

		cardLayout = new CardLayout();
		panel.setLayout(cardLayout);
		this.add(panel);

		panel.add(mainMenuPanel, "main menu");
		panel.add(chooseModePanel, "choose mode");
		//panel.add(beginnerModePanel = new BeginnerModePanel(), "beginner");
		//panel.add(advancedModePanel = new AdvancedModePanel(), "advanced");

		cardLayout.show(panel, "main menu");

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

}
