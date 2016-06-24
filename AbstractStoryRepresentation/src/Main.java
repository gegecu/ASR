import java.sql.SQLException;

import org.apache.log4j.Logger;

import model.instance.AbstractSequenceClassifierInstance;
import model.instance.DictionariesInstance;
import model.instance.SenticNetParserInstance;
import model.instance.StanfordCoreNLPInstance;
import model.knowledge_base.MySQLConnector;
import view.MainFrame;

public class Main {

	private static Logger log = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws SQLException {

		log.debug("\n\n");

		System.out.println("Checking Connection To Database ...");
//		MySQLConnector.getInstance().getConnection();

		new Thread() {

			@Override
			public void run() {

				long t = System.currentTimeMillis();

				StanfordCoreNLPInstance.getInstance();
				DictionariesInstance.getInstance();
				AbstractSequenceClassifierInstance.getInstance();
				SenticNetParserInstance.getInstance();
				// JLanguageToolInstance.getInstance();

				log.debug("---- done loading libraries in "
						+ (System.currentTimeMillis() - t) + " ms ----");

			}

		}.start();

		MainFrame mainFrame = new MainFrame();
		mainFrame.getChooseModePanel().setMainFrame(mainFrame);
		mainFrame.getChooseFriendPanel().setMainFrame(mainFrame);
		mainFrame.getMainMenuPanel().setMainFrame(mainFrame);
		mainFrame.setVisible(true);

	}

}