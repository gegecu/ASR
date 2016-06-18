package controller.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.MainFrame;

public class CancelController implements ActionListener {

	private MainFrame mainFrame;

	@Override
	public void actionPerformed(ActionEvent e) {

		// alert sure delete all progress

		mainFrame.showMainMenu();

	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

}
