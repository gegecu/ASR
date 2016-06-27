import java.sql.SQLException;

import view.MainFrame;

public class Mainv2 {

	// Just for checking UI
	
	public static void main(String[] args) throws SQLException {

		//new IdeaDialog().showDialog();
		MainFrame mainFrame = new MainFrame();
		mainFrame.getChooseModePanel().setMainFrame(mainFrame);
		mainFrame.getChooseFriendPanel().setMainFrame(mainFrame);
		mainFrame.getMainMenuPanel().setMainFrame(mainFrame);
		mainFrame.setVisible(true);

	}

}
