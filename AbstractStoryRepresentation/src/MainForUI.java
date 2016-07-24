import java.sql.SQLException;

import javax.swing.JTextArea;

import org.languagetool.gui.GrammarChecker;

import view.MainFrame;
import view.mode.dialog.help.QuestionAnswerDialog;

public class MainForUI {

	// Just for checking UI

	public static void main(String[] args) throws SQLException {

		new QuestionAnswerDialog().setVisible(true);
		new GrammarChecker(new JTextArea());
		//new IdeaDialog().showDialog();
		MainFrame mainFrame = new MainFrame();
		mainFrame.getChooseModePanel().setMainFrame(mainFrame);
		mainFrame.getChooseFriendPanel().setMainFrame(mainFrame);
		mainFrame.getMainMenuPanel().setMainFrame(mainFrame);
		mainFrame.getViewStoryPanel().setMainFrame(mainFrame);
		mainFrame.setVisible(true);

	}

}
