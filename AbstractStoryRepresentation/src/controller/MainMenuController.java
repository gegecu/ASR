package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.MainFrame;
import view.menu.mainmenu.MainMenuPanel;

public class MainMenuController implements ActionListener {

	private MainFrame mainFrame;

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
			case MainMenuPanel.writeStoryButtonActionCommand :
				mainFrame.showChooseModePanel();
				break;
			case MainMenuPanel.exitButtonActionCommand :
				System.exit(0);
				break;
			case MainMenuPanel.helpButtonActionCommand :
				break;
		}

	}

}
