package controller.choosemode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;

import view.MainFrame;
import view.menu.choosemode.ChooseModePanel;
import view.mode.dialog.WaitDialog;

public class ChooseModeController implements ActionListener {

	private MainFrame mainFrame;
	private WaitDialog waitDialog;
	private Semaphore processed;

	public ChooseModeController() {
		processed = new Semaphore(0);
		waitDialog = new WaitDialog("Loading, Please Wait. =)");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				try {

					switch (e.getActionCommand()) {
						case ChooseModePanel.cancelButtonActionCommand :
							mainFrame.showMainMenu();
							break;
						case ChooseModePanel.beginnerButtonActionCommand :
							mainFrame.showBeginnerMode();
							break;
						case ChooseModePanel.advancedButtonActionCommand :
							mainFrame.showAdvancedMode();
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;

			}

			@Override
			protected void done() {

				try {
					processed.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				waitDialog.setVisible(false);

			}

		}.execute();

		processed.release(1);
		waitDialog.setVisible(true);

	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

}
