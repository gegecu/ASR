/**
 * 
 */
package view.menu.mainmenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.MainMenuController;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;
import view.TemplatePanel;
import view.menu.AliceHeaderPanel;
import view.utility.AutoResizingButton;
import view.utility.RoundedBorder;

/**
 * @author Alice
 * @since December 28, 2015
 */
@SuppressWarnings("serial")
public class MainMenuPanel extends TemplatePanel {

	public static final String writeStoryButtonActionCommand = "Write Story";
	public static final String exitButtonActionCommand = "Exit";
	public static final String helpButtonActionCommand = "help";

	private JButton exitButton;
	private JButton writeStoryButton;
	private JButton helpButton;

	private AliceHeaderPanel aliceHeaderPanel;
	private MainMenuLibraryPanel mainMenuLibraryPanel;

	private MainMenuController mainMenuController;

	@Override
	protected void initializeUI() {

		exitButton = new AutoResizingButton();
		writeStoryButton = new AutoResizingButton();
		helpButton = new AutoResizingButton();

		aliceHeaderPanel = new AliceHeaderPanel();
		mainMenuLibraryPanel = new MainMenuLibraryPanel(95, 5);

		mainMenuController = new MainMenuController();

		setLayout(new MigLayout());
		setBackground(Color.decode("#609AD1"));

		exitButton.setActionCommand(exitButtonActionCommand);
		exitButton.setText("X");
		exitButton.setFont(new Font("Arial", Font.BOLD, 40));
		exitButton.setBackground(Color.RED);
		exitButton.setForeground(Color.WHITE);
		exitButton.setFocusPainted(false);
		exitButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		exitButton
				.setBorder(new RoundedBorder(Color.BLACK, 3, 8, 0, 25, 0, 25));

		helpButton.setActionCommand(helpButtonActionCommand);
		helpButton.setText("?");
		helpButton.setFont(new Font("Arial", Font.BOLD, 40));
		helpButton.setBackground(Color.WHITE);
		helpButton.setForeground(Color.BLACK);
		helpButton.setFocusPainted(false);
		helpButton.setAlignmentY(Component.TOP_ALIGNMENT);
		helpButton.setVerticalAlignment(SwingConstants.TOP);
		helpButton
				.setBorder(new RoundedBorder(Color.BLACK, 3, 8, 13, 0, 13, 0));

		writeStoryButton.setActionCommand(writeStoryButtonActionCommand);
		writeStoryButton.setText("Write Your Story");
		writeStoryButton.setFont(new Font("Arial", Font.BOLD, 25));
		writeStoryButton.setBackground(Color.WHITE);
		writeStoryButton.setForeground(Color.BLACK);
		writeStoryButton.setFocusPainted(false);
		writeStoryButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		writeStoryButton.setVerticalAlignment(SwingConstants.CENTER);
		writeStoryButton.setBorder(new RoundedBorder(Color.BLACK, 3, 12));

		// top panel
		JPanel panel1 = new JPanel(new MigLayout("insets 10 10 5 10"));
		panel1.setBackground(Color.decode("#609AD1"));
		panel1.add(aliceHeaderPanel, "w 86%, grow");
		panel1.add(exitButton, "w 14%, growy");

		// middle panel
		mainMenuLibraryPanel.setBackground(Color.decode("#36B214"));

		// bottom panel []push[]
		JPanel panel2 = new JPanel(
				new MigLayout("insets 0 0 10 5, right", "", ""));
		panel2.setBackground(Color.decode("#36B214"));
		//panel2.add(helpButton, "w 15%, h 100%");
		panel2.add(writeStoryButton, "w 40%, h 100%, right, grow");

		// green panel
		JPanel panel3 = new JPanel(new MigLayout("insets 15 15 5 15"));
		panel3.setBackground(Color.decode("#36B214"));
		panel3.setBorder(new RoundedBorder(Color.BLACK, 3, 12));
		panel3.add(mainMenuLibraryPanel, "w 100%, h 80%, wrap");
		panel3.add(panel2, "w 100%, h 20%, wrap");

		add(panel1, "w 100%, h 10%, grow, wrap");
		add(panel3, "w 100%, h 90%, grow, wrap");

		addActionListener(mainMenuController);

	}

	@Override
	protected void addUIEffects() {

		exitButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) exitButton
					.getBorder();

			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (true == model.isRollover()) {
					border.setThickness(5);
				} else if (false == model.isRollover()) {
					border.setThickness(3);
				}
			}

		});

		helpButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) helpButton
					.getBorder();

			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (true == model.isRollover()) {
					border.setThickness(5);
				} else if (false == model.isRollover()) {
					border.setThickness(3);
				}
			}

		});

		writeStoryButton.getModel().addChangeListener(new ChangeListener() {

			private RoundedBorder border = (RoundedBorder) writeStoryButton
					.getBorder();

			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (true == model.isRollover()) {
					border.setThickness(5);
				} else if (false == model.isRollover()) {
					border.setThickness(3);
				}
			}

		});

	}

	@Override
	protected void addUXFeatures() {
		// TODO Auto-generated method stub

	}

	public JPanel getLibrary() {
		return mainMenuLibraryPanel;
	}

	@Override
	public void addActionListener(ActionListener actionListener) {
		writeStoryButton.addActionListener(actionListener);
		exitButton.addActionListener(actionListener);
		helpButton.addActionListener(actionListener);
	}

	public void setMainFrame(MainFrame mainFrame) {
		mainMenuController.setMainFrame(mainFrame);
		mainMenuLibraryPanel.setMainFrame(mainFrame);
	}

	public void refresh() {
		mainMenuLibraryPanel.refreshLibrary();
	}

}
