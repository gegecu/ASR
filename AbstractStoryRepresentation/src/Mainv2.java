import java.sql.SQLException;

import org.apache.log4j.Logger;

import model.instance.AbstractSequenceClassifierInstance;
import model.instance.DictionariesInstance;
import model.instance.SenticNetParserInstance;
import model.instance.StanfordCoreNLPInstance;
import model.knowledge_base.MySQLConnector;
import view.MainFrame;

public class Mainv2 {

	static int i;
	static int k;

	private static Logger log = Logger.getLogger(Mainv2.class.getName());

	static synchronized int getNumber() {
		return k++;
	}

	public static void main(String[] args) throws SQLException {

		//new IdeaDialog().showDialog();
		MainFrame mainFrame = new MainFrame();
		mainFrame.getChooseModePanel().setMainFrame(mainFrame);
		mainFrame.getChooseFriendPanel().setMainFrame(mainFrame);
		mainFrame.getMainMenuPanel().setMainFrame(mainFrame);
		mainFrame.setVisible(true);

	}

}
