import java.sql.SQLException;

import org.apache.log4j.Logger;

import model.instance.AbstractSequenceClassifierInstance;
import model.instance.DictionariesInstance;
import model.instance.SenticNetParserInstance;
import model.instance.StanfordCoreNLPInstance;
import model.knowledge_base.MySQLConnector;
import view.MainFrame;

public class Main {

	static int i;
	static int k;

	private static Logger log = Logger.getLogger(Main.class.getName());

	static synchronized int getNumber() {
		return k++;
	}

	public static void main(String[] args) throws SQLException {

		log.debug("\n\n");
		// BasicConfigurator.configure();

		//		k = 1;
		//
		//		for (i = 0; i < 10; i++) {
		//
		//			new Thread() {
		//
		//				Integer j = getNumber();
		//
		//				@Override
		//				public void run() {
		//
		//					AbstractSequenceClassifierInstance.getInstance();
		//
		//					System.out.println(j);
		//
		//				}
		//
		//			}.start();
		//
		//		}

		System.out.println("Checking Connection To Database ...");
		MySQLConnector.getInstance().getConnection();

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

		//new IdeaDialog().showDialog();
		MainFrame mainFrame = new MainFrame();
		mainFrame.getChooseModePanel().setMainFrame(mainFrame);
		mainFrame.getChooseFriendPanel().setMainFrame(mainFrame);
		mainFrame.getMainMenuPanel().setMainFrame(mainFrame);
		mainFrame.setVisible(true);

	}

}
