package model.instance;

import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;

/**
 * Singleton implementation of JLanguageTool
 */
public class JLanguageToolInstance {

	/**
	 * The singleton JLanguageTool instance
	 */
	private static JLanguageTool langTool;

	static {
		langTool = new JLanguageTool(new BritishEnglish());
	}

	/**
	 * Returns the singleton JLanguageTool instance
	 * 
	 * @return JLanguageTool instance
	 */
	public static synchronized JLanguageTool getInstance() {
		return langTool;
	}

}
