import java.sql.SQLException;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.languagetool.gui.GrammarChecker;

import edu.stanford.nlp.pipeline.Annotation;
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
		MySQLConnector.getInstance().getConnection();

		Thread loadResources = new Thread() {

			@Override
			public void run() {

				long t = System.currentTimeMillis();

				StanfordCoreNLPInstance.getInstance()
						.annotate(new Annotation("Hello World!"));;
				DictionariesInstance.getInstance();
				AbstractSequenceClassifierInstance.getInstance();
				SenticNetParserInstance.getInstance();
				new GrammarChecker(new JTextArea());

				long t2 = System.currentTimeMillis();

				log.debug("---- done loading libraries in " + (t2 - t)
						+ " ms ----");

				System.out.println("---- done loading libraries in " + (t2 - t)
						+ " ms ----");

			}

		};
		loadResources.setPriority(Thread.MIN_PRIORITY);
		loadResources.start();

		MainFrame mainFrame = new MainFrame();
		mainFrame.getChooseModePanel().setMainFrame(mainFrame);
		mainFrame.getChooseFriendPanel().setMainFrame(mainFrame);
		mainFrame.getMainMenuPanel().setMainFrame(mainFrame);
		mainFrame.getViewStoryPanel().setMainFrame(mainFrame);
		mainFrame.setVisible(true);

	}

}
