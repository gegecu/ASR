package model.instance;

import java.util.concurrent.Semaphore;

import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;

public class JLanguageToolInstance {

	private static Semaphore semaphore;
	private static JLanguageTool langTool;

	static {
		semaphore = new Semaphore(0);
		langTool = new JLanguageTool(new BritishEnglish());
		semaphore.release(1);
	}

	public static synchronized JLanguageTool getInstance() {
		try {
			semaphore.acquire();
			semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return langTool;
	}

}
