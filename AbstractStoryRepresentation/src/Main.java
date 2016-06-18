import model.instance.AbstractSequenceClassifierInstance;
import model.instance.DictionariesInstance;
import model.instance.JLanguageToolInstance;
import model.instance.SenticNetParserInstance;
import model.instance.StanfordCoreNLPInstance;
import view.MainFrame;

public class Main {

	static int i;
	static int k;

	static synchronized int getNumber() {
		return k++;
	}

	public static void main(String[] args) {

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

		new Thread() {

			@Override
			public void run() {

				StanfordCoreNLPInstance.getInstance();
				DictionariesInstance.getInstance();
				AbstractSequenceClassifierInstance.getInstance();
				SenticNetParserInstance.getInstance();
				// JLanguageToolInstance.getInstance();

				System.out.println("---- done loading ----");
				
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
