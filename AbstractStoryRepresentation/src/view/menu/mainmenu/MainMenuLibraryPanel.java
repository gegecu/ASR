package view.menu.mainmenu;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.MainMenuLibraryController;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;
import view.ViewPanel;
import view.utility.AutoResizingButton;

/**
 * @author Alice
 * @since December 23, 2015
 */
@SuppressWarnings("serial")
public class MainMenuLibraryPanel extends ViewPanel {

	private MainMenuLibraryController mainMenuLibraryController;

	private Map<Integer, JButton> reuseButtonMap;
	private int currentUsedButton;

	private static final Border border3 = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.BLACK, 3),
			BorderFactory.createEmptyBorder(5, 5, 5, 5));
	private static final Border border5 = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.BLACK, 5),
			BorderFactory.createEmptyBorder(3, 3, 3, 3));

	public MainMenuLibraryPanel(int panelWidthPercentage,
			int verticalScrollBarWidthPercentage) {
		super(panelWidthPercentage, verticalScrollBarWidthPercentage, 5, 5, 10, 5);

		mainMenuLibraryController = new MainMenuLibraryController();
		scrollPanePanel.setLayout(new MigLayout(
				"nocache, center center, insets 10 0 0 0, gapy 10"));

		reuseButtonMap = new HashMap<>();
		currentUsedButton = 0;

	}

	/**
	 * @param obj
	 */
	public void addEntry(Object obj) {
		throw new UnsupportedOperationException();
	}

	public void refreshLibrary() {
		mainMenuLibraryController.setMainMenuLibraryPanel(this);
		mainMenuLibraryController.refreshLibrary();
		verticalScrollBar.setValue(0);
	}

	public void addStory(String id, String title) {
		JButton button = createButton();
		button.setText(title);
		button.setActionCommand(id);
		scrollPanePanel.add(button, "w 98%, wmax 98%, wrap");
	}

	private JButton createButton() {

		JButton button;
		if (currentUsedButton < reuseButtonMap.size()) {
			button = reuseButtonMap.get(currentUsedButton);
		} else {

			button = new AutoResizingButton();
			button.setFont(new Font("Arial", Font.BOLD, 30));
			button.setBorder(border3);
			button.setBackground(Color.WHITE);
			button.addActionListener(mainMenuLibraryController);
			button.setFocusPainted(false);

			button.getModel().addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					ButtonModel model = (ButtonModel) e.getSource();
					if (true == model.isRollover()) {
						button.setBorder(border5);
					} else if (false == model.isRollover()) {
						button.setBorder(border3);
					}
				}

			});

			reuseButtonMap.put(currentUsedButton, button);

		}

		currentUsedButton++;

		return button;

	}

	public void clearLibraryPanel() {
		scrollPanePanel.removeAll();
		currentUsedButton = 0;
	}

	public void setMainFrame(MainFrame mainFrame) {
		mainMenuLibraryController.setMainFrame(mainFrame);
	}

}
