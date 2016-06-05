/**
 * 
 */
package view;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import view.menu.choosefriend.ChooseFriendPanel;
import view.menu.choosemode.ChooseWritingModePanel;
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
	private ChooseWritingModePanel chooseWritingModePanel;
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
		setVisible(true);
	}

	private void initializeUI() {

		panel = new JPanel();
		mainMenuPanel = new MainMenuPanel();
		chooseFriendPanel = new ChooseFriendPanel();
		chooseWritingModePanel = new ChooseWritingModePanel();

		cardLayout = new CardLayout();
		panel.setLayout(cardLayout);
		this.add(panel);

		panel.add(mainMenuPanel, "main menu");
		panel.add(chooseWritingModePanel, "writing mode");
		panel.add(new BeginnerModePanel(), "beginner");
		panel.add(new AdvancedModePanel(), "advanced");

		cardLayout.show(panel, "beginner");

	}

}
